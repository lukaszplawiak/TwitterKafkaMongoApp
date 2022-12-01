package com.lukaszplawiak.twitterkafkamongoapp.dto;

public class PayloadDto {
    private String authorId;
    private String authorName;
    private String createdAt;
    private String tweetText;

    public PayloadDto() {
    }

    public PayloadDto(String authorId, String authorName, String createdAt, String tweetText) {
        this.authorId = authorId;
        this.authorName = authorName;
        this.createdAt = createdAt;
        this.tweetText = tweetText;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getTweetText() {
        return tweetText;
    }

    public void setTweetText(String tweetText) {
        this.tweetText = tweetText;
    }
}
