package com.example.nftfabric.model;


public class ImageComposeRequest {
    private String category;
    private int level;
    private String text;

    // геттеры/сеттеры
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getLevel() { return level; }
    public void setLevel(int level) { this.level = level; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}
