package com.behavior.dao;

import com.behavior.pojo.Grade;
import com.behavior.pojo.School;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface SchoolDao extends JpaRepository<School,String>, JpaSpecificationExecutor<Grade> {

    School findSchoolById(String id);
}
