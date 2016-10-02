package com.grudus.nativeexamshelper.pojos;


import java.util.Date;

public class JsonExam {

    private Long id;
    private String examInfo;
    private Date date;

    private JsonSubject subject;

    public JsonExam() {
    }

    public JsonExam(Long id, String examInfo, Date date, JsonSubject subject) {
        this.id = id;
        this.examInfo = examInfo;
        this.date = date;
        this.subject = subject;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public JsonSubject getSubject() {
        return subject;
    }

    public void setSubject(JsonSubject subject) {
        this.subject = subject;
    }

    @Override
    public String toString() {
        return "JsonExam{" +
                "id=" + id +
                ", examInfo='" + examInfo + '\'' +
                ", date=" + date +
                ", subject=" + subject +
                '}';
    }
}
