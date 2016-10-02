package com.grudus.nativeexamshelper.pojos;


public class JsonSubject {
    private Long id;
    private String title;
    private String color;

    private JsonUser user;

    public JsonSubject(Long id, String title, String color, JsonUser user) {
        this.id = id;
        this.title = title;
        this.color = color;
        this.user = user;
    }

    public JsonSubject() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public JsonUser getUser() {
        return user;
    }

    public void setUser(JsonUser user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "JsonSubject{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", color='" + color + '\'' +
                ", user=" + user +
                '}';
    }
}
