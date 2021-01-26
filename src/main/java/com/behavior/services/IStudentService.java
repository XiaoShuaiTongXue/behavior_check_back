package com.behavior.services;

import com.behavior.pojo.OnlineStudent;
import com.behavior.pojo.OutlineStudent;
import com.behavior.reponse.ResponseResult;
import com.behavior.pojo.Student;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public interface IStudentService {
    ResponseResult register(Student student);

    ResponseResult doLogin(Student student);

    Student checkStudent();

    ResponseResult gesSigns(int page,int size,String state);

    ResponseResult sign();

//    ResponseResult postOutlineBehavior(OutlineStudent outlineStudent);

    ResponseResult postOnlineBehavior(OnlineStudent onlineStudent);

    ResponseResult findSign();

    ResponseResult findOnlineBehavior();

    ResponseResult getSchools();

    ResponseResult getOnlineBehaviors(int page, int size);
}
