package com.example.school.entity;

import lombok.Data;

@Data
public class LoginFormDTO {
    private String phone;
    private String code;
    private String password;
    private String newphone;
    private Integer maxchange;
}