package com.behavior.controller;

import com.behavior.pojo.Student;
import com.behavior.reponse.ResponseResult;
import com.behavior.services.IStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/test")
public class Test {

    @Autowired
    private IStudentService studentService;

    @GetMapping("/token")
    public ResponseResult checkToken(HttpServletRequest request){
        Student student = studentService.checkStudent();
        if (student == null) {
            return ResponseResult.FAILED("获取失败");
        }
        return ResponseResult.SUCCESS("解析成功").setData(student);
    }
}
