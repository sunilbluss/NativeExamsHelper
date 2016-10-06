package com.grudus.nativeexamshelper.pojos;


import java.util.Date;

public class JsonExam {
    private Long id;
    private Long subjectId;
    private Long userId;
    private String examInfo;
    private Date date;

    public JsonExam(Long id, Long subjectId, Long userId, String examInfo, Date date) {
        this.id = id;
        this.subjectId = subjectId;
        this.userId = userId;
        this.examInfo = examInfo;
        this.date = date;
    }

    public JsonExam() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getExamInfo() {
        return examInfo;
    }

    public void setExamInfo(String examInfo) {
        this.examInfo = examInfo;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "JsonAndroidExam{" +
                "id=" + id +
                ", subjectId=" + subjectId +
                ", userId=" + userId +
                ", examInfo='" + examInfo + '\'' +
                ", date=" + date +
                '}';
    }
}
