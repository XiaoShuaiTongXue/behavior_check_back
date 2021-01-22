package com.behavior.dao;

import com.behavior.pojo.Classes;
import com.behavior.pojo.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClassesDao extends JpaRepository<Classes,String>, JpaSpecificationExecutor<Classes> {

    Classes findClassesById(String id);


}
