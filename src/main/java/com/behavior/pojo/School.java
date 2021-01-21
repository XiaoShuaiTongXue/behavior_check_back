package com.behavior.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table ( name ="tb_school" )
public class School {

  	@Id
	private String id;
  	@Column(name = "school_name" )
	private String schoolName;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getSchoolName() {
		return schoolName;
	}

	public void setSchoolName(String schoolName) {
		this.schoolName = schoolName;
	}

}
