package com.behavior.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name = "tb_sign_student")
public class SignStudent {

    @Id
    private String id;
    @Column(name = "student_id")
    private String studentId;
    @Column(name = "sign_record_id")
    private String signRecordId;
    @Column(name = "sign_state")
    private int signState;
    @OneToOne(targetEntity = Student.class)
    @JoinColumn(name = "student_id", referencedColumnName = "id", insertable = false, updatable = false)
    private Student student;
    @JsonIgnoreProperties({"signStudents"})
    @ManyToOne(targetEntity = SignRecord.class)
    @JoinColumn(name = "sign_record_id", referencedColumnName = "id", insertable = false, updatable = false)
    private SignRecord signRecord;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getStudentId() {
        return studentId;
    }

    public String getSignRecordId() {
        return signRecordId;
    }

    public void setSignRecordId(String signRecordId) {
        this.signRecordId = signRecordId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public SignRecord getSignRecord() {
        return signRecord;
    }

    public void setSignRecord(SignRecord signRecord) {
        this.signRecord = signRecord;
    }

    public String getStudent() {
        return student.getName();
    }

    public void setStudent(Student student) {
        this.student = student;
    }


    public int getSignState() {
        return signState;
    }

    public void setSignState(int signState) {
        this.signState = signState;
    }
}
