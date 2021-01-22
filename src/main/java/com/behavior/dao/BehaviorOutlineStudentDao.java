package com.behavior.dao;

import com.behavior.pojo.BehaviorOutlineStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BehaviorOutlineStudentDao extends JpaRepository<BehaviorOutlineStudent,String>, JpaSpecificationExecutor<BehaviorOutlineStudent> {
}
