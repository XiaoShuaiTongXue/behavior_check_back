package com.behavior.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table ( name ="tb_behavior_outline_course" )
public class OutlineCourse {

  	@Id
	private String id;
  	@Column(name = "course_id" )
	private String courseId;
  	@Column(name = "behavior_start_time" )
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date behaviorStartTime;
  	@Column(name = "behavior_end_time" )
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date behaviorEndTime;
  	@Column(name = "write_count" )
	private long writeCount;
  	@Column(name = "put_bag_count" )
	private long putBagCount;
  	@Column(name = "phone_count" )
	private long phoneCount;
  	@Column(name = "look_note_count" )
	private long lookNoteCount;
  	@Column(name = "pass_note_count" )
	private long passNoteCount;


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


	public long getWriteCount() {
		return writeCount;
	}

	public void setWriteCount(long writeCount) {
		this.writeCount = writeCount;
	}


	public long getPutBagCount() {
		return putBagCount;
	}

	public void setPutBagCount(long putBagCount) {
		this.putBagCount = putBagCount;
	}


	public long getPhoneCount() {
		return phoneCount;
	}

	public void setPhoneCount(long phoneCount) {
		this.phoneCount = phoneCount;
	}


	public long getLookNoteCount() {
		return lookNoteCount;
	}

	public void setLookNoteCount(long lookNoteCount) {
		this.lookNoteCount = lookNoteCount;
	}


	public long getPassNoteCount() {
		return passNoteCount;
	}

	public void setPassNoteCount(long passNoteCount) {
		this.passNoteCount = passNoteCount;
	}

}
