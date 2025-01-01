package com.mri.uiapp;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class History {
    private int id;
    private String dateTime;
    private User user;


    public History(int id, String dateTime, User user) {
        this.id = id;
        this.dateTime = dateTime;
        this.user = user;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDateTime() {
        String formattedDateTime = formatDateTime();
        return formattedDateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getUserId() {
        return this.user.getId();
    }

    public String getUserFirstName() {
        return this.user.getFirstName();
    }

    public String getUserSecondName() {
        return this.user.getSecondName();
    }

    @Override
    public String toString() {
        return "History{" +
                "id=" + id +
                ", dateTime='" + dateTime + '\'' +
                ", user=" + user +
                '}';
    }

    private String formatDateTime() {
        String dt = this.dateTime;
        LocalDateTime ldt = LocalDateTime.parse(dt);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd, HH:mm a");
        String formattedDateTime = ldt.format(formatter);
        return formattedDateTime;
    }
}
