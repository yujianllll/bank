package com.blog.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.blog.entity.BlogComment;
import org.apache.ibatis.annotations.Options;

import java.util.List;

/**
 * @ClassName:BlogCommentMapper
 * @Author:DC
 * @Date:2024/7/1 15:50
 * @version:1.0
 * @Description:博客评论映射
 */
public interface BlogCommentMapper extends BaseMapper<BlogComment> {
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertComment(BlogComment blogComment);
    int deleteComment(Long id);
    BlogComment selectCommentById(Long id);
    BlogComment selectCommentByBlogIdAndId(Long id, Long blogId);
    List<BlogComment> selectParentCommentByBlogId(Long blogId);
    List<BlogComment> selectSonCommentByBlogId(Long blogId, Long parentId);
    int likeComment(Long id);
    int unLikeComment(Long id);
    List<BlogComment> selectCommentByUserId(Long userId);
}
