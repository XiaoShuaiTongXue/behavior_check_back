package com.behavior.dao;

import com.behavior.pojo.SignStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SignStudentDao extends JpaRepository<SignStudent, String>, JpaSpecificationExecutor<SignStudent> {
    List<SignStudent> findSignStudentsBySignRecordIdIn(List<String> ids);

    SignStudent findSignStudentById(String id);

    List<SignStudent> findSignStudentsByStudentId(String id);

    @Modifying
    @Query(nativeQuery = true,value = "UPDATE `tb_sign_student` SET `sign_state` = ? WHERE id = ?")
    int updateStateById(int state,String id);
}
