package com.behavior.services;

import com.behavior.reponse.ResponseResult;
import com.behavior.pojo.Student;
import io.swagger.annotations.Api;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;


public interface IStudentService {
    ResponseResult register(Student student);

    ResponseResult doLogin(Student student);

    Student checkStudent();


    ResponseResult enterFaceCsv(String studentNum,String faceCsv);

    ResponseResult gesSigns(int page,int size,String state);

    ResponseResult sign(String signStudentId);

    ResponseResult getFaceCsv();
}
