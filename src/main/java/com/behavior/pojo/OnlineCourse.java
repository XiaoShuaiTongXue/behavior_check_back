package com.behavior.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "tb_behavior_online_course")
public class OnlineCourse {

    @Id
    private String id;
    @Column(name = "behavior_start_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date behaviorStartTime;
    @Column(name = "behavior_end_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date behaviorEndTime;
    @Column(name = "course_id")
    private String courseId;
    @Column(name = "sleep_count")
    private int sleepCount;
    @Column(name = "talk_count")
    private int talkCount;
    @Column(name = "leave_count")
    private int leaveCount;
    @Column(name = "out_count")
    private int outCount;
    @JsonIgnoreProperties({"onlineCourse"})
    @OneToMany(targetEntity = OnlineStudent.class)
    @JoinColumn(name = "behavior_id", referencedColumnName = "id")
    List<OnlineStudent> onlineStudents;

    public List<OnlineStudent> getOnlineStudents() {
        return onlineStudents;
    }

    public void setOnlineStudents(List<OnlineStudent> onlineStudents) {
        this.onlineStudents = onlineStudents;
    }

    public int getOutCount() {
        return outCount;
    }

    public void addOutCount(int outCount) {
        this.outCount += outCount;
    }

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

    @JsonIgnore
    public String getCourseId() {
        return courseId;
    }

    @JsonProperty
    public void setCourseId(String courseId) {
        this.courseId = courseId;
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

    public void addTalkCount(int speakCount) {
        this.talkCount += speakCount;
    }


    public int getLeaveCount() {
        return leaveCount;
    }

    public void addLeaveCount(int outCount) {
        this.leaveCount += outCount;
    }

}
