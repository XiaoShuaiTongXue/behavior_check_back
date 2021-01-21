package com.behavior.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table ( name ="tb_sign_record" )
public class SignRecord {

  	@Id
	private String id;
  	@Column(name = "course_id" )
	private String courseId;
  	@Column(name = "sign_start_time" )
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date signStartTime;
  	@Column(name = "sign_end_time" )
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date signEndTime;
	@Column(name = "out_end_time" )
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date outEndTime;


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


	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
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
