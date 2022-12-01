package com.lukaszplawiak.twitterkafkamongoapp.dto;

public class RequestDto {
    private int minute;
    private String keyword;

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}