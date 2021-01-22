package com.behavior.controller;

import com.behavior.pojo.Student;
import com.behavior.pojo.Teacher;
import com.behavior.reponse.ResponseResult;
import com.behavior.services.ITeacherService;
import com.behavior.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@CrossOrigin
@RestController
@RequestMapping("/teacher")
public class TeacherApi {

    @Autowired
    ITeacherService teacherService;

    @PostMapping("/register")
    public ResponseResult register(@RequestBody Teacher teacher) {
        return teacherService.register(teacher);
    }

    @PostMapping("/login")
    public ResponseResult doLogin(@RequestBody Teacher teacher) {
        return teacherService.doLogin(teacher);
    }

    @GetMapping("/course")
    public ResponseResult getCourseName() {
        return teacherService.getCourseName();
    }

    @PostMapping("/sign")
    public ResponseResult beginSign(@RequestParam("courseName") String courseName,
                                    @RequestParam("signTime") int signTime,
                                    @RequestParam("truantTime") int truantTime) {
        return teacherService.beginSign(courseName, signTime, truantTime);
    }

    /**
     * 改签，学号，修改内容（正常，事假）
     */
    @PostMapping("/update/sigin")
    public ResponseResult updateSign(@RequestParam("signID") String signID,
                                     @RequestParam("state") int state) {
        return teacherService.updateSign(signID, state);
    }

    @GetMapping("/sign/{page}/{size}")
    public ResponseResult getSignInfo(@PathVariable("page") int page,
                                      @PathVariable("size") int size,
                                      @RequestParam(value = "courseName") String courseName,
                                      @RequestParam(value = "date", required = false) String date) {
        return teacherService.getSignInfo(page, size, courseName, date);
    }

    @GetMapping("/sign/date")
    public ResponseResult getCourseDate(@RequestParam("courseId") String courseId) {
        return teacherService.getCourseDate(courseId);
    }

    /**
     * 课上行为检测
     */
    @PostMapping("/online/begin")
    public ResponseResult beginOnlineBehavior(@RequestParam("courseName") String courseName) {
        return teacherService.beginBehavior(courseName, Constants.Behavior.BEHAVIOR_ONLINE);
    }

    @PostMapping("/online/stop")
    public ResponseResult stopOnlineBehavior(@RequestParam("courseName") String courseName) {
        return teacherService.stopBehavior(courseName,Constants.Behavior.BEHAVIOR_ONLINE);
    }

    @PostMapping("/outline/behavior")
    public ResponseResult beginOutlineBehavior(@RequestParam("courseName") String courseName) {
        return teacherService.beginBehavior(courseName,Constants.Behavior.BEHAVIOR_OUTLINE);
    }

    @PostMapping("/outline/stop")
    public ResponseResult stopOutlineBehavior(@RequestParam("courseName") String courseName) {
        return teacherService.stopBehavior(courseName,Constants.Behavior.BEHAVIOR_OUTLINE);
    }
}
