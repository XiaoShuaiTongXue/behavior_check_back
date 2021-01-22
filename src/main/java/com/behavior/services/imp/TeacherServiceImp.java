package com.behavior.services.imp;

import com.behavior.dao.*;
import com.behavior.pojo.*;
import com.behavior.reponse.ResponseResult;
import com.behavior.reponse.ResponseState;
import com.behavior.services.ITeacherService;
import com.behavior.utils.*;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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

    @Autowired
    private OnlineCourseDao onlineCourseDao;

    @Autowired
    private OutlineCourseDao outlineCourseDao;

    @Autowired
    private BehaviorOnlineStudentDao behaviorOnlineStudentDao;

    @Autowired
    private BehaviorOutlineStudentDao behaviorOutlineStudentDao;
    /**
     * 注册
     * @param teacher 注册老师信息
     * @return
     */
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

    /**
     * 登录
     * @param teacher 老师登录信息
     * @return
     */
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

    /**
     * 检测并获取用户信息
     * @return
     */
    @Override
    public Teacher checkTeacher() {
        HttpServletRequest request = getServletRequest();
        String tokenKey = CookieUtil.getCookie(request, Constants.User.TEACHER_COOKIE_TOKEN_KEY);
        return getTeacherByTokenKey(tokenKey);
    }

    /**
     * 获取老师教的课程
     * @return
     */
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

    /**
     * 开始签到
     * @param courseName 课程名称
     * @param signTime 迟到截止时长（分）
     * @param truantTime 旷课截止时长（分）
     * @return
     */
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
        String courseId = courseDao.findCourseIdByCourseNameAndTeacherId(courseName, teacherId);
        SignRecord signRecord = new SignRecord();
        recordId = String.valueOf(idWorker.nextId());
        signRecord.setId(recordId);
        signRecord.setCourseId(courseId);
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
        List<String> classIds = courseDao.findClassIdsByCourseId(courseId);
        List<String> studentIds = studentDao.findIdsByClassIds(classIds);
        for (String studentId : studentIds) {
            SignStudent signStudent = new SignStudent();
            signStudent.setId(String.valueOf(idWorker.nextId()));
            signStudent.setStudentId(studentId);
            signStudent.setSignRecordId(recordId);
            signStudent.setSignState(Constants.SignState.TRUANT_STATE);
            signStudentDao.save(signStudent);
        }
        return ResponseResult.SUCCESS("开始签到").setData(recordId);
    }

    /**
     * 更新签到状态
     * @param signStudentID 学生签到ID
     * @param state 更新得签到
     * @return
     */
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

    /**
     * 获取签到信息
     * @param page 页码
     * @param size 每页的条数
     * @param courseName 课程名称
     * @param date 签到日期
     * @return
     */
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

    /**
     * 根据课程ID获取课程的所有签到日期
     * @param courseId 课程ID
     * @return
     */
    @Override
    public ResponseResult getCourseDate(String courseId) {
        List<String> signRecordDates = signRecordDao.findSignRecordsDateByCourseId(courseId);
        return ResponseResult.SUCCESS("获取日期成功").setData(signRecordDates);
    }

    @Override
    public ResponseResult beginBehavior(String courseName,int type) {
        Teacher teacher = checkTeacher();
        if (teacher == null) {
            return ResponseResult.FAILED("当前账号未登录，请登录后重试");
        }
        String courseId = courseDao.findCourseIdByCourseNameAndTeacherId(courseName, teacher.getId());
        List<String> classIds = courseDao.findClassIdsByCourseId(courseId);
        List<String> studentIds = studentDao.findIdsByClassIds(classIds);
        String behaviorId = String.valueOf(idWorker.nextId());
        if (type == Constants.Behavior.BEHAVIOR_ONLINE) {
            if (redisUtil.get(Constants.User.TEACHER_KEY_ONLINE + courseId) != null) {
                return ResponseResult.FAILED("该课程已经开启线上行为检测，无法重复开启");
            }
            OnlineCourse onlineCourse = new OnlineCourse();
            onlineCourse.setId(behaviorId);
            onlineCourse.setBehaviorStartTime(new Date());
            onlineCourse.setCourseId(courseId);
            onlineCourseDao.save(onlineCourse);
            for (String studentId : studentIds) {
                BehaviorOnlineStudent behaviorOnlineStudent = new BehaviorOnlineStudent();
                behaviorOnlineStudent.setId(String.valueOf(idWorker.nextId()));
                behaviorOnlineStudent.setStudentId(studentId);
                behaviorOnlineStudent.setBehaviorId(behaviorId);
                behaviorOnlineStudent.setPostTime(new Date());
                behaviorOnlineStudentDao.save(behaviorOnlineStudent);
            }
            redisUtil.set(Constants.User.TEACHER_KEY_ONLINE + courseId, behaviorId);
        }else if (type == Constants.Behavior.BEHAVIOR_OUTLINE){
            if (redisUtil.get(Constants.User.TEACHER_KEY_OUTLINE + courseId) != null) {
                return ResponseResult.FAILED("该课程已经开启线下行为检测，无法重复开启");
            }
            OutlineCourse outlineCourse = new OutlineCourse();
            outlineCourse.setId(behaviorId);
            outlineCourse.setBehaviorStartTime(new Date());
            outlineCourse.setCourseId(courseId);
            outlineCourseDao.save(outlineCourse);
            for (String studentId : studentIds) {
                BehaviorOutlineStudent behaviorOutlineStudent = new BehaviorOutlineStudent();
                behaviorOutlineStudent.setId(String.valueOf(idWorker.nextId()));
                behaviorOutlineStudent.setStudentId(studentId);
                behaviorOutlineStudent.setBehaviorId(behaviorId);
                behaviorOutlineStudent.setPostTime(new Date());
                behaviorOutlineStudentDao.save(behaviorOutlineStudent);
            }
            redisUtil.set(Constants.User.TEACHER_KEY_OUTLINE + courseId, behaviorId);
        }else {
            return ResponseResult.FAILED("类型错误");
        }
        return ResponseResult.SUCCESS(teacher.getName()+"-"+courseName+"课程:行为检测开始").setData(studentIds);
    }


    @Override
    public ResponseResult stopBehavior(String courseName,int type) {
        Teacher teacher = checkTeacher();
        if (teacher == null) {
            return ResponseResult.FAILED("当前账号未登录，请登录后重试");
        }
        String courseId = courseDao.findCourseIdByCourseNameAndTeacherId(courseName, teacher.getId());
        if (type == Constants.Behavior.BEHAVIOR_ONLINE){
            String onlineBehaviorId = (String) redisUtil.get(Constants.User.TEACHER_KEY_ONLINE + courseId);
            if (TextUtil.isEmpty(onlineBehaviorId)) {
                return ResponseResult.FAILED(teacher.getName()+"的"+courseName+"课程未开启线上行为检测");
            }
            onlineCourseDao.updateEndTimeById(new Date(),onlineBehaviorId);
            redisUtil.del(Constants.User.TEACHER_KEY_ONLINE + courseId);
        }else if(type == Constants.Behavior.BEHAVIOR_OUTLINE){
            String outlineBehaviorId = (String) redisUtil.get(Constants.User.TEACHER_KEY_OUTLINE + courseId);
            if (TextUtil.isEmpty(outlineBehaviorId)) {
                return ResponseResult.FAILED(teacher.getName()+"的"+courseName+"课程未开启线下行为检测");
            }
            outlineCourseDao.updateEndTimeById(new Date(),outlineBehaviorId);
            redisUtil.del(Constants.User.TEACHER_KEY_OUTLINE + courseId);
        }
        return ResponseResult.SUCCESS(teacher.getName()+"-"+courseName+"课程: 行为检测结束");
    }

    /**
     * 根据信息生成token
     * @param teacher 老师信息
     * @return
     */
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

    /**
     * 根据tokenKey检测和获取老师的信息
     * @param tokenKey
     * @return
     */
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
