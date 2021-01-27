package com.behavior.controller;

import com.behavior.pojo.OnlineStudent;
import com.behavior.pojo.Student;
import com.behavior.reponse.ResponseResult;
import com.behavior.services.IStudentService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api("学生接口")
@CrossOrigin("*")
@RestController
@RequestMapping("/student")
public class StudentApi {

    @Autowired
    private IStudentService studentService;

    @ApiOperation("学生注册")
    @PostMapping("/register")
    public ResponseResult register(@RequestBody Student student) {
        return studentService.register(student);
    }

    @ApiOperation("学生登录")
    @PostMapping("/login")
    public ResponseResult doLogin(@RequestBody Student student) {
        return studentService.doLogin(student);
    }

    @ApiOperation("获取所有的学校")
    @GetMapping("/schools")
    public ResponseResult findSchools() {
        return studentService.getSchools();
    }

//    @ApiOperation("获取指定学校的年级")
//    @GetMapping("/grades")
//    public ResponseResult findGrades(@RequestParam("schoolId")String schoolId){
//        return studentService.getGrades(schoolId);
//    }
//
//    @ApiOperation("获取指定年级的班级")
//    @GetMapping("/classes")
//    public ResponseResult findClasses(@RequestParam("gradeId")String gradeId){
//        return studentService.getClasses(gradeId);
//    }

//    @PostMapping("/face")
//    public ResponseResult enterFaceCsv(@Param("student_num") String studentNum,
//                                       @Param("face_csv") String faceCsv) {
//        return studentService.enterFaceCsv(studentNum, faceCsv);
//    }
//
//    @GetMapping("/face_csv")
//    public ResponseResult getFaceCsv(){
//        return studentService.getFaceCsv();
//    }

    @ApiOperation("获取学生签到信息")
    @GetMapping("/signs/{page}/{size}")
    public ResponseResult getSigns(@PathVariable("page") int page,
                                   @PathVariable("size") int size,
                                   @RequestParam(value = "state", required = false) String state) {
        return studentService.gesSigns(page, size, state);
    }


    @ApiOperation("签到")
    @PostMapping("/sign")
    public ResponseResult sign() {
        return studentService.sign();
    }

    @ApiOperation("查询当前可用签到")
    @GetMapping("/sign")
    public ResponseResult findSign() {
        return studentService.findSign();
    }

    @ApiOperation("提交课上行为检测数据")
    @PostMapping("/online/behavior")
    public ResponseResult postOnlineBehavior(@RequestBody OnlineStudent onlineStudent) {
        return studentService.postOnlineBehavior(onlineStudent);
    }

    @ApiOperation("查询当前可用课上行为检测")
    @GetMapping("/online/behavior")
    public ResponseResult findOnlineBehavior() {
        return studentService.findOnlineBehavior();
    }

    @ApiOperation("获取学生课上行为检测信息")
    @GetMapping("/online/behavior/{page}/{size}")
    public ResponseResult getOnlineBehaviors(@PathVariable("page") int page,
                                             @PathVariable("size") int size) {
        return studentService.getOnlineBehaviors(page, size);
    }

//    @ApiOperation("提交课下行为检测数据")
//    @PostMapping("/outline/behavior")
//    public ResponseResult postOutlineBehavior(@Body OutlineStudent outlineStudent){
//        return studentService.postOutlineBehavior(outlineStudent);
//    }
}
