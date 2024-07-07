package com.course.entity;

import java.io.Serializable;

/**
 * @ClassName:VideoTime
 * @Author:DC
 * @Date:2024/7/6 14:51
 * @version:1.0
 * @Description:视频时间类
 */
@lombok.Data
public class VideoTime implements Serializable {
    private static final long serialVersionUID = 1L;
    private String userId;
    private Long time;
}
