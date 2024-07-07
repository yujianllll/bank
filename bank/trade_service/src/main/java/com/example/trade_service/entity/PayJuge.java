package com.example.trade_service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("pay_juge")
public class PayJuge {

  @TableField("id")
  private long id;
  @TableField("user_id")
  private long userId;
  @TableField("pay_id")
  private String payId;
  @TableField("solder_id")
  private long solderId;
  @TableField("content")
  private String content;
  @TableField("juge")
  private long juge;
  @TableField("image")
  private String image;
  @TableField("createTime")
  private java.sql.Timestamp createTime;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }


  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }


  public String getPayId() {
    return payId;
  }

  public void setPayId(String payId) {
    this.payId = payId;
  }


  public long getSolderId() {
    return solderId;
  }

  public void setSolderId(long solderId) {
    this.solderId = solderId;
  }


  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }


  public long getJuge() {
    return juge;
  }

  public void setJuge(long juge) {
    this.juge = juge;
  }


  public String getImage() {
    return image;
  }

  public void setImage(String image) {
    this.image = image;
  }

}
