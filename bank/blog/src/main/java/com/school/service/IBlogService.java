package com.school.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.school.dto.Result;
import com.school.entity.Blog;

/**
 * @ClassName:IBlogService
 * @Author:DC
 * @Date:2024/6/29 16:58
 * @version:1.0
 * @Description:博客服务接口
 */
public interface IBlogService extends IService<Blog> {
    Result queryHotBlog(Integer current, String user);
    Result saveBlog(Blog blog);
    Result updateBlog(Blog blog, Long userId);
    Result deleteBlog(Long id,Long userId);
    Result queryBlogByUserId(Long userId);
    Result likeBlog(Long id, Long userId);


}
