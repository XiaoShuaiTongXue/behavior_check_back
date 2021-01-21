package com.behavior.services.imp;

import com.behavior.dao.*;
import com.behavior.pojo.*;
import com.behavior.reponse.ResponseResult;
import com.behavior.reponse.ResponseState;
import com.behavior.services.ITeacherService;
import com.behavior.utils.*;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;
import java.util.*;


@Service
@Transactional
public class TeacherServiceImp implements ITeacherService {

    @Autowired
    private TeacherDao teacherDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private CourseDao courseDao;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private SignRecordDao signRecordDao;

    @Autowired
    private SignStudentDao signStudentDao;

    @Override
    public ResponseResult register(Teacher teacher) {
        String name = teacher.getName();
        String password = teacher.getPassword();
        String teacherNumber = teacher.getTeacherNumber();
        String sex = teacher.getSex();
        if (TextUtil.isEmpty(teacherNumber)) {
            return ResponseResult.FAILED("学号不能为空");
        }
        if (TextUtil.isEmpty(name)) {
            return ResponseResult.FAILED("姓名不能为空");
        }
        if (TextUtil.isEmpty(sex)) {
            return ResponseResult.FAILED("性别不能为空");
        }
        if (!TextUtil.judgeSex(sex)) {
            return ResponseResult.FAILED("性别输入错误");
        }
        if (TextUtil.isEmpty(password)) {
            return ResponseResult.FAILED("密码不能为空");
        }
        if (teacherDao.findTeacherByTeacherNumber(teacherNumber) != null) {
            return ResponseResult.FAILED("该学号已经注册");
        }
        teacher.setId(String.valueOf(idWorker.nextId()));
        teacher.setPassword(bCryptPasswordEncoder.encode(password));
        teacher.setAvatar(Constants.User.DEFAULT_AVATAR);
        teacherDao.save(teacher);
        return ResponseResult.SUCCESS("注册成功");
    }

    @Override
    public ResponseResult doLogin(Teacher teacher) {
        String teacherNumber = teacher.getTeacherNumber();
        String password = teacher.getPassword();
        if (TextUtil.isEmpty(teacherNumber)) {
            return ResponseResult.FAILED("工号不能为空");
        }
        if (TextUtil.isEmpty(password)) {
            return ResponseResult.FAILED("密码输入为空");
        }
        Teacher teacherFromDb = teacherDao.findTeacherByTeacherNumber(teacherNumber);
        if (teacherFromDb == null || !bCryptPasswordEncoder.matches(teacher.getPassword(), teacherFromDb.getPassword())) {
            return ResponseResult.FAILED("账号或密码错误");
        }
        HttpServletRequest request = getServletRequest();
        String tokenKey = CookieUtil.getCookie(request, Constants.User.TEACHER_COOKIE_TOKEN_KEY);
        if (tokenKey != null) {
            redisUtil.del(Constants.User.TEACHER_KEY_TOKEN + tokenKey);
        }
        createToken(teacherFromDb);
        return ResponseResult.Get(ResponseState.LOGIN_IN_SUCCESS);
    }

    @Override
    public Teacher checkTeacher() {
        HttpServletRequest request = getServletRequest();
        String tokenKey = CookieUtil.getCookie(request, Constants.User.TEACHER_COOKIE_TOKEN_KEY);
        return getTeacherByTokenKey(tokenKey);
    }

    @Override
    public ResponseResult getCourseName() {
        Teacher teacher = checkTeacher();
        if (teacher == null) {
            return ResponseResult.FAILED("用户未登录或登录已经过期，请重新登录");
        }
        List<Course> courses = courseDao.findCoursesByTeacherId(teacher.getId());
        Map<String,String> courseMaps = new HashMap<>();
        for (Course course : courses) {
            courseMaps.put(course.getId(),course.getCourseName());
        }
        return ResponseResult.SUCCESS("获取课程成功").setData(courseMaps);
    }

