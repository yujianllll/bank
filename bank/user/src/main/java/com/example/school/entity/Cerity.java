package com.example.school.entity;


import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("cerity")
public class Cerity {
    @TableField("id")
    private Long id;
    @TableField("content")
    private String content;
    @TableField("user_id")
    private Long user_id;
    @TableField("identy")
    private Integer identy;
    @TableField("iss")//1审核通过，2审核失败
    private Integer iss;
}
