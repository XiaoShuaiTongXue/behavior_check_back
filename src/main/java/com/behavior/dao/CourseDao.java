package com.behavior.dao;

import com.behavior.pojo.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CourseDao extends JpaRepository<Course, String>, JpaSpecificationExecutor<Course> {


    List<Course> findCoursesByTeacherId(String teacherId);

    @Query(nativeQuery = true, value = "SELECT `id` FROM `tb_course` WHERE `course_name` = ? AND `teacher_id` = ?")
    String findCourseIdByCourseNameAndTeacherId(String courseName, String teacherId);

    Course findCourseByCourseNameAndTeacherId(String courseName, String teacherId);

    @Query(nativeQuery = true,value = "SELECT `class_id` FROM `tb_class_course` WHERE `course_id`  = ?")
    List<String> findClassIdsByCourseId(String courseId);
}
