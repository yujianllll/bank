package com.course.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.course.constfuc.ConstFuc;
import com.course.dto.Result;
import com.course.entity.CourseComment;
import com.course.mapper.CourseCommentMapper;
import com.course.mapper.CourseMapper;
import com.course.service.ICourseCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName:CourseCommentServiceImpl
 * @Author:DC
 * @Date:2024/7/5 0:08
 * @version:1.0
 * @Description:课程服务
 */
@Service
public class CourseCommentServiceImpl extends ServiceImpl<CourseCommentMapper, CourseComment> implements ICourseCommentService {
    @Autowired
    private CourseCommentMapper courseCommentMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private ConstFuc constFuc;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    private static final String COURSE_COMMENT_KEY_PREFIX = "course:comments:";
    private static final String COURSE_JOIN_ID_KEY_PREFIX = "course:join:id:";
    // likes: course:comments:{courseId}:likes:{commentId}->{userId}
    // comments: course:comments:{courseId}->{commentId}
    @Override
    public Result saveCourseComment(CourseComment courseComment, String userId) {
        String joinKey = COURSE_JOIN_ID_KEY_PREFIX + courseComment.getCourseId().toString();
        if (Boolean.FALSE.equals(redisTemplate.opsForSet().isMember(joinKey, userId))) {
            return Result.fail("请先加入课程");
        }
        courseComment.setCreateTime(LocalDateTime.now());
        int result = courseCommentMapper.insertComment(courseComment);
        if (result > 0) {
            String key = COURSE_COMMENT_KEY_PREFIX + courseComment.getCourseId().toString();
            redisTemplate.opsForSet().add(key, courseComment.getId().toString());
            result = courseMapper.commentCourse(courseComment.getCourseId());
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
    public Result likeCourseComment(Long id, Long courseId, String userId) {
        String key = COURSE_COMMENT_KEY_PREFIX + courseId.toString() + ":likes:" + id.toString();
        if(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId))){
            redisTemplate.opsForSet().remove(key, userId);
            int result = courseCommentMapper.unLikeComment(id);
            if (result > 0) {
                return Result.ok("取消点赞成功");
            }
            else {
                return Result.fail("取消点赞失败");
            }
        }
        else{
            redisTemplate.opsForSet().add(key, userId);
            int result = courseCommentMapper.likeComment(id);
            if (result > 0) {
                return Result.ok("点赞成功");
            }
            else {
                return Result.fail("点赞失败");
            }
        }
    }
    @Override
    public Result queryCourseComment(Long courseId, String userId, Integer current) {
        List<CourseComment> courseComments = courseCommentMapper.getCommentByCourseId(courseId);
        if (courseComments.size() > 0) {
            Page<CourseComment> page = new Page<>(current, 10);
            int start = (current - 1) * 10;
            int end = Math.min(start + 10, courseComments.size());
            if(start > end){
                start = 0;
                end = Math.min(start + 10, courseComments.size());
                page.setCurrent(1);
            }
            List<CourseComment> records = courseComments.subList(start, end);
            if (userId != null && !userId.equals("")) {
                for (CourseComment courseComment : records) {
                    String key = COURSE_COMMENT_KEY_PREFIX + courseComment.getCourseId().toString() + ":likes:" + courseComment.getId().toString();
                    courseComment.setIsLike(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId)));
                    if(courseComment.getImages() != null && !courseComment.getImages().equals("")){
                        courseComment.setImages(courseComment.getImages().replace("\\", "/"));
                    }
                }
            }
            page.setRecords(records);
            return Result.ok(page);
        }
        return Result.fail("暂无评论");
    }
    @Override
    public Result deleteCourseComment(Long id, String userId) {
        CourseComment courseComment = courseCommentMapper.getCommentById(id);
        if(courseComment == null){
            return Result.fail("评论不存在");
        }
        if (!Objects.equals(courseComment.getUserId(), Long.valueOf(userId))) {
            return Result.fail("只能删除自己的评论");
        }
        int result = courseCommentMapper.deleteComment(id);
        if (result > 0) {
            if(courseComment.getImages() != null && !courseComment.getImages().equals("")){
                boolean delete = constFuc.deleteFile(courseComment.getImages());
                if(!delete) {
                    System.out.println("删除图片失败");
                }
            }
            String key = COURSE_COMMENT_KEY_PREFIX + courseComment.getCourseId().toString();
            redisTemplate.opsForSet().remove(key, id.toString());
            String likeKey = COURSE_COMMENT_KEY_PREFIX + courseComment.getCourseId().toString() + ":likes:" + id.toString();
            if(Boolean.TRUE.equals(redisTemplate.hasKey(likeKey))){
                redisTemplate.delete(likeKey);
            }
            result = courseMapper.unCommentCourse(courseComment.getCourseId());
            if (result > 0) {
                System.out.println("评论数-1");
            }
            return Result.ok("删除成功");
        }
        else {
            return Result.fail("删除失败");
        }
    }

}
