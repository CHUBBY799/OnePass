package com.think.onepass.model;

import java.sql.Date;


public class Secret {
    private int id;
    private String user;
    private String password;
    private String label;
    private Date lastTime;

    public Secret(int id, String user, String password, String label, Date lastTime) {
        this.id = id;
        this.user = user;
        this.password = password;
        this.label = label;
        this.lastTime = lastTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Date getLastTime() {
        return lastTime;
    }

    public void setLastTime(Date lastTime) {
        this.lastTime = lastTime;
    }
}
