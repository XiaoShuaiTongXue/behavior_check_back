package com.behavior.dao;

import com.behavior.pojo.OutlineStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

public interface OutlineStudentDao extends JpaRepository<OutlineStudent,String>, JpaSpecificationExecutor<OutlineStudent> {

    OutlineStudent findOutlineStudentByStudentIdAndBehaviorId(String studentId,String behaviorId);

    @Query(nativeQuery = true,value = "SELECT SUM(`write_count`) FROM `tb_behavior_outline_student` WHERE `behavior_id` = ?")
    int getSumWriteCount(String behaviorId);

    @Query(nativeQuery = true,value = "SELECT SUM(`put_bag_count`) FROM `tb_behavior_outline_student` WHERE `behavior_id` = ?")
    int getSumPutBagCount(String behaviorId);

    @Query(nativeQuery = true,value = "SELECT SUM(`phone_count`) FROM `tb_behavior_outline_student` WHERE `behavior_id` = ?")
    int getSumPhoneCount(String behaviorId);

    @Query(nativeQuery = true,value = "SELECT SUM(`look_note_count`) FROM `tb_behavior_outline_student` WHERE `behavior_id` = ?")
    int getSumLookNoteCount(String behaviorId);

    @Query(nativeQuery = true, value = "SELECT SUM(`pass_note_count`) FROM `tb_behavior_outline_student` WHERE `behavior_id` = ?")
    int getSumPassNoteCount(String behaviorId);


}
