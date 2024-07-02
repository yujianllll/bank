package com.example.pay_service.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class PayOrder {

  @TableField("bizOrderNo")
  private String bizOrderNo;
  @TableField("payOrderNo")
  private String payOrderNo;
  private String id;
  @TableField("bizUserId")
  private long bizUserId;
  private double amount;
  /**
   * 支付状态，0：待提交，1:待支付，2：支付超时或取消，3：支付成功
   */
  private long status;
  @TableField("paySuccessTime")
  private java.sql.Timestamp paySuccessTime;
  @TableField("payOverTime")
  private java.sql.Timestamp payOverTime;
  @TableField("createTime")
  private java.sql.Timestamp createTime;

  public Integer getClose() {
    return close;
  }

  public void setClose(Integer close) {
    this.close = close;
  }

  private Integer close;

  public String getBizOrderNo() {
    return bizOrderNo;
  }

  public void setBizOrderNo(String bizOrderNo) {
    this.bizOrderNo = bizOrderNo;
  }


  public String getPayOrderNo() {
    return payOrderNo;
  }

  public void setPayOrderNo(String payOrderNo) {
    this.payOrderNo = payOrderNo;
  }


  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }


  public long getBizUserId() {
    return bizUserId;
  }

  public void setBizUserId(long bizUserId) {
    this.bizUserId = bizUserId;
  }


  public double getAmount() {
    return amount;
  }

  public void setAmount(double amount) {
    this.amount = amount;
  }


  public long getStatus() {
    return status;
  }

  public void setStatus(long status) {
    this.status = status;
  }


  public java.sql.Timestamp getPaySuccessTime() {
    return paySuccessTime;
  }

  public void setPaySuccessTime(java.sql.Timestamp paySuccessTime) {
    this.paySuccessTime = paySuccessTime;
  }


  public java.sql.Timestamp getPayOverTime() {
    return payOverTime;
  }

  public void setPayOverTime(java.sql.Timestamp payOverTime) {
    this.payOverTime = payOverTime;
  }


  public java.sql.Timestamp getCreateTime() {
    return createTime;
  }

  public void setCreateTime(java.sql.Timestamp createTime) {
    this.createTime = createTime;
  }

}
