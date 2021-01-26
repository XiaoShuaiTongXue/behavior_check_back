package com.behavior.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "tb_school")
public class School {

    @Id
    private String id;
    @Column(name = "school_name")
    private String schoolName;

    @JsonIgnoreProperties({"school"})
    @OneToMany(targetEntity = Grade.class)
    @JoinColumn(name = "school_id", referencedColumnName = "id")
    List<Grade> grade;

    public List<Grade> getGrade() {
        return grade;
    }

    public void setGrade(List<Grade> grades) {
        this.grade = grades;
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    @JsonProperty
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
