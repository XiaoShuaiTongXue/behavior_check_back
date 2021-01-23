package com.behavior.dao;

import com.behavior.pojo.OnlineStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface OnlineStudentDao extends JpaRepository<OnlineStudent, String>, JpaSpecificationExecutor<OnlineStudent> {

    OnlineStudent findOnlineStudentByStudentIdAndBehaviorId(String studentId, String behaviorId);

    @Query(nativeQuery = true, value = "SELECT SUM(`sleep_count`) FROM `tb_behavior_online_student` WHERE `behavior_id` = ?")
    int getSumSleepCount(String behaviorId);

    @Query(nativeQuery = true, value = "SELECT SUM(`talk_count`) FROM `tb_behavior_online_student` WHERE `behavior_id` = ?")
    int getSumTalkCount(String behaviorId);

    @Query(nativeQuery = true, value = "SELECT SUM(`leave_count`) FROM `tb_behavior_online_student` WHERE `behavior_id` = ?")
    int getSumLeaveCount(String behaviorId);

}
