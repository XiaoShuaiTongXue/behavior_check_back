package com.behavior.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table ( name ="tb_behavior_online_course" )
public class OnlineCourse {

  	@Id
	private String id;
  	@Column(name = "behavior_start_time" )
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date behaviorStartTime;
  	@Column(name = "behavior_end_time" )
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date behaviorEndTime;
  	@Column(name = "course_id" )
	private String courseId;
  	@Column(name = "sleep_count" )
	private int sleepCount;
  	@Column(name = "talk_count" )
	private int talkCount;
  	@Column(name = "leave_count" )
	private int leaveCount;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public Date getBehaviorStartTime() {
		return behaviorStartTime;
	}

	public void setBehaviorStartTime(Date behaviorStartTime) {
		this.behaviorStartTime = behaviorStartTime;
	}


	public Date getBehaviorEndTime() {
		return behaviorEndTime;
	}

	public void setBehaviorEndTime(Date behaviorEndTime) {
		this.behaviorEndTime = behaviorEndTime;
	}


	public String getCourseId() {
		return courseId;
	}

	public void setCourseId(String courseId) {
		this.courseId = courseId;
	}


	public int getSleepCount() {
		return sleepCount;
	}

	public void addSleepCount(int sleepCount) {
		this.sleepCount += sleepCount;
	}


	public int getTalkCount() {
		return talkCount;
	}

	public void addTalkCount(int speakCount) {
		this.talkCount += speakCount;
	}


	public int getLeaveCount() {
		return leaveCount;
	}

	public void addLeaveCount(int outCount) {
		this.leaveCount += outCount;
	}

}
