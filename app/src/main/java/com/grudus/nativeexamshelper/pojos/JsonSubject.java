package com.grudus.nativeexamshelper.pojos;


import com.google.gson.Gson;

public class JsonSubject {
    private Long id;
    private Long userId;
    private String title;
    private String color;
    private String change;

    public JsonSubject() {
    }

    public JsonSubject(Long id, Long userId, String title, String color, String change) {

        this.id = id;
        this.userId = userId;
        this.title = title;
        this.color = color;
        this.change = change;
    }

    public Long getId() {

        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

    public String getChange() {
        return change;
    }

    public void setChange(String change) {
        this.change = change;
    }
}
