package com.behavior.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table ( name ="tb_behavior_online_student" )
public class BehaviorOnlineStudent {

  	@Id
	private String id;
  	@Column(name = "student_id" )
	private String studentId;
  	@Column(name = "behavior_id" )
	private String behaviorId;
	@Column(name = "post_time" )
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date postTime;
	@Column(name = "sleep_count" )
	private double sleepCount;
	@Column(name = "talk_count" )
	private double talkCount;
	@Column(name = "leave_count" )
	private double leaveCount;
	@Column(name = "file_post" )
	private String filePost;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getStudentId() {
		return studentId;
	}

	public void setStudentId(String studentId) {
		this.studentId = studentId;
	}


	public String getBehaviorId() {
		return behaviorId;
	}

	public void setBehaviorId(String behaviorId) {
		this.behaviorId = behaviorId;
	}


	public double getSleepCount() {
		return sleepCount;
	}

	public void setSleepCount(double sleepCount) {
		this.sleepCount = sleepCount;
	}


	public double getTalkCount() {
		return talkCount;
	}

	public void setTalkCount(double talkCount) {
		this.talkCount = talkCount;
	}


	public double getLeaveCount() {
		return leaveCount;
	}

	public void setLeaveCount(double leaveCount) {
		this.leaveCount = leaveCount;
	}


	public String getFilePost() {
		return filePost;
	}

	public void setFilePost(String filePost) {
		this.filePost = filePost;
	}


	public Date getPostTime() {
		return postTime;
	}

	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}

}
