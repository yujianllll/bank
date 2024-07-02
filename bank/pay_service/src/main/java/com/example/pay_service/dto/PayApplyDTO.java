package com.example.pay_service.dto;


import lombok.Data;

@Data
public class PayApplyDTO {
    private String bizOrderNo;
    private long bizUserId;
    private double amount;
    private long status;
}
