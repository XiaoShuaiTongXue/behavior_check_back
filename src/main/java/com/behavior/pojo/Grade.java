package com.behavior.pojo;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table ( name ="tb_grade" )
public class Grade {

  	@Id
	private String id;
  	@Column(name = "grade_name" )
	private String gradeName;
  	@Column(name = "school_id" )
	private String schoolId;
  	@Column(name = "enrollment_year" )
	private String enrollmentYear;
	@OneToOne(targetEntity = School.class)
	@JoinColumn(name = "school_id",referencedColumnName = "id",insertable = false,updatable = false)
	private School school;

	public School getSchool() {
		return school;
	}

	public void setSchool(School school) {
		this.school = school;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGradeName() {
		return gradeName;
	}

	public void setGradeName(String gradeName) {
		this.gradeName = gradeName;
	}

	public String getSchoolId() {
		return schoolId;
	}

	public void setSchoolId(String schoolId) {
		this.schoolId = schoolId;
	}


	public String getEnrollmentYear() {
		return enrollmentYear;
	}

	public void setEnrollmentYear(String enrollmentYear) {
		this.enrollmentYear = enrollmentYear;
	}

}
