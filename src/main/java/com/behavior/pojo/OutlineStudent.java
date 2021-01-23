package com.behavior.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table ( name ="tb_behavior_outline_student" )
public class OutlineStudent {

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
	private int writeCount;
  	@Column(name = "put_bag_count" )
	private int putBagCount;
  	@Column(name = "phone_count" )
	private int phoneCount;
  	@Column(name = "look_note_count" )
	private int lookNoteCount;
  	@Column(name = "pass_note_count" )
	private int passNoteCount;


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


	public int getWriteCount() {
		return writeCount;
	}

	public void addWriteCount(int writeCount) {
		this.writeCount += writeCount;
	}


	public int getPutBagCount() {
		return putBagCount;
	}

	public void addPutBagCount(int putBagCount) {
		this.putBagCount += putBagCount;
	}


	public int getPhoneCount() {
		return phoneCount;
	}

	public void addPhoneCount(int phoneCount) {
		this.phoneCount += phoneCount;
	}


	public int getLookNoteCount() {
		return lookNoteCount;
	}

	public void addLookNoteCount(int lookNoteCount) {
		this.lookNoteCount += lookNoteCount;
	}


	public int getPassNoteCount() {
		return passNoteCount;
	}

	public void addPassNoteCount(int passNoteCount) {
		this.passNoteCount += passNoteCount;
	}

}
