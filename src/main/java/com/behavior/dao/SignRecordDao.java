package com.behavior.dao;

import com.behavior.pojo.SignRecord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface SignRecordDao extends JpaRepository<SignRecord, String>, JpaSpecificationExecutor<SignRecord> {

    @Query(nativeQuery = true, value = "SELECT `sign_start_time` FROM `tb_sign_record` WHERE `course_id` = ?")
    List<String> findSignRecordsDateByCourseId(String courseId);

    @Query(nativeQuery = true, value = "SELECT `id` FROM `tb_sign_record` WHERE `course_id` = ? AND `sign_start_time` = ? ")
    List<String> findIdsByCourseIdAndSignStartTime(String courseId, Date date, Pageable pageable);

    @Query(nativeQuery = true, value = "SELECT `id` FROM `tb_sign_record` WHERE `course_id` = ? ")
    List<String> findIdsByCourseId(String courseId, Pageable pageable);

    SignRecord findSignRecordById(String id);
}
