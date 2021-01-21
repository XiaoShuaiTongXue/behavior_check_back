package com.behavior.utils;

import com.behavior.dao.ClassesDao;
import com.behavior.dao.GradeDao;
import com.behavior.dao.SchoolDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.Scanner;

public class FileUtil {

    @Value("${shuai.blog.file.save-path}")
    public String filePath;

    @Autowired
    private  ClassesDao classesDao;

    @Autowired
    private GradeDao gradeDao;

    @Autowired
    private SchoolDao schoolDao;

    public static String getFilePath(String classId,String name){
        return null;
    }
}
