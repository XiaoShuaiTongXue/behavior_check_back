package com.behavior.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table ( name ="tb_sign_record" )
public class SignRecord {

  	@Id
	private String id;
  	@Column(name = "course_id" )
	private String courseId;
	@OneToOne(targetEntity = Course.class)
	@JoinColumn(name = "course_id",referencedColumnName = "id",insertable = false,updatable = false)
  	private Course course;
  	@Column(name = "sign_start_time" )
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date signStartTime;
  	@Column(name = "sign_end_time" )
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date signEndTime;
	@Column(name = "out_end_time" )
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date outEndTime;
	@JsonIgnoreProperties({"signRecord"})
	@OneToMany(targetEntity = SignStudent.class)
	@JoinColumn(name = "sign_record_id",referencedColumnName = "id")
	List<SignStudent> signStudents;

	public List<SignStudent> getSignStudents() {
		return signStudents;
	}

	public void setSignStudents(List<SignStudent> signStudents) {
		this.signStudents = signStudents;
	}

	public Date getOutEndTime() {
		return outEndTime;
	}

	public void setOutEndTime(Date outEndTime) {
		this.outEndTime = outEndTime;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}

	public Course getCourse() {
		return course;
	}

	public void setCourse(Course course) {
		this.course = course;
	}

	public Date getSignStartTime() {
		return signStartTime;
	}

	public void setSignStartTime(Date signStartTime) {
		this.signStartTime = signStartTime;
	}


	public Date getSignEndTime() {
		return signEndTime;
	}

	public void setSignEndTime(Date signEndTime) {
		this.signEndTime = signEndTime;
	}

}
