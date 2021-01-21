package com.behavior.utils;

import com.behavior.pojo.Classes;
import com.behavior.pojo.Student;
import com.behavior.pojo.Teacher;
import io.jsonwebtoken.Claims;

import java.util.HashMap;
import java.util.Map;

public class ClaimsUtils {
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String SEX = "sex";
    private static final String SN = "studentNumber";
    private static final String TN = "teacherNumber";
    private static final String AVATAR = "avatar";
    private static final String CLASS_ID = "class_id";
    private static final String CLASS_NAME = "class_name";
    private static final String BEHAVIOR_PATH = "behavior_path";

    public static Map<String,Object> student2Claims(Student student){
        Map<String,Object> claims = new HashMap<>();
        claims.put(ID,student.getId());
        claims.put(NAME,student.getName());
        claims.put(SEX,student.getSex());
        claims.put(SN,student.getStudentNumber());
        claims.put(AVATAR,student.getAvatar());
        claims.put(CLASS_ID,student.getClassId());
        claims.put(BEHAVIOR_PATH,student.getBehaviorPath());
        claims.put(CLASS_NAME,student.getClassName());
        return claims;
    }

    public static Student claims2Student(Claims claims){
        Student student = new Student();
        student.setId((String) claims.get(ID));
        student.setName((String) claims.get(NAME));
        student.setSex((String) claims.get(SEX));
        student.setStudentNumber((String) claims.get(SN));
        student.setAvatar((String) claims.get(AVATAR));
        student.setClassId((String) claims.get(CLASS_ID));
        student.setBehaviorPath((String) claims.get(BEHAVIOR_PATH));
        student.setClassName((String) claims.get(CLASS_NAME));
        return student;
    }

    public static Map<String,Object> teacher2Claims(Teacher teacher){
        Map<String,Object> claims = new HashMap<>();
        claims.put(ID,teacher.getId());
        claims.put(NAME,teacher.getName());
        claims.put(SEX,teacher.getSex());
        claims.put(AVATAR,teacher.getAvatar());
        claims.put(TN,teacher.getTeacherNumber());
        return claims;
    }

    public static Teacher claims2Teacher(Claims claims){
        Teacher teacher = new Teacher();
        teacher.setId((String) claims.get(ID));
        teacher.setName((String) claims.get(NAME));
        teacher.setSex((String) claims.get(SEX));
        teacher.setTeacherNumber((String) claims.get(TN));
        teacher.setAvatar((String) claims.get(AVATAR));
        return teacher;
    }
}
