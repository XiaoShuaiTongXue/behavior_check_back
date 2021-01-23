package com.behavior.services.imp;

import com.behavior.dao.*;
import com.behavior.pojo.*;
import com.behavior.reponse.ResponseResult;
import com.behavior.reponse.ResponseState;
import com.behavior.services.IStudentService;
import com.behavior.utils.*;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
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
import java.io.File;
import java.util.Date;

@Service
@Transactional
public class StudentServiceImp implements IStudentService {

    @Autowired
    private StudentDao studentDao;

    @Autowired
    private IdWorker idWorker;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private ClassesDao classesDao;

    @Autowired
    private SignStudentDao signStudentDao;

    @Autowired
    private OnlineStudentDao onlineStudentDao;

    @Autowired
    private OutlineStudentDao outlineStudentDao;

    @Value("${shuai.blog.file.save-path}")
    public String basePath;


    @Override
    public ResponseResult register(Student student) {
        String name = student.getName();
        String password = student.getPassword();
        String studentNumber = student.getStudentNumber();
        String sex = student.getSex();
        String classId = student.getClassId();
        if (TextUtil.isEmpty(studentNumber)) {
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
        if (studentDao.findUserByStudentNumber(studentNumber) != null) {
            return ResponseResult.FAILED("该学号已经注册");
        }
        if (TextUtil.isEmpty(classId)) {
            return ResponseResult.FAILED("班级未选择");
        }
        String className = getClassName(classId);
        student.setClassName(className);
        String filePath = getFilePath(className, name);
        student.setBehaviorPath(filePath);
        student.setId(String.valueOf(idWorker.nextId()));
        student.setPassword(bCryptPasswordEncoder.encode(password));
        student.setAvatar(Constants.User.DEFAULT_AVATAR);
        studentDao.save(student);
        return ResponseResult.SUCCESS("注册成功");
    }


    @Override
    public ResponseResult doLogin(Student student) {
        String studentNumber = student.getStudentNumber();
        String password = student.getPassword();
        if (TextUtil.isEmpty(studentNumber)) {
            return ResponseResult.FAILED("学号不能为空");
        }
        if (TextUtil.isEmpty(password)) {
            return ResponseResult.FAILED("密码输入为空");
        }
        Student studentFromDb = studentDao.findUserByStudentNumber(studentNumber);
        if (studentFromDb == null || !bCryptPasswordEncoder.matches(student.getPassword(), studentFromDb.getPassword())) {
            return ResponseResult.FAILED("账号或密码错误");
        }
        HttpServletRequest request = getServletRequest();
        String tokenKey = CookieUtil.getCookie(request, Constants.User.STUDENT_COOKIE_TOKEN_KEY);
        if (tokenKey != null) {
            redisUtil.del(Constants.User.STUDENT_KEY_TOKEN + tokenKey);
        }
        createToken(studentFromDb);
        return ResponseResult.Get(ResponseState.LOGIN_IN_SUCCESS);
    }

//    @Override
//    public ResponseResult enterFaceCsv(String studentNum, String faceCsv) {
//        Student studentFromDb = studentDao.findUserByStudentNumber(studentNum);
//        if (studentFromDb == null) {
//            return ResponseResult.FAILED("用户学号错误");
//        }
//        if (faceCsv == null) {
//            return ResponseResult.FAILED("人脸数据为空");
//        }
//        studentFromDb.setFaceCsv(faceCsv);
//        studentDao.save(studentFromDb);
//        return ResponseResult.SUCCESS("人脸数据录入失败");
//    }
//
//        @Override
//    public ResponseResult getFaceCsv() {
//        Student student = checkStudent();
//        if (student == null) {
//            return ResponseResult.FAILED("用户未登录，请登录后重试");
//        }
//        String faceCsv = studentDao.findFaceCsvByStudentId(student.getId());
//        if (TextUtil.isEmpty(faceCsv)) {
//            return ResponseResult.FAILED("人脸信息未录入，请先录入人脸数据");
//        }
//        return ResponseResult.SUCCESS("人脸信息查询成功").setData(faceCsv);
//    }

    @Override
    public ResponseResult gesSigns(int page, int size, String state) {
        Student student = checkStudent();
        if (student == null) {
            return ResponseResult.FAILED("学生未登录，请登录后重试");
        }
        Page<SignStudent> signStudents = signStudentDao.findAll(new Specification<SignStudent>() {
            @Override
            public Predicate toPredicate(Root<SignStudent> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate studentPre = cb.equal(root.get("studentId").as(String.class), student.getId());
                if (!TextUtil.isEmpty(state) && TextUtil.judgeState(Integer.parseInt(state))) {
                    Predicate statePre = cb.equal(root.get("sign_state").as(int.class), Integer.parseInt(state));
                    return cb.and(studentPre, statePre);
                }
                return studentPre;
            }
        }, PageUtil.getPageable(page, size, Sort.by(Sort.Direction.DESC, "out_end_time")));
        return ResponseResult.SUCCESS("学生个人签到信息查询成功").setData(signStudents);
    }

    @Override
    public ResponseResult sign(String signStudentId) {
        SignStudent signStudent = signStudentDao.findSignStudentById(signStudentId);
        if (signStudent == null) {
            return ResponseResult.FAILED("学生个人签到ID错误");
        }
        String signRecordId = signStudent.getSignRecordId();
        if (redisUtil.get(Constants.User.NORMAL_SIGN + signRecordId) != null) {
            signStudentDao.deleteById(signStudentId);
            return ResponseResult.SUCCESS("签到成功");
        }
        if (redisUtil.get(Constants.User.LATER_SIGN + signRecordId) != null) {
            signStudentDao.updateStateById(Constants.SignState.LATE_STATE, signStudentId);
            return ResponseResult.SUCCESS("签到成功,你已经迟到，但是没有超过旷课时间");
        }
        return ResponseResult.FAILED("无法签到");
    }


    @Override
    public ResponseResult postOutlineBehavior(OutlineStudent outlineStudent) {
        Student student = checkStudent();
        if (student == null) {
            return ResponseResult.FAILED("用户未登录，请登录后重试");
        }
        String classId = student.getClassId();
        String studentId = student.getId();
        Object behaviorId = redisUtil.get(Constants.User.CLASS_KEY + classId);
        if (behaviorId == null) {
            return ResponseResult.FAILED("该班级未开始课下行为检测");
        }
        OutlineStudent outlineStudentFromDb = outlineStudentDao
                .findOutlineStudentByStudentIdAndBehaviorId(studentId, (String) behaviorId);
        int lookNoteCount = outlineStudent.getLookNoteCount();
        int phoneCount = outlineStudent.getPhoneCount();
        int writeCount = outlineStudent.getWriteCount();
        int passNoteCount = outlineStudent.getPassNoteCount();
        int putBagCount = outlineStudent.getPutBagCount();
        if (lookNoteCount > 0) {
            outlineStudentFromDb.addLookNoteCount(lookNoteCount);
        }
        if (phoneCount > 0) {
            outlineStudentFromDb.addPhoneCount(phoneCount);
        }
        if (writeCount > 0) {
            outlineStudentFromDb.addWriteCount(writeCount);
        }
        if (passNoteCount > 0) {
            outlineStudentFromDb.addPassNoteCount(passNoteCount);
        }
        if (putBagCount > 0) {
            outlineStudentFromDb.addPutBagCount(putBagCount);
        }
        outlineStudentFromDb.setPostTime(new Date());
        outlineStudentDao.save(outlineStudentFromDb);
        return ResponseResult.SUCCESS("线下学生行为更新成功");
    }

    @Override
    public ResponseResult postOnlineBehavior(OnlineStudent onlineStudent) {
        Student student = checkStudent();
        if (student == null) {
            return ResponseResult.FAILED("用户未登录，请登录后重试");
        }
        String studentId = student.getId();
        String classId = student.getClassId();
        Object behaviorId = redisUtil.get(Constants.User.CLASS_KEY + classId);
        if (behaviorId == null) {
            return ResponseResult.FAILED("该班级未开始课上行为检测");
        }
        OnlineStudent onlineStudentFromDb = onlineStudentDao
                .findOnlineStudentByStudentIdAndBehaviorId(studentId, (String) behaviorId);
        int sleepCount = onlineStudent.getSleepCount();
        int leaveCount = onlineStudent.getLeaveCount();
        int talkCount = onlineStudent.getTalkCount();
        int outCount = onlineStudent.getOutCount();
        if (sleepCount > 0) {
            onlineStudentFromDb.addSleepCount(sleepCount);
        }
        if (leaveCount > 0) {
            onlineStudentFromDb.addLeaveCount(leaveCount);
        }
        if (talkCount > 0) {
            onlineStudentFromDb.addTalkCount(talkCount);
        }
        if (outCount > 0) {
            onlineStudentFromDb.addOutCount(outCount);
        }
        onlineStudentFromDb.setPostTime(new Date());
        onlineStudentDao.save(onlineStudentFromDb);
        return ResponseResult.SUCCESS("线上学生行为更新成功");
    }

    @Override
    public Student checkStudent() {
        HttpServletRequest request = getServletRequest();
        String tokenKey = CookieUtil.getCookie(request, Constants.User.STUDENT_COOKIE_TOKEN_KEY);
        Student student = getStudentByTokenKey(tokenKey);
        return student;

    }


    private String createToken(Student student) {
        HttpServletResponse response = getServletResponse();
        String token = JwtUtil.createToken(ClaimsUtils.student2Claims(student));
        String tokenKey = DigestUtils.md5DigestAsHex(token.getBytes());
        redisUtil.set(Constants.User.STUDENT_KEY_TOKEN + tokenKey, token, Constants.TimeValueByS.HOUR_2);
        CookieUtil.setUpCookie(response, Constants.User.STUDENT_COOKIE_TOKEN_KEY, tokenKey);
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

    public Student getStudentByTokenKey(String tokenKey) {
        String token = (String) redisUtil.get(Constants.User.STUDENT_KEY_TOKEN + tokenKey);
        if (token == null) {
            return null;
        }
        try {
            Claims claims = JwtUtil.parseJWT(token);
            return ClaimsUtils.claims2Student(claims);
        } catch (Exception e) {
            return null;
        }
    }

    public String getClassName(String classId) {
        Classes classInfo = classesDao.findClassesById(classId);
        String classNo = classInfo.getClassNo();
        String gradeName = classInfo.getGrade().getGradeName();
        String schoolName = classInfo.getGrade().getSchool().getSchoolName();
        return schoolName + "-" + gradeName + "-" + classNo;
    }

    public String getFilePath(String className, String name) {
        String[] split = className.split("-");
        String filePath = basePath + File.separator
                + split[0] + File.separator
                + split[1] + File.separator
                + split[2] + File.separator
                + name;
        File studentFile = new File(filePath);
        if (!studentFile.exists()) {
            studentFile.mkdirs();
        }
        return filePath;
    }
}
