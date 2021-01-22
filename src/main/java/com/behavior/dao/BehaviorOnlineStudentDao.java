package com.behavior.dao;

import com.behavior.pojo.BehaviorOnlineStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BehaviorOnlineStudentDao extends JpaRepository<BehaviorOnlineStudent,String>, JpaSpecificationExecutor<BehaviorOnlineStudent> {
}
