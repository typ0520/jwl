package com.example.jwl;

/**
 * @author tong
 */
@Table(name = "t_cls")
public class Class {
    @Id
    private String uuid;
    @Column(name = "head_teacher_id")
    private int headTeacherId;
    @Column(name = "student_num")
    private int studentNum;

    public int getHeadTeacherId() {
        return headTeacherId;
    }

    public void setHeadTeacherId(int headTeacherId) {
        this.headTeacherId = headTeacherId;
    }

    public int getStudentNum() {
        return studentNum;
    }

    public void setStudentNum(int studentNum) {
        this.studentNum = studentNum;
    }
}
