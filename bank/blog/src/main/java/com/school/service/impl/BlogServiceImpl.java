package com.school.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.constfuc.ConstFuc;
import com.school.dto.Result;
import com.school.entity.Blog;
import com.school.mapper.BlogMapper;
import com.school.service.IBlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @ClassName:BlogServiceImpl
 * @Author:DC
 * @Date:2024/6/29 17:01
 * @version:1.0
 * @Description:博客服务类
 */
@Service
public class BlogServiceImpl extends ServiceImpl<BlogMapper, Blog> implements IBlogService{
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private BlogMapper blogCommentMapper;
    @Autowired
    private ConstFuc constFuc;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    private static final String BLOG_LIKES_KEY_PREFIX = "blog:likes:";
   @Override
    public Result queryHotBlog(Integer current, String user) {
        // 获取当前时间和10天前的时间
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenDaysAgo = now.minusDays(10);
        // 自定义查询方法
        List<Blog> blogs = blogMapper.selectBlogs(tenDaysAgo);
        // 分页处理
        Page<Blog> page = new Page<>(current, 10);
        int start = (current - 1) * 10;
        int end = Math.min(start + 10, blogs.size());
        List<Blog> records = blogs.subList(start, end);
       if(user!= null && !user.equals("")){
           for(Blog blog : records){
               String key = BLOG_LIKES_KEY_PREFIX + blog.getId().toString();
               blog.setIsLike(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, user)));
           }
       }
        page.setRecords(records);

        return Result.ok(records);
    }
    @Override
    public Result saveBlog(Blog blog) {
        blog.setCreateTime(LocalDateTime.now());
        blog.setUpdateTime(LocalDateTime.now());

        int result = blogMapper.insert(blog);
        if (result > 0) {
            return Result.ok("保存成功");
        }
        else {
            return Result.fail("保存失败");
        }

    }
    @Override
    public Result queryBlogByUserId(Long user_id) {
        List<Blog> blogs = blogMapper.getBlogByUserId(user_id);
        for(Blog blog : blogs){
            String key = BLOG_LIKES_KEY_PREFIX + blog.getId().toString();
            blog.setIsLike(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, user_id.toString())));
        }
        return Result.ok(blogs);
    }

    @Override
    public Result updateBlog(Blog blog, Long user_id) {
        Blog oldBlog = blogMapper.selectById(blog.getId());
        if(!Objects.equals(oldBlog.getUserId(), user_id)){
            return Result.fail("不能修改别人的博客");
        }
        if(blog.getImages() == null || blog.getImages().equals("") || blog.getImages().equals("不修改图片")){
            blog.setImages(null);
        }
        else{
            if(oldBlog.getImages() != null && !oldBlog.getImages().isEmpty()){
                boolean deleteFile = constFuc.deleteFile(oldBlog.getImages());
            }
            if(blog.getImages().equals("删除图片")){
                blog.setImages("");
            }
        }
        blog.setUpdateTime(LocalDateTime.now());
        int result = blogMapper.updateById(blog);
        if (result > 0) {
            return Result.ok("更新成功");
        }
        else {
            return Result.fail("更新失败");
        }
    }

    @Override
    public Result deleteBlog(Long id, Long user_id) {
        Blog blog = blogMapper.selectById(id);
        if(blog == null){
            return Result.fail("博客不存在");
        }
        if(!Objects.equals(blog.getUserId(), user_id)){
            return Result.fail("不能删除别人的博客");
        }
        int result = blogMapper.deleteById(id);
        if (result > 0) {
            if(blog.getImages() != null && !blog.getImages().isEmpty()){
                boolean deleteFile = constFuc.deleteFile(blog.getImages());
            }
            String key = BLOG_LIKES_KEY_PREFIX + id.toString();
            if(Boolean.TRUE.equals(redisTemplate.hasKey(key))){
                redisTemplate.delete(key);
            }
            String commentKey = "blog:comments:" + id;
            if(Boolean.TRUE.equals(redisTemplate.hasKey(commentKey))) {
                Set<String> keys = redisTemplate.keys(commentKey + "*");
                if (keys != null && !keys.isEmpty()) {
                    redisTemplate.delete(keys);
                    System.out.println("删除评论缓存成功");
                }
            }
            return Result.ok("删除成功");
        }
        else {
            return Result.fail("删除失败");
        }
    }
    @Override
    public Result likeBlog(Long id, Long user_id) {
        String key = BLOG_LIKES_KEY_PREFIX + id.toString();
        String userId = user_id.toString();
        if(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId))){
            redisTemplate.opsForSet().remove(key, userId);
            int result = blogMapper.unLikeBlog(id);
            if (result > 0) {
                return Result.ok("取消点赞成功");
            }
            else {
                return Result.fail("取消点赞失败");
            }
        }
        else{
            redisTemplate.opsForSet().add(key, userId);
            int result = blogMapper.likeBlog(id);
            if (result > 0) {
                return Result.ok("点赞成功");
            }
            else {
                return Result.fail("点赞失败");
            }
        }
    }









}
