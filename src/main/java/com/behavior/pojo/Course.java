package com.behavior.pojo;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table ( name ="tb_course" )
public class Course {

  	@Id
	private String id;
  	@Column(name = "course_name" )
	private String courseName;
  	@Column(name = "teacher_id" )
	private String teacherId;
	@ManyToMany(targetEntity = Classes.class)
	@JoinTable(name = "tb_class_course",joinColumns = @JoinColumn(name = "course_id"),
			inverseJoinColumns = @JoinColumn(name = "class_id"))
	private List<Classes> classes;


	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getCourseName() {
		return courseName;
	}

	public void setCourseName(String courseName) {
		this.courseName = courseName;
	}


	public void setTeacherId(String teacherId) {
		this.teacherId = teacherId;
	}

	public List<Classes> getClasses() {
		return classes;
	}

	public void setClasses(List<Classes> classes) {
		this.classes = classes;
	}
}
