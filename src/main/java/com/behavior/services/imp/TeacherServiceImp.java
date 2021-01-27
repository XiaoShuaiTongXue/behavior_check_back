package com.behavior.services.imp;

import com.behavior.dao.*;
import com.behavior.pojo.*;
import com.behavior.reponse.ResponseResult;
import com.behavior.reponse.ResponseState;
import com.behavior.services.ITeacherService;
import com.behavior.utils.*;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.scheduling.annotation.Scheduled;
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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
    private OnlineStudentDao onlineStudentDao;

    @Autowired
    private OutlineStudentDao outlineStudentDao;

    @Value("${shuai.blog.file.behavior-path}")
    private String behaviorFile;

    private static Map<String, String> onlineCourseIds = new HashMap<>();
    private static Map<String, String> outlineCourseIds = new HashMap<>();

    /**
     * 注册
     *
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
     *
     * @param teacher 老师登录信息
     * @return
     */
    @Override
    public ResponseResult doLogin(Teacher teacher) {
        HttpServletRequest request = getServletRequest();
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
        String tokenKey = CookieUtil.getCookie(request, Constants.User.TEACHER_COOKIE_TOKEN_KEY);
        if (tokenKey != null) {
            redisUtil.del(Constants.User.TEACHER_KEY_TOKEN + tokenKey);
        }
        createToken(teacherFromDb);
        return ResponseResult.Get(ResponseState.LOGIN_IN_SUCCESS);
    }

    /**
     * 检测并获取用户信息
     *
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
     *
     * @return
     */
    @Override
    public ResponseResult getCourseName() {
        Teacher teacher = checkTeacher();
        if (teacher == null) {
            return ResponseResult.FAILED("用户未登录或登录已经过期，请重新登录");
        }
        List<Course> courses = courseDao.findCoursesByTeacherId(teacher.getId());
        Map<String, String> courseMaps = new HashMap<>();
        for (Course course : courses) {
            courseMaps.put(course.getId(), course.getCourseName());
        }
        return ResponseResult.SUCCESS("获取课程成功").setData(courseMaps);
    }

    /**
     * 开始签到
     *
     * @param courseName 课程名称
     * @param signTime   迟到截止时长（分）
     * @param truantTime 旷课截止时长（分）
     * @return
     */
    @Override
    public ResponseResult beginSign(String courseName, int signTime, int truantTime) {
        Teacher teacher = checkTeacher();
        if (teacher == null) {
            return ResponseResult.FAILED("账号未登录");
        }
        String teacherId = teacher.getId();
        String recordId = (String) redisUtil.get(Constants.User.TEACHER_SIGN + teacherId);
        if (recordId != null) {
            SignRecord signRecordFromDb = signRecordDao.findSignRecordById(recordId);
            return ResponseResult.FAILED("当前签到未结束，请稍后后重试").setData(signRecordFromDb);
        }
        String courseId = courseDao.findCourseIdByCourseNameAndTeacherId(courseName, teacherId);
        List<String> classIds = courseDao.findClassIdsByCourseId(courseId);
        for (String classId : classIds){
            if (redisUtil.get(Constants.User.NORMAL_SIGN + classId) != null) {
                return ResponseResult.FAILED("该课程签到中存在班级冲突，更多信息请联系相关部门");
            }
        }
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
        calendar.add(Calendar.MINUTE, truantTime);
        signRecord.setOutEndTime(calendar.getTime());
        signRecordDao.save(signRecord);
        redisUtil.set(Constants.User.TEACHER_SIGN + teacherId, recordId,Constants.TimeValueByS.MIN*(signTime+truantTime));
        for (String classId : classIds) {
            redisUtil.set(Constants.User.NORMAL_SIGN + classId, recordId, Constants.TimeValueByS.MIN * signTime);
            redisUtil.set(Constants.User.LATER_SIGN + classId, recordId, Constants.TimeValueByS.MIN * truantTime);
        }
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
     *
     * @param signStudentID 学生签到ID
     * @param state         更新得签到
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
     *
     * @param page       页码
     * @param size       每页的条数
     * @param courseName 课程名称
     * @param date       签到日期
     * @return
     */
    @Override
    public ResponseResult getSignInfo(int page, int size, String courseName, String date) {
        Teacher teacher = checkTeacher();
        if (teacher == null) {
            return ResponseResult.FAILED("账号未登录");
        }
        if (TextUtil.isEmpty(courseName)) {
            return ResponseResult.FAILED("课程名称未选择");
        }
        String teacherId = teacher.getId();
        Pageable pageable = PageUtil.getPageable(page, size, null);
        String courseId = courseDao.findCourseIdByCourseNameAndTeacherId(courseName, teacherId);
        if (TextUtil.isEmpty(courseId)) {
            return ResponseResult.FAILED("课程名称错误");
        }
        Page<SignRecord> signRecords = signRecordDao.findAll(new Specification<SignRecord>() {
            @Override
            public Predicate toPredicate(Root<SignRecord> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Predicate coursePre = cb.equal(root.get("courseId").as(String.class), courseId);
                if (TextUtil.isEmpty(date)) {
                    return coursePre;
                }
                Predicate datePre = cb.equal(root.get("signStartTime").as(String.class), date);
                return cb.and(coursePre, datePre);
            }
        }, pageable);
        return ResponseResult.SUCCESS("查询成功").setData(signRecords);
    }

    /**
     * 根据课程ID获取课程的所有签到日期
     *
     * @param courseId 课程ID
     * @return
     */
    @Override
    public ResponseResult getCourseDate(String courseId) {
        List<String> signRecordDates = signRecordDao.findSignRecordsDateByCourseId(courseId);
        return ResponseResult.SUCCESS("获取日期成功").setData(signRecordDates);
    }

    @Override
    public ResponseResult getSignLate() {
        return getNowSign(Constants.SignState.LATE_STATE);
    }

    @Override
    public ResponseResult getSignOut() {
        return getNowSign(Constants.SignState.TRUANT_STATE);
    }

    private ResponseResult getNowSign(int state) {
        Teacher teacher = checkTeacher();
        if (teacher == null) {
            return ResponseResult.FAILED("当前用户未登录");
        }
        String teacherId = teacher.getId();
        Object signRecordId = redisUtil.get(Constants.User.TEACHER_SIGN + teacherId);
        if (signRecordId == null) {
            return ResponseResult.FAILED("未开始签到");
        }
        List<SignStudent> signStudents = signStudentDao.findAllBySignRecordIdAndSignState((String) signRecordId, state);
        return ResponseResult.SUCCESS("本堂课签到信息获取成功").setData(signStudents);
    }

    @Override
    public ResponseResult beginBehavior(String courseName, int type) {
        Teacher teacher = checkTeacher();
        if (teacher == null) {
            return ResponseResult.FAILED("当前账号未登录，请登录后重试");
        }
        String teacherId = teacher.getId();
        if (redisUtil.get(Constants.User.TEACHER_KEY_ONLINE + teacherId) != null) {
            return ResponseResult.FAILED("您已经开启一门课程的行为检测");
        }
        String courseId = courseDao.findCourseIdByCourseNameAndTeacherId(courseName, teacherId);
        if (courseId == null) {
            return ResponseResult.FAILED("课程不存在");
        }
        List<String> classIds = courseDao.findClassIdsByCourseId(courseId);
        List<String> studentIds = studentDao.findIdsByClassIds(classIds);
        String behaviorId = String.valueOf(idWorker.nextId());
        if (redisUtil.get(Constants.User.BEHAVIOR_KEY_ONLINE + courseId) != null ||
                redisUtil.get(Constants.User.BEHAVIOR_KEY_OUTLINE + courseId) != null) {
            return ResponseResult.FAILED("该课程已经开启线上或线下行为检测，无法重复开启");
        }
        for (String classId : classIds) {
            if (redisUtil.get(Constants.User.CLASS_KEY + classId) != null) {
                return ResponseResult.FAILED("当前课程存在班级冲突，了解更多信息请联系相关部门").setData(classId);
            }
        }
        redisUtil.set(Constants.User.TEACHER_KEY_ONLINE+teacherId,courseId,Constants.TimeValueByS.HOUR_2);
        for (String classId : classIds) {
            redisUtil.set(Constants.User.CLASS_KEY + classId, behaviorId, Constants.TimeValueByS.HOUR_2);
        }
        if (type == Constants.Behavior.BEHAVIOR_ONLINE) {
            OnlineCourse onlineCourse = new OnlineCourse();
            onlineCourse.setId(behaviorId);
            onlineCourse.setBehaviorStartTime(new Date());
            onlineCourse.setCourseId(courseId);
            onlineCourseDao.save(onlineCourse);
            for (String studentId : studentIds) {
                OnlineStudent onlineStudent = new OnlineStudent();
                onlineStudent.setId(String.valueOf(idWorker.nextId()));
                onlineStudent.setStudentId(studentId);
                onlineStudent.setBehaviorId(behaviorId);
                onlineStudent.setPostTime(new Date());
                onlineStudentDao.save(onlineStudent);
            }
            onlineCourseIds.put(courseId, behaviorId);
            redisUtil.set(Constants.User.BEHAVIOR_KEY_ONLINE + courseId, behaviorId, Constants.TimeValueByS.HOUR_2);
        } else if (type == Constants.Behavior.BEHAVIOR_OUTLINE) {
            OutlineCourse outlineCourse = new OutlineCourse();
            outlineCourse.setId(behaviorId);
            outlineCourse.setBehaviorStartTime(new Date());
            outlineCourse.setCourseId(courseId);
            outlineCourseDao.save(outlineCourse);
            for (String studentId : studentIds) {
                OutlineStudent outlineStudent = new OutlineStudent();
                outlineStudent.setId(String.valueOf(idWorker.nextId()));
                outlineStudent.setStudentId(studentId);
                outlineStudent.setBehaviorId(behaviorId);
                outlineStudent.setPostTime(new Date());
                outlineStudentDao.save(outlineStudent);
            }
            outlineCourseIds.put(courseId, behaviorId);
            redisUtil.set(Constants.User.BEHAVIOR_KEY_OUTLINE + courseId, behaviorId, Constants.TimeValueByS.HOUR_2);
        } else {
            return ResponseResult.FAILED("类型错误");
        }
        return ResponseResult.SUCCESS(teacher.getName() + "-" + courseName + "课程:行为检测开始").setData(studentIds);
    }

    /**
     * 停止行为检测，并刷新数据库和更新redis
     *
     * @param type
     * @return
     */
    public ResponseResult stopBehavior(int type) {
        Teacher teacher = checkTeacher();
        if (teacher == null) {
            return ResponseResult.FAILED("当前用户未登录");
        }
        String teacherId = teacher.getId();
        Object courseIdObj = redisUtil.get(Constants.User.TEACHER_KEY_ONLINE + teacherId);
        if (courseIdObj == null) {
            return ResponseResult.FAILED("未开启行为检测");
        }
        String courseId = (String) courseIdObj;
        List<String> classIds = courseDao.findClassIdsByCourseId(courseId);
        for (String classId : classIds) {
            redisUtil.del(Constants.User.CLASS_KEY + classId);
        }
        redisUtil.del(Constants.User.TEACHER_KEY_ONLINE + teacherId);
        if (type == Constants.Behavior.BEHAVIOR_ONLINE) {
            String behaviorId = (String) redisUtil.get(Constants.User.BEHAVIOR_KEY_ONLINE + courseId);
            onlineCourseDao.updateEndTimeById(new Date(), behaviorId);
            sumOnlineBehavior(behaviorId);
            OnlineCourse onlineCourseFromDb = onlineCourseDao.findOnlineCourseById(behaviorId);
            redisUtil.del(Constants.User.BEHAVIOR_KEY_ONLINE + courseId);
            onlineCourseIds.remove(courseId);
            return ResponseResult.SUCCESS("线上行为检测结束").setData(onlineCourseFromDb);
        } else if (type == Constants.Behavior.BEHAVIOR_OUTLINE) {
//            String behaviorId = (String) redisUtil.get(Constants.User.BEHAVIOR_KEY_OUTLINE + courseId);
//            outlineCourseDao.updateEndTimeById(new Date(), behaviorId);
//            sumOutlineBehavior(behaviorId);
//            redisUtil.del(Constants.User.BEHAVIOR_KEY_OUTLINE + courseId);
//            outlineCourseIds.remove(courseId);
        }
        return ResponseResult.SUCCESS("行为检测结束");
    }

    @Override
    public ResponseResult getOnlineNow() {
        ResponseResult result = getBehaviorIdFromRedis();
        if (result.isSuccess()) {
            String behaviorId = (String) result.getData();
            OnlineCourse onlineCourse = onlineCourseDao.findOnlineCourseById(behaviorId);
            return ResponseResult.SUCCESS("查询课上行为检测信息成功").setData(onlineCourse);
        }
        return result;
    }

    private ResponseResult getBehaviorIdFromRedis() {
        Teacher teacher = checkTeacher();
        if (teacher == null) {
            return ResponseResult.FAILED("当前用户未登录");
        }
        String teacherId = teacher.getId();
        Object courseObj = redisUtil.get(Constants.User.TEACHER_KEY_ONLINE + teacherId);
        if (courseObj == null) {
            return ResponseResult.FAILED("当前没有未开启线上行为检测");
        }
        String behaviorId = (String) redisUtil.get(Constants.User.BEHAVIOR_KEY_ONLINE + (String) courseObj);
        return ResponseResult.SUCCESS(behaviorId);
    }

    @Override
    public ResponseResult getOnlineInfos(int page, int size, String courseName) {
        Teacher teacher = checkTeacher();
        if (teacher == null) {
            return ResponseResult.FAILED("当前账号未登录");
        }
        Pageable pageable = PageUtil.getPageable(page, size, Sort.by(Sort.Direction.DESC, "behaviorStartTime"));
        Page<OnlineCourse> courses = onlineCourseDao.findAll((Specification<OnlineCourse>) (root, query, cb) -> {
            String courseId = courseDao.findCourseIdByCourseNameAndTeacherId(courseName, teacher.getId());
            if (!TextUtil.isEmpty(courseId)) {
                return cb.equal(root.get("courseId").as(String.class), courseId);
            }
            return null;
        }, pageable);
        return ResponseResult.SUCCESS("线下行为检测信息查询成功").setData(courses);
    }

    @Override
    public ResponseResult getOnlineNowChartsData() {
        ResponseResult result = getBehaviorIdFromRedis();
        if (result.isSuccess()) {
            String behaviorId = (String) result.getData();
            return getOnlineChartsData(behaviorId);
        }
        return result;
    }

    @Override
    public ResponseResult getOnlineChartsData(String behaviorId) {
        List<List<Integer>> lists = new ArrayList<>();
        String savePath = behaviorFile + File.separator + behaviorId+".txt";
        File file = new File(savePath);
        Scanner scanner = null;
        try {
            scanner = new Scanner(file);
        } catch (FileNotFoundException e) {
            return ResponseResult.FAILED("当前图表未生成，请稍后再试");
        }
        int[] lastData = new int[4];
        for (int i = 0; i < 4; i++) {
            List<Integer> data = new ArrayList<>();
            lists.add(data);
        }
        while (scanner.hasNext()) {
            int[] data = Arrays.stream(scanner.next().split(",")).mapToInt(Integer::parseInt).toArray();
            lists.get(0).add(data[0] - lastData[0]);
            lists.get(1).add(data[1] - lastData[1]);
            lists.get(2).add(data[2] - lastData[2]);
            lists.get(3).add(data[3] - lastData[3]);
            lastData = data;
        }
        String [] names = {"走神次数","瞌睡次数","说话次数","旷课次数"};
        List<Series> seriesList = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Series series = new Series();
            series.setName(names[1]);
            series.setData(lists.get(i));
            seriesList.add(series);
        }
        return ResponseResult.SUCCESS("获取图表成功").setData(seriesList);
    }

    private void sumOnlineBehavior(String onlineBehaviorId) {
        int sumLeaveCount = onlineStudentDao.getSumLeaveCount(onlineBehaviorId);
        int sumSleepCount = onlineStudentDao.getSumSleepCount(onlineBehaviorId);
        int sumTalkCount = onlineStudentDao.getSumTalkCount(onlineBehaviorId);
        int sumOutCount = onlineStudentDao.getSumOutCount(onlineBehaviorId);
        writeOnlineBehavior(onlineBehaviorId, sumLeaveCount, sumSleepCount, sumTalkCount, sumOutCount);
        OnlineCourse onlineCourseFromDb = onlineCourseDao.findOnlineCourseById(onlineBehaviorId);
        onlineCourseFromDb.addTalkCount(sumTalkCount);
        onlineCourseFromDb.addSleepCount(sumSleepCount);
        onlineCourseFromDb.addLeaveCount(sumLeaveCount);
        onlineCourseFromDb.addOutCount(sumOutCount);
        onlineCourseDao.save(onlineCourseFromDb);
    }

    private void writeOnlineBehavior(String onlineBehaviorId,
                                     int sumLeaveCount,
                                     int sumSleepCount,
                                     int sumTalkCount,
                                     int sumOutCount) {
        String savePath = behaviorFile + File.separator + onlineBehaviorId + ".txt";
        System.out.println(savePath);
        File behaviorFile = new File(savePath);
        try {
            if (!behaviorFile.exists()) {
                behaviorFile.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(behaviorFile, true);
            fos.write((sumLeaveCount + "," + sumSleepCount + "," + sumTalkCount + "," + sumOutCount + "\n")
                    .getBytes(StandardCharsets.UTF_8));
            fos.close();
        } catch (IOException e) {
            return;
        }
    }

//    private void sumOutlineBehavior(String outlineBehaviorId) {
//        int sumLookNoteCount = outlineStudentDao.getSumLookNoteCount(outlineBehaviorId);
//        int sumPassNoteCount = outlineStudentDao.getSumPassNoteCount(outlineBehaviorId);
//        int sumPhoneCount = outlineStudentDao.getSumPhoneCount(outlineBehaviorId);
//        int sumWriteCount = outlineStudentDao.getSumWriteCount(outlineBehaviorId);
//        int sumPutBagCount = outlineStudentDao.getSumPutBagCount(outlineBehaviorId);
//        writeOutlineBehavior(outlineBehaviorId, sumLookNoteCount, sumPassNoteCount, sumPhoneCount, sumWriteCount, sumPutBagCount);
//        OutlineCourse outlineCourseFromDb = outlineCourseDao.findOutlineCourseById(outlineBehaviorId);
//        outlineCourseFromDb.addLookNoteCount(sumLookNoteCount);
//        outlineCourseFromDb.addPassNoteCount(sumPassNoteCount);
//        outlineCourseFromDb.addPhoneCount(sumPhoneCount);
//        outlineCourseFromDb.addWriteCount(sumWriteCount);
//        outlineCourseFromDb.addPutBagCount(sumPutBagCount);
//        outlineCourseDao.save(outlineCourseFromDb);
//    }
//
//    private void writeOutlineBehavior(String outlineBehaviorId, int sumLookNoteCount, int sumPassNoteCount, int sumPhoneCount, int sumWriteCount, int sumPutBagCount) {
//        String savePath = behaviorFile + File.separator + outlineBehaviorId + ".txt";
//        System.out.println(savePath);
//        File behaviorFile = new File(savePath);
//        try {
//            if (!behaviorFile.exists()) {
//                behaviorFile.createNewFile();
//            }
//            FileOutputStream fos = new FileOutputStream(behaviorFile, true);
//            fos.write((sumLookNoteCount + "," + sumPassNoteCount + "," + sumPhoneCount + "," + sumWriteCount + "," + sumPutBagCount + "\n")
//                    .getBytes(StandardCharsets.UTF_8));
//            fos.close();
//        } catch (IOException e) {
//            System.out.println(e);
//            return;
//        }
//    }


    /**
     * 根据信息生成token
     *
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
    /**
     * 根据tokenKey检测和获取老师的信息
     *
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

    @Scheduled(fixedRate = Constants.TimeValueByMS.MIN_5)
    public void onlineTask() {
        if (onlineCourseIds.size() == 0) {
            System.out.println("当前没有执行的线上行为");
            return;
        }
        Set<String> courseSet = onlineCourseIds.keySet();
        for (String courseId : courseSet) {
            if (redisUtil.get(Constants.User.BEHAVIOR_KEY_ONLINE + courseId) == null) {
                String onlineBehaviorId = onlineCourseIds.get(courseId);
                onlineCourseDao.updateEndTimeById(new Date(), onlineBehaviorId);
                sumOnlineBehavior(onlineBehaviorId);
                onlineCourseIds.remove(courseId);
                return;
            }
            sumOnlineBehavior(onlineCourseIds.get(courseId));
        }
        System.out.println("线上任务行为保存成功：" + LocalDateTime.now());
    }

    private HttpServletResponse getServletResponse() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getResponse();
    }

    private HttpServletRequest getServletRequest() {
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return requestAttributes.getRequest();
    }

//    @Scheduled(fixedRate = Constants.TimeValueByMS.MIN_5)
//    public void outlineTask() {
//        if (outlineCourseIds.size() == 0) {
//            System.out.println("当前没有执行的线下行为");
//            return;
//        }
//        Set<String> courseSet = outlineCourseIds.keySet();
//        for (String courseId : courseSet) {
//            if (redisUtil.get(Constants.User.BEHAVIOR_KEY_OUTLINE + courseId) == null) {
//                String outlineBehaviorId = outlineCourseIds.get(courseId);
//                outlineCourseDao.updateEndTimeById(new Date(), outlineBehaviorId);
//                sumOnlineBehavior(outlineBehaviorId);
//                outlineCourseIds.remove(courseId);
//                return;
//            }
//            sumOnlineBehavior(outlineCourseIds.get(courseId));
//        }
//        System.out.println("线下任务行为保存成功：" + LocalDateTime.now());
//    }
}
