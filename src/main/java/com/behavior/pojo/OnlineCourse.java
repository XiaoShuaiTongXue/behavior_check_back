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
	private long sleepCount;
  	@Column(name = "speak_count" )
	private long speakCount;
  	@Column(name = "out_count" )
	private long outCount;


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


	public long getSleepCount() {
		return sleepCount;
	}

	public void setSleepCount(long sleepCount) {
		this.sleepCount = sleepCount;
	}


	public long getSpeakCount() {
		return speakCount;
	}

	public void setSpeakCount(long speakCount) {
		this.speakCount = speakCount;
	}


	public long getOutCount() {
		return outCount;
	}

	public void setOutCount(long outCount) {
		this.outCount = outCount;
	}

}
