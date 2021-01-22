package com.behavior.dao;

import com.behavior.pojo.OnlineCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;

public interface OnlineCourseDao extends JpaRepository<OnlineCourse,String>, JpaSpecificationExecutor<OnlineCourse> {

    @Modifying
    @Query(nativeQuery = true,value = "UPDATE `tb_behavior_online_course` SET `behavior_end_time` = ? WHERE `id` = ?")
    int updateEndTimeById (Date endTime,String id);

}
