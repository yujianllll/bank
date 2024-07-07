package com.school.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.school.constfuc.ConstFuc;
import com.school.dto.Result;
import com.school.entity.BlogComment;
import com.school.mapper.BlogCommentMapper;
import com.school.mapper.BlogMapper;
import com.school.service.IBlogCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * @ClassName:BlogCommentImpl
 * @Author:DC
 * @Date:2024/7/1 15:53
 * @version:1.0
 * @Description:博客接口实现
 */
@Service
public class BlogCommentImpl extends ServiceImpl<BlogCommentMapper, BlogComment> implements IBlogCommentService {
    @Autowired
    private BlogCommentMapper blogCommentMapper;
    @Autowired
    private BlogMapper blogMapper;
    @Autowired
    private ConstFuc constFuc;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static final String BLOG_COMMENT_KEY_PREFIX = "blog:comments:";
    // likes: blog:comments:{blogId}:likes:{commentId}->{userId}
    // comments: blog:comments:{blogId}->{commentId}
    // sonComments: blog:comments:{blogId}:{parentId}->{sonCommentId}
    // sonLikes: blog:comments:{blogId}:{parentId}:likes:{sonCommentId}->{userId}


    @Override
    public Result saveComment(BlogComment blogComment) {
        if(blogComment.getParentId()!=null && blogComment.getParentId()!=0){
            BlogComment parentComment = blogCommentMapper.selectCommentById(blogComment.getParentId());
            if(parentComment == null){
                return Result.fail("父评论不存在");
            }
        }
        blogComment.setCreateTime(LocalDateTime.now());
        int result = blogCommentMapper.insertComment(blogComment);
        if (result > 0) {
            String key = BLOG_COMMENT_KEY_PREFIX + blogComment.getBlogId().toString();
            if(blogComment.getParentId()!=null && blogComment.getParentId()!=0){
                String sonKey = key + ":" + blogComment.getParentId().toString();
                redisTemplate.opsForSet().add(sonKey, blogComment.getId().toString());
            }
            else{
                redisTemplate.opsForSet().add(key, blogComment.getId().toString());
            }
            result = blogMapper.commentBlog(blogComment.getBlogId());
            if (result > 0) {
                System.out.println("评论数+1");
            }
            return Result.ok("评论成功");
        }
        else {
            return Result.fail("评论失败");
        }
    }
    @Override
    public Result deleteComment(Long id, Long userId) {
        BlogComment blogComment = blogCommentMapper.selectCommentById(id);
        if (blogComment == null) {
            return Result.fail("评论不存在");
        }
        if (blogComment.getUserId().longValue() != userId) {
            return Result.fail("只能删除自己的评论");
        }
        if(blogComment.getImages()!=null){
            boolean flag = constFuc.deleteFile(blogComment.getImages());
            if(!flag){
                System.out.println("删除图片失败");
            }
        }
        int result = blogCommentMapper.deleteComment(id);
        if (result > 0) {
            String key = BLOG_COMMENT_KEY_PREFIX + blogComment.getBlogId().toString(); // 评论集合
            if(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, id.toString()))){
                redisTemplate.opsForSet().remove(key, id.toString());
                String sonKey = key + ":" + blogComment.getId().toString(); // 子评论集合
                long count = 1L;
                if(Boolean.TRUE.equals(redisTemplate.hasKey(sonKey))){
                    Long sonCount = redisTemplate.opsForSet().size(sonKey);
                    count = count + sonCount;
                    redisTemplate.delete(sonKey);
                }
                String sonLikesKey = sonKey + ":likes:*"; // 子评论点赞集合
                Set<String> keys = redisTemplate.keys(sonLikesKey);
                if (keys != null && !keys.isEmpty()) {
                    redisTemplate.delete(keys);
                    System.out.println("删除子评论点赞集合成功");
                }
                result = blogMapper.unCommentBlog(blogComment.getBlogId(), count);
            }
            String likesKey = key + ":likes:" + id.toString(); // 点赞集合
            if (Boolean.TRUE.equals(redisTemplate.hasKey(likesKey))) {
                redisTemplate.delete(likesKey);
                System.out.println("成功删除键：" + likesKey);
            } else {
                System.out.println("键不存在：" + likesKey);
            }
            if (result > 0) {
                System.out.println("评论数-1");
            }
            return Result.ok("删除成功");
        }
        else {
            return Result.fail("删除失败");
        }
    }

    @Override
    public Result queryCommentByBlogId(Long blogId, Integer current, String userId) {
        List<BlogComment> blogComments = blogCommentMapper.selectParentCommentByBlogId(blogId);
        if (blogComments == null || blogComments.size() == 0) {
            return Result.fail("暂无评论");
        }
        // 分页处理
        Page<BlogComment> page = new Page<>(current, 10);
        int start = (current - 1) * 10;
        int end = Math.min(start + 10, blogComments.size());
        if(start > end){
            start = 0;
            end = Math.min(start + 10, blogComments.size());
            page.setCurrent(1);
        }
        List<BlogComment> records = blogComments.subList(start, end);
        if(userId!= null && !userId.equals("")){
            for(BlogComment blogComment : records){
                String key = BLOG_COMMENT_KEY_PREFIX+blogId.toString()+":likes:"+ blogComment.getId().toString();
                blogComment.setIsLike(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId)));
                String sonKey = BLOG_COMMENT_KEY_PREFIX+blogId.toString()+":"+blogComment.getId().toString();
                Long count = redisTemplate.opsForSet().size(sonKey);
                blogComment.setSonCount(count);
                if(blogComment.getImages()!=null && !blogComment.getImages().equals("")){
                    blogComment.setImages(blogComment.getImages().replace("\\","/"));
                }

            }
        }
        page.setRecords(records);

        return Result.ok(records);
    }

    @Override
    public Result querySonCommentByBlogId(Long blogId, Long parentId, Integer current, String userId) {
        List<BlogComment> blogComments = blogCommentMapper.selectSonCommentByBlogId(blogId, parentId);
        if (blogComments == null || blogComments.size() == 0) {
            return Result.fail("暂无评论");
        }
        // 分页处理
        Page<BlogComment> page = new Page<>(current, 10);
        int start = (current - 1) * 10;
        int end = Math.min(start + 10, blogComments.size());
        if(start > end){
            start = 0;
            end = Math.min(start + 10, blogComments.size());
            page.setCurrent(1);
        }
        List<BlogComment> records = blogComments.subList(start, end);
        if(userId!= null && !userId.equals("")){
            for(BlogComment blogComment : records){
                String key = BLOG_COMMENT_KEY_PREFIX+blogId.toString()+":"+parentId.toString()+":likes:"+blogComment.getId().toString();
                blogComment.setIsLike(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId)));
                if(blogComment.getImages()!=null && !blogComment.getImages().equals("")){
                    blogComment.setImages(blogComment.getImages().replace("\\","/"));
                }
            }
        }
        page.setRecords(records);

        return Result.ok(records);
    }
    @Override
    public Result likeComment(Long id, Long blogId, String userId) {
        BlogComment blogComment = blogCommentMapper.selectCommentByBlogIdAndId(id, blogId);
        if (blogComment == null) {
            return Result.fail("评论不存在");
        }
        if(blogComment.getParentId()!=null && blogComment.getParentId()!=0){
            String sonLikesKey = BLOG_COMMENT_KEY_PREFIX+blogId.toString()+":"+blogComment.getParentId().toString()+":likes:"+id.toString();
            if(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(sonLikesKey, userId))){
                redisTemplate.opsForSet().remove(sonLikesKey, userId);
                int result = blogCommentMapper.unLikeComment(id);
                if (result > 0) {
                    return Result.ok("取消点赞成功");
                }
                else {
                    return Result.fail("取消点赞失败");
                }
            }
            else {
                redisTemplate.opsForSet().add(sonLikesKey, userId);
                int result = blogCommentMapper.likeComment(id);
                if (result > 0) {
                    return Result.ok("点赞成功");
                } else {
                    return Result.fail("点赞失败");
                }
            }
        }
        else{
            String key = BLOG_COMMENT_KEY_PREFIX+blogId.toString()+":likes:"+id.toString();
            if(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId))){
                redisTemplate.opsForSet().remove(key, userId);
                int result = blogCommentMapper.unLikeComment(id);
                if (result > 0) {
                    return Result.ok("取消点赞成功");
                }
                else {
                    return Result.fail("取消点赞失败");
                }
            }
            else{
                redisTemplate.opsForSet().add(key, userId);
                int result = blogCommentMapper.likeComment(id);
                if (result > 0) {
                    return Result.ok("点赞成功");
                }
                else {
                    return Result.fail("点赞失败");
                }
            }
        }
    }
}
