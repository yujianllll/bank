package com.school.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName:BlogComment
 * @Author:DC
 * @Date:2024/7/1 15:42
 * @version:1.0
 * @Description:博客评论
 */
@TableName("blogComment")
@lombok.Data
public class BlogComment implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long blogId;
    private Long parentId; // 父评论id
    private Long answerId; // 回复id
    private String content;
    private Integer liked;
    private String images;
    private LocalDateTime createTime;

    @TableField(exist = false)
    private String Icon; // 用户头像
    @TableField(exist = false)
    private String name;
    @TableField(exist = false)
    private Boolean isLike;
    @TableField(exist = false)
    private String answerName; // 回复评论的名字

}
