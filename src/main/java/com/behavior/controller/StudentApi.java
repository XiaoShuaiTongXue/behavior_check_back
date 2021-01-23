package com.behavior.controller;

import com.behavior.pojo.OnlineStudent;
import com.behavior.pojo.OutlineStudent;
import com.behavior.pojo.Student;
import com.behavior.reponse.ResponseResult;
import com.behavior.services.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/student")
public class StudentApi {

    @Autowired
    private IStudentService studentService;

    @PostMapping("/register")
    public ResponseResult register(@RequestBody Student student) {
        return studentService.register(student);
    }

    @PostMapping("/login")
    public ResponseResult doLogin(@RequestBody Student student) {
        return studentService.doLogin(student);
    }

//    @PostMapping("/face")
//    public ResponseResult enterFaceCsv(@RequestParam("student_num") String studentNum,
//                                       @RequestParam("face_csv") String faceCsv) {
//        return studentService.enterFaceCsv(studentNum, faceCsv);
//    }
//
//    @GetMapping("/face_csv")
//    public ResponseResult getFaceCsv(){
//        return studentService.getFaceCsv();
//    }

    @GetMapping("/signs/{page}/{size}")
    public ResponseResult getSigns(@PathVariable("page") int page,
                                   @PathVariable("size") int size,
                                   @RequestParam(value = "state", required = false) String state) {
        return studentService.gesSigns(page,size,state);
    }


    @PostMapping("/sign")
    public ResponseResult sign(@RequestParam("signStudentId")String signStudentId){
        return studentService.sign(signStudentId);
    }

    @PostMapping("/online/behavior")
    public ResponseResult postOnlineBehavior(@RequestBody OnlineStudent onlineStudent){
        return studentService.postOnlineBehavior(onlineStudent);
    }

    @PostMapping("/outline/behavior")
    public ResponseResult postOutlineBehavior(@RequestBody OutlineStudent outlineStudent){
        return studentService.postOutlineBehavior(outlineStudent);
    }
}
