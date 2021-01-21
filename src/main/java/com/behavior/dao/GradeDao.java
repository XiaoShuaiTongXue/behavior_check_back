package com.behavior.dao;

import com.behavior.pojo.Grade;
import com.behavior.pojo.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface GradeDao extends JpaRepository<Grade,String>, JpaSpecificationExecutor<Grade> {

    Grade findGradeById(String id);
}
