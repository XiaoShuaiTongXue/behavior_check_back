package com.behavior.controller;

import com.behavior.pojo.Student;
import com.behavior.pojo.Teacher;
import com.behavior.reponse.ResponseResult;
import com.behavior.services.ITeacherService;
import com.behavior.utils.Constants;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

@Api("老师接口")
@CrossOrigin
@RestController
@RequestMapping("/teacher")
public class TeacherApi {

    @Autowired
    ITeacherService teacherService;

    @ApiOperation("老师注册")
    @PostMapping("/register")
    public ResponseResult register(@RequestBody Teacher teacher) {
        return teacherService.register(teacher);
    }

    @ApiOperation("老师登录")
    @PostMapping("/login")
    public ResponseResult doLogin(@RequestBody Teacher teacher) {
        return teacherService.doLogin(teacher);
    }

    @ApiOperation("获取老师教授的所有课程")
    @GetMapping("/course")
    public ResponseResult getCourseName() {
        return teacherService.getCourseName();
    }

    @ApiOperation("开始签到")
    @PostMapping("/sign")
    public ResponseResult beginSign(@RequestParam("courseName") String courseName,
                                    @RequestParam("signTime") int signTime,
                                    @RequestParam("truantTime") int truantTime) {
        return teacherService.beginSign(courseName, signTime, truantTime);
    }

    @ApiOperation("更新签到信息")
    @PostMapping("/update/sigin")
    public ResponseResult updateSign(@RequestParam("signID") String signID,
                                     @RequestParam("state") int state) {
        return teacherService.updateSign(signID, state);
    }

    @ApiOperation("获取签到信息")
    @GetMapping("/sign/{page}/{size}")
    public ResponseResult getSignInfo(@PathVariable("page") int page,
                                      @PathVariable("size") int size,
                                      @RequestParam(value = "courseName") String courseName,
                                      @RequestParam(value = "date", required = false) String date) {
        return teacherService.getSignInfo(page, size, courseName, date);
    }

    @ApiOperation("获取指定课程的所有签到日期")
    @GetMapping("/sign/date")
    public ResponseResult getCourseDate(@RequestParam("courseId") String courseId) {
        return teacherService.getCourseDate(courseId);
    }

    @ApiOperation("开始课上行为检测")
    @PostMapping("/online/begin")
    public ResponseResult beginOnlineBehavior(@RequestParam("courseName") String courseName) {
        return teacherService.beginBehavior(courseName, Constants.Behavior.BEHAVIOR_ONLINE);
    }

    @ApiOperation("停止课上行为检测")
    @PostMapping("/online/stop")
    public ResponseResult stopOnlineBehavior(@RequestParam("courseName") String courseName) {
        return teacherService.stopBehavior(courseName,Constants.Behavior.BEHAVIOR_ONLINE);
    }

//    @ApiOperation("开始课下行为检测")
//    @PostMapping("/outline/behavior")
//    public ResponseResult beginOutlineBehavior(@RequestParam("courseName") String courseName) {
//        return teacherService.beginBehavior(courseName,Constants.Behavior.BEHAVIOR_OUTLINE);
//    }
//
//    @ApiOperation("停止课下行为检测")
//    @PostMapping("/outline/stop")
//    public ResponseResult stopOutlineBehavior(@RequestParam("courseName") String courseName) {
//        return teacherService.stopBehavior(courseName,Constants.Behavior.BEHAVIOR_OUTLINE);
//    }
}
