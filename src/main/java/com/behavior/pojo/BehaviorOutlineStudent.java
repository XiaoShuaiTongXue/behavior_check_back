package com.behavior.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table ( name ="tb_behavior_outline_student" )
public class BehaviorOutlineStudent {

  	@Id
	private String id;
  	@Column(name = "student_id" )
	private String studentId;
  	@Column(name = "behavior_id" )
	private String behaviorId;
  	@Column(name = "post_time" )
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
	private Date postTime;
  	@Column(name = "file_post" )
	private String filePost;
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


	public Date getPostTime() {
		return postTime;
	}

	public void setPostTime(Date postTime) {
		this.postTime = postTime;
	}


	public String getFilePost() {
		return filePost;
	}

	public void setFilePost(String filePost) {
		this.filePost = filePost;
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
