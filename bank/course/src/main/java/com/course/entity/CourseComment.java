package com.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName:CourseComment
 * @Author:DC
 * @Date:2024/7/3 22:39
 * @version:1.0
 * @Description:课程评论
 */
@TableName("course_comments")
@lombok.Data
public class CourseComment implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long courseId;
    private String content;
    private Integer liked;
    private String images;
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String Icon; // 用户头像
    @TableField(exist = false)
    private String name;
    @TableField(exist = false)
    private Boolean isLike = false;
}
