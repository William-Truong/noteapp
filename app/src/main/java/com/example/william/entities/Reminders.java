package com.example.william.entities;

import java.io.Serializable;

public class Reminders implements Serializable {
    private String title;
    private String description;
    private String date;
    private String time;
    private int active;

    public Reminders() {
    }

    public Reminders(String title, String description, String date, String time, int active) {
        this.title = title;
        this.description = description;
        this.date = date;
        this.time = time;
        this.active = active;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }
}
