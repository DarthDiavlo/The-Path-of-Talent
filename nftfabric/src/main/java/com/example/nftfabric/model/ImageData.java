package com.example.nftfabric.model;

import jakarta.persistence.*;

@Entity
@Table(name = "images_data")
public class ImageData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String category;
    private String subcategory;
    private Integer level;
    private Integer m;
    private Integer u;
    private Integer s;
    private Integer i;
    private Integer c;

    @Lob
    private byte[] image;

    // геттеры/сеттеры
    public Long getId() { return id; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSubcategory() { return subcategory; }
    public void setSubcategory(String subcategory) { this.subcategory = subcategory; }

    public Integer getLevel() { return level; }
    public void setLevel(Integer level) { this.level = level; }

    public Integer getM() { return m; }
    public void setM(Integer m) { this.m = m; }

    public Integer getU() { return u; }
    public void setU(Integer u) { this.u = u; }

    public Integer getS() { return s; }
    public void setS(Integer s) { this.s = s; }

    public Integer getI() { return i; }
    public void setI(Integer i) { this.i = i; }

    public Integer getC() { return c; }
    public void setC(Integer c) { this.c = c; }

    public byte[] getImage() { return image; }
    public void setImage(byte[] image) { this.image = image; }
}