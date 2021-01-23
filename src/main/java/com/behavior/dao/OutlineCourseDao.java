package com.behavior.dao;

import com.behavior.pojo.OutlineCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface OutlineCourseDao extends JpaRepository<OutlineCourse,String>, JpaSpecificationExecutor<OutlineCourse> {

    @Modifying
    @Query(nativeQuery = true,value = "UPDATE `tb_behavior_outline_course` SET `behavior_end_time` = ? WHERE `id` = ?")
    int updateEndTimeById (Date endTime, String id);

    OutlineCourse findOutlineCourseById(String id);
}
