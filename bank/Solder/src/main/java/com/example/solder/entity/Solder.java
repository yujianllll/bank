package com.example.solder.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

@TableName("solder")
public class Solder {

  private long id;
  private String name;
  private double price;
  private String stock;
  private String image;
  private String category;
  private String brand;
  private String sold;
  @TableField("isAD")
  private long isAd;
  @TableField("createdTime")
  private java.sql.Timestamp createdTime;
  private String creater;


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }


  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }


  public String getStock() {
    return stock;
  }

  public void setStock(String stock) {
    this.stock = stock;
  }


  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }


  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }


  public String getBrand() {
    return brand;
  }

  public void setBrand(String brand) {
    this.brand = brand;
  }


  public String getSold() {
    return sold;
  }

  public void setSold(String sold) {
    this.sold = sold;
  }


  public long getIsAd() {
    return isAd;
  }

  public void setIsAd(long isAd) {
    this.isAd = isAd;
  }


  public java.sql.Timestamp getCreatedTime() {
    return createdTime;
  }

  public void setCreatedTime(java.sql.Timestamp createdTime) {
    this.createdTime = createdTime;
  }


  public String getCreater() {
    return creater;
  }

  public void setCreater(String creater) {
    this.creater = creater;
  }

}
