package com.behavior.controller;

import com.behavior.pojo.Student;
import com.behavior.reponse.ResponseResult;
import com.behavior.services.IStudentService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@CrossOrigin
@RestController
@RequestMapping("/student")
public class StudentApi {

    @Autowired
    private IStudentService userService;

    @PostMapping("/register")
    public ResponseResult register(@RequestBody Student student) {
        return userService.register(student);
    }

    @PostMapping("/login")
    public ResponseResult doLogin(@RequestBody Student student) {
        return userService.doLogin(student);
    }

    @PostMapping("/face")
    public ResponseResult enterFaceCsv(@RequestParam("student_num") String studentNum,
                                       @RequestParam("face_csv") String faceCsv) {
        return userService.enterFaceCsv(studentNum, faceCsv);
    }

    @GetMapping("/face_csv")
    public ResponseResult getFaceCsv(){
        return userService.getFaceCsv();
    }

    @GetMapping("/signs/{page}/{size}")
    public ResponseResult getSigns(@PathVariable("page") int page,
                                   @PathVariable("size") int size,
                                   @RequestParam(value = "state", required = false) String state) {
        return userService.gesSigns(page,size,state);
    }


    @GetMapping("/sign")
    public ResponseResult sign(@RequestParam("signStudentId")String signStudentId){
        return userService.sign(signStudentId);
    }
}
