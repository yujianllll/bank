package com.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName:Course
 * @Author:DC
 * @Date:2024/7/3 22:28
 * @version:1.0
 * @Description:课程
 */
@TableName("course")
@lombok.Data
public class Course implements Serializable {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long userId;
    private String title;
    private String images;
    private String summary;
    private String sort;
    private Integer liked;
    private Integer comments;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;

    @TableField(exist = false)
    private String icon;
    @TableField(exist = false)
    private String name;
    @TableField(exist = false)
    private Boolean isLike = false;
    @TableField(exist = false)
    private Boolean isJoin = false;
}
