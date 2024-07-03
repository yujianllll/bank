package com.example.school.entity;

import lombok.Data;

@Data
public class Where {

  private String sheng;
  private String shi;
  private String xian;
  private String home;
  private long userId;
  private String phone;


  public String getSheng() {
    return sheng;
  }

  public void setSheng(String sheng) {
    this.sheng = sheng;
  }


  public String getShi() {
    return shi;
  }

  public void setShi(String shi) {
    this.shi = shi;
  }


  public String getXian() {
    return xian;
  }

  public void setXian(String xian) {
    this.xian = xian;
  }


  public String getHome() {
    return home;
  }

  public void setHome(String home) {
    this.home = home;
  }


  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }


  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

}
