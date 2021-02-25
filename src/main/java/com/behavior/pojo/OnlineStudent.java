package com.behavior.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tb_behavior_online_student")
public class OnlineStudent {

    @Id
    private String id;
    @Column(name = "student_id")
    private String studentId;
    @Column(name = "behavior_id")
    private String behaviorId;
    @Column(name = "post_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date postTime;
    @Column(name = "sleep_count")
    private int sleepCount;
    @Column(name = "talk_count")
    private int talkCount;
    @Column(name = "leave_count")
    private int leaveCount;
    @Column(name = "out_count")
    private int outCount;
    @Column(name = "file_post")
    private String filePost;
    @OneToOne(targetEntity = Student.class)
    @JoinColumn(name = "student_id", referencedColumnName = "id", insertable = false, updatable = false)
    Student student;
    @JsonIgnoreProperties({"onlineStudents"})
    @ManyToOne(targetEntity = OnlineCourse.class)
    @JoinColumn(name = "behavior_id", referencedColumnName = "id", insertable = false, updatable = false)
    private OnlineCourse onlineCourse;
    @Transient
    private double sleepPro;
    @Transient
    private double talkPro;
    @Transient
    private double leavePro;
    @Transient
    private double outPro;
    @Transient
    private double studyPro;
    @Transient
    private double degreePro;

    @JsonIgnore
    public String getStudentId() {
        return studentId;
    }

    @JsonIgnore
    public String getBehaviorId() {
        return behaviorId;
    }


    public void setOnlineCourse(OnlineCourse onlineCourse) {
        this.onlineCourse = onlineCourse;
    }

    public String getStudent() {
        return student.getName();
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    @JsonProperty
    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    @JsonProperty
    public void setBehaviorId(String behaviorId) {
        this.behaviorId = behaviorId;
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

    public void addTalkCount(int talkCount) {
        this.talkCount += talkCount;
    }


    public int getLeaveCount() {
        return leaveCount;
    }

    public void addLeaveCount(int leaveCount) {
        this.leaveCount += leaveCount;
    }

    public int getOutCount() {
        return outCount;
    }

    public void addOutCount(int outCount) {
        this.outCount += outCount;
    }

    public void setFilePost(String filePost) {
        this.filePost = filePost;
    }


    public Date getPostTime() {
        return postTime;
    }

    public void setPostTime(Date postTime) {
        this.postTime = postTime;
    }

    public int getSleepPro() {
        this.sleepPro = this.sleepCount * 100.0 / (onlineCourse.getSleepCount() + 0.001);
        return (int) sleepPro;
    }


    public int getTalkPro() {
        this.talkPro = this.talkCount * 100.0 / (onlineCourse.getTalkCount() + 0.001);
        return (int) talkPro;
    }


    public int getLeavePro() {
        this.leavePro = this.leaveCount * 100.0 / (onlineCourse.getLeaveCount() + 0.001);
        return (int) leavePro;
    }


    public int getOutPro() {
        this.outPro = this.outCount * 100.0 / (onlineCourse.getOutCount() + 0.001);
        return (int) outPro;
    }


    public int getStudyPro() {
        this.studyPro = 100 -(this.sleepPro + this.talkPro + this.outPro + this.leavePro) / 4;
        return (int) studyPro;
    }


    public int getDegreePro() {
        long complete = this.postTime.getTime() - onlineCourse.getBehaviorStartTime().getTime();
        this.degreePro = complete/10.0/onlineCourse.getLastTime();
        return (int) degreePro;
    }

}
