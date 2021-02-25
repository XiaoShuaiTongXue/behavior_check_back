package com.behavior.services;

import com.behavior.pojo.Teacher;
import com.behavior.reponse.ResponseResult;

public interface ITeacherService {
    ResponseResult register(Teacher teacher);

    ResponseResult doLogin( Teacher teacher);

    Teacher checkTeacher();

    ResponseResult getCourseName();

    ResponseResult beginSign(String courseName, int signTime,int truantTime);

    ResponseResult updateSign(String signID, int state);

    ResponseResult getSignInfo(String courseName);

    ResponseResult getCourseDate(String courseId);

    ResponseResult beginBehavior(String courseName,int type);

    ResponseResult stopBehavior(int type);

    ResponseResult getSignLate();

    ResponseResult getSignOut();

    ResponseResult getOnlineNow();

    ResponseResult getOnlineInfos(String courseName);

    ResponseResult getOnlineNowChartsData();

    ResponseResult getOnlineChartsData(String behaviorId);

    ResponseResult getTeacherInfo();
}
