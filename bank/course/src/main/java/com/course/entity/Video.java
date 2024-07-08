package com.course.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName:Video
 * @Author:DC
 * @Date:2024/7/3 22:42
 * @version:1.0
 * @Description:视频
 */
@TableName("video")
@lombok.Data
public class Video implements Serializable {
    private static final long serialVersionUID = 1L;
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long courseId;
    private String title;
    private String summary;
    private String videos;
    private Long sorting;
    private LocalDateTime createTime;
    private Long allTime;

    @TableField(exist = false)
    private Boolean isFinish = false;
    @TableField(exist = false)
    private Long assignedTime = 0L;

}
