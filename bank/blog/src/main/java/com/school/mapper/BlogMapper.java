package com.school.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.school.entity.Blog;
import org.apache.ibatis.annotations.Options;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @ClassName:BlogMapper
 * @Author:DC
 * @Date:2024/6/29 17:23
 * @version:1.0
 * @Description:博客映射
 */
public interface BlogMapper extends BaseMapper<Blog> {
    List<Blog> selectBlogs(LocalDateTime time);
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertBlog(Blog blog);    //新增博客
    int deleteBlog(Long id);    //删除博客
    int updateBlog(Blog blog);    //更新博客
    List<Blog> getBlogByUserId(Long userId);    //根据作者查询博客
    Blog getBlogById(Long id);    //根据id查询博客
    int unLikeBlog(Long id);    //取消点赞
    int likeBlog(Long id);    //点赞
    int commentBlog(Long id);    //评论博客
    int unCommentBlog(Long id, Long size);    //取消评论博客
}
