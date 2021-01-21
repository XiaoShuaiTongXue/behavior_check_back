package com.behavior.pojo;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table ( name ="tb_class" )
public class Classes {

	@Id
	private String id;
	@Column(name = "grade_id" )
	private String gradeId;
	@Column(name = "class_no" )
	private String classNo;
	@Column(name = "class_count" )
	private String classCount;
	@OneToOne(targetEntity = Grade.class)
	@JoinColumn(name = "grade_id",referencedColumnName = "id",insertable = false,updatable = false)
	private Grade grade;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getGradeId() {
		return gradeId;
	}

	public void setGradeId(String gradeId) {
		this.gradeId = gradeId;
	}


	public String getClassNo() {
		return classNo;
	}

	public void setClassNo(String classNo) {
		this.classNo = classNo;
	}


	public String getClassCount() {
		return classCount;
	}

	public void setClassCount(String classCount) {
		this.classCount = classCount;
	}

	public Grade getGrade() {
		return grade;
	}

	public void setGrade(Grade grade) {
		this.grade = grade;
	}


}