    @Override
    public ResponseResult beginSign(String courseName, int signTime,int truantTime) {
        Teacher teacher = checkTeacher();
        if (teacher == null) {
            return ResponseResult.FAILED("账号未登录");
        }
        String teacherId = teacher.getId();
        String recordId = (String) redisUtil.get(Constants.User.TEACHER_KEY_RECORD + teacherId);
        if (recordId != null) {
            return ResponseResult.FAILED("当前课前签到未结束，请稍后后重试").setData(recordId);
        }
        Course course = courseDao.findCourseByCourseNameAndTeacherId(courseName, teacherId);
        SignRecord signRecord = new SignRecord();
        recordId = String.valueOf(idWorker.nextId());
        signRecord.setId(recordId);
        signRecord.setCourseId(course.getId());
        Date startDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startDate);
        calendar.add(Calendar.MINUTE, signTime);
        signRecord.setSignStartTime(startDate);
        signRecord.setSignEndTime(calendar.getTime());
        calendar.setTime(startDate);
        calendar.add(Calendar.MINUTE,truantTime);
        signRecord.setOutEndTime(calendar.getTime());
        signRecordDao.save(signRecord);
        redisUtil.set(Constants.User.TEACHER_KEY_RECORD + teacherId, recordId, Constants.TimeValueByS.MIN * signTime);
        redisUtil.set(Constants.User.NORMAL_SIGN+recordId,1,Constants.TimeValueByS.MIN * signTime);
        redisUtil.set(Constants.User.LATER_SIGN+recordId,1,Constants.TimeValueByS.MIN * truantTime);
        List<String> classIds = new ArrayList<>();
        for (Classes mClass : course.getClasses()) {
            classIds.add(mClass.getId());
        }
        List<Student> students = studentDao.findAllByClassIdIn(classIds);
        for (Student student : students) {
            SignStudent signStudent = new SignStudent();
            signStudent.setId(String.valueOf(idWorker.nextId()));
            signStudent.setStudentId(student.getId());
            signStudent.setSignRecordId(recordId);
            signStudent.setSignState(Constants.SignState.TRUANT_STATE);
            signStudentDao.save(signStudent);
        }
        return ResponseResult.SUCCESS("开始签到").setData(recordId);
    }

    @Override
    public ResponseResult updateSign(String signStudentID, int state) {
        if (!TextUtil.judgeState(state)) {
            return ResponseResult.FAILED("状态输入不正确");
        }
        int result = signStudentDao.updateStateById(state, signStudentID);
        if (result > 0) {
            return ResponseResult.SUCCESS("学生签到信息修改完成");
        }
        return ResponseResult.SUCCESS("学生签到信息修改失败");
    }

    @Override
    public ResponseResult getSignInfo(int page, int size, String courseName, String date) {
        Teacher teacher = checkTeacher();
        if (teacher == null) {
            return ResponseResult.FAILED("账号未登录");
        }
        if (TextUtil.isEmpty(courseName)){
            return ResponseResult.FAILED("课程名称未选择");
        }
        String teacherId = teacher.getId();
        Pageable pageable = PageUtil.getPageable(page, size, null);
        String courseId = courseDao.findCourseIdByCourseNameAndTeacherId(courseName, teacherId);
        List<String> ids;
        if (TextUtil.isEmpty(date)) {
            ids = signRecordDao.findIdsByCourseId(courseId,pageable);
        }else {
            try {
                ids = signRecordDao.findIdsByCourseIdAndSignStartTime(courseId,TextUtil.formatDate(date),pageable);
            }catch (Exception e){
                return ResponseResult.FAILED("日期类型格式输入错误");
            }
        }
        List<SignStudent> signStudent = signStudentDao.findSignStudentsBySignRecordIdIn(ids);
        return ResponseResult.SUCCESS("查询成功").setData(signStudent);
    }

    @Override
    public ResponseResult getCourseDate(String courseId) {
        List<String> signRecordDates = signRecordDao.findSignRecordsDateByCourseId(courseId);
        return ResponseResult.SUCCESS("获取日期成功").setData(signRecordDates);
    }


    private String createToken(Teacher teacher) {
        HttpServletResponse response = getServletResponse();
        String token = JwtUtil.createToken(ClaimsUtils.teacher2Claims(teacher));
        String tokenKey = DigestUtils.md5DigestAsHex(token.getBytes());
        redisUtil.set(Constants.User.TEACHER_KEY_TOKEN + tokenKey, token, Constants.TimeValueByS.HOUR_2);
        CookieUtil.setUpCookie(response, Constants.User.TEACHER_COOKIE_TOKEN_KEY, tokenKey);
        return tokenKey;

    }

    private HttpServletRequest getServletRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest();
    }

    private HttpServletResponse getServletResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getResponse();
    }

    public Teacher getTeacherByTokenKey(String tokenKey) {
        String token = (String) redisUtil.get(Constants.User.TEACHER_KEY_TOKEN + tokenKey);
        if (token == null) {
            return null;
        }
        try {
            Claims claims = JwtUtil.parseJWT(token);
            return ClaimsUtils.claims2Teacher(claims);
        } catch (Exception e) {
            return null;
        }
    }
}
