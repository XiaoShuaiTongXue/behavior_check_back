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
    @Column(name = "sleep_count")
    private int sleepCount;
    @Column(name = "talk_count")
    private int talkCount;
    @Column(name = "leave_count")
    private int leaveCount;
    @Column(name = "out_count")
    private int outCount;
    @Column(name = "course_id")
    private String courseId;
    @OneToOne(targetEntity = Course.class)
    @JoinColumn(name = "course_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Course course;
    @JsonIgnoreProperties({"onlineCourse"})
    @OneToMany(targetEntity = OnlineStudent.class)
    @JoinColumn(name = "behavior_id", referencedColumnName = "id")
    List<OnlineStudent> onlineStudents;
    @Transient
    private float lastTime;

    public List<OnlineStudent> getOnlineStudents() {
        return onlineStudents;
    }

    public void setOnlineStudents(List<OnlineStudent> onlineStudents) {
        this.onlineStudents = onlineStudents;
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

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public int getSleepCount() {
        return sleepCount;
    }

    public void setSleepCount(int sleepCount) {
        this.sleepCount = sleepCount;
    }

    public int getTalkCount() {
        return talkCount;
    }

    public void setTalkCount(int talkCount) {
        this.talkCount = talkCount;
    }

    public int getLeaveCount() {
        return leaveCount;
    }

    public void setLeaveCount(int leaveCount) {
        this.leaveCount = leaveCount;
    }

    public int getOutCount() {
        return outCount;
    }

    public void setOutCount(int outCount) {
        this.outCount = outCount;
    }

    public int getLastTime() {
        Date end = this.getBehaviorEndTime();
        if (end == null) {
            end = new Date();
        }
        long all = end.getTime() - this.getBehaviorStartTime().getTime();
        this.lastTime = (float)(all  / 1000.0) ;
        return (int) lastTime;
    }
}
