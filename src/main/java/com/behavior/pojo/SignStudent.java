package com.behavior.pojo;

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

    @OneToOne(targetEntity = SignRecord.class)
    @JoinColumn(name = "sign_record_id", referencedColumnName = "id", insertable = false, updatable = false)
    private SignRecord signRecord;

    public SignRecord getSignRecord() {
        return signRecord;
    }

    public void setSignRecord(SignRecord signRecord) {
        this.signRecord = signRecord;
    }

    public Student getStudent() {
        return student;
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


    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }


    public String getSignRecordId() {
        return signRecordId;
    }

    public void setSignRecordId(String signRecordId) {
        this.signRecordId = signRecordId;
    }

    public int getSignState() {
        return signState;
    }

    public void setSignState(int signState) {
        this.signState = signState;
    }
}
