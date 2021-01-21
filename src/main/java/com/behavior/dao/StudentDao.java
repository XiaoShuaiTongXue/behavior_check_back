package com.behavior.dao;

import com.behavior.pojo.Student;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface StudentDao extends JpaRepository<Student,String>, JpaSpecificationExecutor<Student> {

    Student findStudentById(String id);

    Student findUserByStudentNumber(String studentNumber);

    Student findStudentsByClassId(String classId);

    List<Student> findAllByClassIdIn(List<String>classIds);

    @Query(nativeQuery = true,value = "select `face_csv` from `tb_student` where id = ?")
    String findFaceCsvByStudentId(String id);
}
