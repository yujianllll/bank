package com.example.trade_service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("`order`")
public class Order {

  private String id;
  private long userId;
  private double price;
  /**
   * 订单的状态，1、未付款 2、已付款,未发货 3、已发货,未确认 4、确认收货，交易成功 5、交易取消，订单关闭 6、交易结束，已评价
   */
  private long status;
  @TableField("creatTime")
  private java.sql.Timestamp creatTime;
  @TableField("payTime")
  private java.sql.Timestamp payTime;
  @TableField("consignTime")
  private java.sql.Timestamp consignTime;
  @TableField("endTime")
  private java.sql.Timestamp endTime;
  private long solderId;


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  public long getUserId() {
    return userId;
  }

  public void setUserId(long userId) {
    this.userId = userId;
  }


  public double getPrice() {
    return price;
  }

  public void setPrice(double price) {
    this.price = price;
  }


  public long getStatus() {
    return status;
  }

  public void setStatus(long status) {
    this.status = status;
  }


  public java.sql.Timestamp getCreatTime() {
    return creatTime;
  }

  public void setCreatTime(java.sql.Timestamp creatTime) {
    this.creatTime = creatTime;
  }


  public java.sql.Timestamp getPayTime() {
    return payTime;
  }

  public void setPayTime(java.sql.Timestamp payTime) {
    this.payTime = payTime;
  }


  public java.sql.Timestamp getConsignTime() {
    return consignTime;
  }

  public void setConsignTime(java.sql.Timestamp consignTime) {
    this.consignTime = consignTime;
  }


  public java.sql.Timestamp getEndTime() {
    return endTime;
  }

  public void setEndTime(java.sql.Timestamp endTime) {
    this.endTime = endTime;
  }


  public long getSolderId() {
    return solderId;
  }

  public void setSolderId(long solderId) {
    this.solderId = solderId;
  }

}
