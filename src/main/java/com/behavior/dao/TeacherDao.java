package com.behavior.dao;

import com.behavior.pojo.Student;
import com.behavior.pojo.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface TeacherDao extends JpaRepository<Teacher,String>, JpaSpecificationExecutor<Teacher> {

    Teacher findTeacherByTeacherNumber(String teacherNumber);

}
