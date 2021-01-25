package com.behavior.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;

@Entity
@Table ( name ="tb_student" )
public class Student {

	@Id
	private String id;
	@Column(name = "name" )
	private String name;
	@Column(name = "password" )
	private String password;
	@Column(name = "sex" )
	private String sex;
	@Column(name = "student_number" )
	private String studentNumber;
	@Column(name = "avatar" )
	private String avatar;
	@Column(name = "class_id" )
	private String classId;
	@Column(name = "class_name" )
	private String className;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	@JsonProperty
	public void setPassword(String password) {
		this.password = password;
	}


	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}


	public String getStudentNumber() {
		return studentNumber;
	}

	public void setStudentNumber(String studentNumber) {
		this.studentNumber = studentNumber;
	}


	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}


	public String getClassId() {
		return classId;
	}

	public void setClassId(String classId) {
		this.classId = classId;
	}


	public String getClassName() {
		return className;
	}

	public void setClassName(String faceCsv) {
		this.className = faceCsv;
	}

}
