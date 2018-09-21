package com.think.onepass.model;

import java.sql.Date;


public class Secret {
    private long id;
    private String user;
    private String title;
    private String password;
    private String label;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    private String lastTime;

    public Secret(long id,String user,String title, String password, String label, String lastTime) {
        this.id=id;
        this.user = user;
        this.password = password;
        this.label = label;
        this.lastTime = lastTime;
        this.title=title;
    }
    public Secret(){

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }
}
