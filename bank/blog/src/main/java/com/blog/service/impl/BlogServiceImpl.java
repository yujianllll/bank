package com.blog.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.blog.constfuc.ConstFuc;
import com.blog.dto.Result;
import com.blog.entity.Blog;
import com.blog.mapper.BlogMapper;
import com.blog.service.IBlogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

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
    private RedisTemplate<String, String> redisTemplate;
    private static final String BLOG_LIKES_KEY_PREFIX = "blog:likes:";
    private static final String BLOG_COLLECT_KEY_PREFIX = "blog:collect:id:";
    private static final String BLOG_COLLECT_USER_KEY_PREFIX = "blog:collect:user:";
   @Override
    public Result queryHotBlog(Integer current, String user) {
        // 获取当前时间和10天前的时间
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tenDaysAgo = now.minusDays(10);
        // 自定义查询方法
        List<Blog> blogs = blogMapper.selectBlogs(tenDaysAgo);
        // 分页处理
       Page<Blog> page = blogPageQuery(blogs, user, current);
        return Result.ok(page);
    }
    @Override
    public Result saveBlog(Blog blog) {
        blog.setCreateTime(LocalDateTime.now());
        blog.setUpdateTime(LocalDateTime.now());

        int result = blogMapper.insertBlog(blog);
        if (result > 0) {
            return Result.ok("保存成功");
        }
        else {
            return Result.fail("保存失败");
        }

    }
    @Override
    public Result queryBlogByUserId(Integer current, Long user_id) {
        List<Blog> blogs = blogMapper.getBlogByUserId(user_id);
        if(blogs == null || blogs.isEmpty()){
            return Result.fail("暂无博客");
        }
        Page<Blog> page = blogPageQuery(blogs, String.valueOf(user_id), current);
        return Result.ok(page);
    }

    @Override
    public Result updateBlog(Blog blog, Long user_id) {
        Blog oldBlog = blogMapper.selectById(blog.getId());
        if(oldBlog == null){
            return Result.fail("博客不存在");
        }
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
        int result = blogMapper.updateBlog(blog);
        if (result > 0) {
            return Result.ok("更新成功");
        }
        else {
            return Result.fail("更新失败");
        }
    }

    @Override
    public Result deleteBlog(Long id, Long user_id) {
        Blog blog = blogMapper.getBlogById(id);
        if(blog == null){
            return Result.fail("博客不存在");
        }
        if(!Objects.equals(blog.getUserId(), user_id)){
            return Result.fail("不能删除别人的博客");
        }
        int result = blogMapper.deleteBlog(id);
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
            String collectKey = BLOG_COLLECT_KEY_PREFIX + id.toString();
            if(Boolean.TRUE.equals(redisTemplate.hasKey(collectKey))){
                Set<String> users = redisTemplate.opsForSet().members(collectKey);
                if (users != null) {
                    for(String user : users){
                        redisTemplate.opsForSet().remove(BLOG_COLLECT_USER_KEY_PREFIX + user, id.toString());
                    }
                }
                redisTemplate.delete(collectKey);
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
    @Override
    public Result searchBlogByTitle(String title, Integer current, String user) {
        // 自定义查询方法
        List<Blog> blogs = blogMapper.searchBlogByTitle(title);
        if(blogs == null || blogs.isEmpty()){
            return Result.fail("暂无搜索结果");
        }
        // 分页处理
        Page<Blog> page = blogPageQuery(blogs, user, current);
        return Result.ok(page);
    }
    @Override
    public Result collectBlog(Long id, String user) {
       if(user == null || user.equals("")){
            return Result.fail("请先登录");
       }
        String userKey = BLOG_COLLECT_USER_KEY_PREFIX + user;
        String blogKey = BLOG_COLLECT_KEY_PREFIX + id.toString();
        if(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(userKey, id.toString()))) {
            Long result = redisTemplate.opsForSet().remove(userKey, id.toString());
            redisTemplate.opsForSet().remove(blogKey, user);
            if (result != 0) {
                return Result.ok("取消收藏成功");
            } else {
                return Result.fail("取消收藏失败");
            }
        }
        else{
            Long result = redisTemplate.opsForSet().add(userKey, id.toString());
            redisTemplate.opsForSet().add(blogKey, user);
            if (result > 0) {
                return Result.ok("收藏成功");
            }
            else {
                return Result.fail("收藏失败");
            }
        }
    }
    @Override
    public Result queryCollectBlog(Integer current,String user) {
       if(user == null || user.equals("")){
            return Result.fail("请先登录");
       }
        String key = BLOG_COLLECT_USER_KEY_PREFIX + user;
       if(Boolean.TRUE.equals(redisTemplate.hasKey(key))){
           Set<String> collectBlogIds = redisTemplate.opsForSet().members(key);
           if (collectBlogIds != null && !collectBlogIds.isEmpty()) {
               Set<Long> collectBlogIdsLong = collectBlogIds.stream()
                       .map(Long::valueOf)
                       .collect(Collectors.toSet());
               List<Blog> blogs = blogMapper.queryBlogsById(collectBlogIdsLong);
               // 分页处理
               Page<Blog> page = blogPageQuery(blogs, user, current);
               return Result.ok(page);
           }
       }
        return Result.ok("暂无收藏博客");
    }





    public Page<Blog> blogPageQuery(List<Blog> dataList, String user, Integer current) {
        Page<Blog> page = new Page<>(current, 10);
        int start = (current - 1) * 10;
        int end = Math.min(start + 10, dataList.size());
        if(start > end){
            start = 0;
            end = Math.min(start + 10, dataList.size());
            page.setCurrent(1);
        }
        List<Blog> records = dataList.subList(start, end);
        if (user != null && !user.equals("")) {
            for (Blog blog : records) {
                String likeKey = BLOG_LIKES_KEY_PREFIX + blog.getId().toString();
                String collectKey = BLOG_COLLECT_KEY_PREFIX + blog.getId().toString();
                blog.setIsLike(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(likeKey, user)));
                blog.setIsCollect(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(collectKey, user)));
                if(blog.getImages() != null && !blog.getImages().isEmpty()){
                    blog.setImages(formatPath(blog.getImages()));
                }
            }
        }
        page.setRecords(records);
        return page;
    }

    public String formatPath(String path) {
        if (path != null && !path.isEmpty()) {
            return path.replace("\\", "/");
        }
        return null;
    }



}
