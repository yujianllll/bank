package com.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.school.dto.Result;
import com.school.entity.BlogComment;

/**
 * @ClassName:IBlogCommentService
 * @Author:DC
 * @Date:2024/7/1 15:52
 * @version:1.0
 * @Description:博客评论接口
 */
public interface IBlogCommentService extends IService<BlogComment> {
    Result saveComment(BlogComment blogComment);
    Result deleteComment(Long id,Long userId);
    Result queryCommentByBlogId(Long blogId, Integer current, String userId);
    Result querySonCommentByBlogId(Long blogId, Long parentId, Integer current, String userId);
    Result likeComment(Long id, Long blogId, String userId);
    Result queryCommentByUserId(Long userId, Integer current);
}
