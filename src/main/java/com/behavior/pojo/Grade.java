package com.behavior.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tb_grade")
public class Grade {

    @Id
    private String id;
    @Column(name = "grade_name")
    private String gradeName;
    @JsonIgnore
    @Column(name = "school_id")
    private String schoolId;
    @Column(name = "enrollment_year")
    private String enrollmentYear;

    @JsonIgnoreProperties({"grade"})
    @OneToMany(targetEntity = Classes.class)
    @JoinColumn(name = "grade_id", referencedColumnName = "id")
    List<Classes> classes;

    @JsonIgnoreProperties({"grade"})
    @ManyToOne(targetEntity = School.class)
    @JoinColumn(name = "school_id", referencedColumnName = "id", insertable = false, updatable = false)
    private School school;

    public List<Classes> getClasses() {
        return classes;
    }

    public void setClasses(List<Classes> classes) {
        this.classes = classes;
    }

    public School getSchool() {
        return school;
    }

    public void setSchool(School school) {
        this.school = school;
    }

    @JsonIgnore
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


    public String getEnrollmentYear() {
        return enrollmentYear;
    }

    @JsonIgnore
    public String getSchoolId() {
        return schoolId;
    }

    @JsonProperty
    public void setSchoolId(String schoolId) {
        this.schoolId = schoolId;
    }

    public void setEnrollmentYear(String enrollmentYear) {
        this.enrollmentYear = enrollmentYear;
    }

}
