package com.course.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.course.constfuc.ConstFuc;
import com.course.dto.Result;
import com.course.entity.Course;
import com.course.mapper.CourseMapper;
import com.course.service.ICourseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName:CourseServiceImpl
 * @Author:DC
 * @Date:2024/7/4 15:44
 * @version:1.0
 * @Description:课程接口实现
 */
@Service
public class CourseServiceImpl  extends ServiceImpl<CourseMapper, Course> implements ICourseService {
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private ConstFuc constFuc;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    private static final String COURSE_LIKES_KEY_PREFIX = "course:likes:";
    private static final String COURSE_JOIN_ID_KEY_PREFIX = "course:join:id:";
    private static final String COURSE_JOIN_USER_KEY_PREFIX = "course:join:user:";
    private static final String VIDEO_KEY_PREFIX = "course:video:";
    private static final String FINISH_KEY_PREFIX = "course:finish:";


    @Override
    public Result saveCourse(Course course) {
        course.setCreateTime(LocalDateTime.now());
        course.setUpdateTime(LocalDateTime.now());
        if(course.getCredit() == null){
            course.setCredit(0L);
        }
        int result = courseMapper.insertCourse(course);
        if(result > 0){
            return Result.ok("课程添加成功");
        }else{
            return Result.fail("课程添加失败");
        }

    }
    @Override
    public Result queryCourse(Integer current, String user) {
        List<Course> courseList = courseMapper.queryCourseList();
        Page<Course> page = coursePageQuery(courseList, user, current);
        return Result.ok(page);
    }
    @Override
    public Result updateCourse(Course course, Long userId) {
        Course oldCourse = courseMapper.queryCourseById(course.getId());
        if (oldCourse == null) {
            return Result.fail("课程不存在");
        }
        if (!Objects.equals(oldCourse.getUserId(), userId)) {
            return Result.fail("不能修改别人的博客");
        }
        if (course.getImages() == null) {
            course.setImages(null);
        }
        else {
            if (oldCourse.getImages() != null && !oldCourse.getImages().isEmpty()) {
                boolean deleteFile = constFuc.deleteFile(oldCourse.getImages());
            }
        }
        oldCourse.setUpdateTime(LocalDateTime.now());
        int result = courseMapper.updateCourse(course);
        if (result > 0) {
            return Result.ok("更新成功");
        } else {
            return Result.fail("更新失败");
        }
    }
    @Override
    public Result likeCourse(Long id, String userId) {
        String key = COURSE_LIKES_KEY_PREFIX + id.toString();
        if(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, userId))){
            redisTemplate.opsForSet().remove(key, userId);
            int result = courseMapper.unLikeCourse(id);
            if (result > 0) {
                return Result.ok("取消点赞成功");
            }
            else {
                return Result.fail("取消点赞失败");
            }
        }
        else{
            redisTemplate.opsForSet().add(key, userId);
            int result = courseMapper.likeCourse(id);
            if (result > 0) {
                return Result.ok("点赞成功");
            }
            else {
                return Result.fail("点赞失败");
            }
        }
    }
    @Override
    public Result searchCourseByTitle(String title, Integer current, String user) {
        List<Course> courseList = courseMapper.searchCourse(title, null, null);
        if(courseList.isEmpty()){
            return Result.fail("没有搜索到相关标题课程");
        }
        Page<Course> page = coursePageQuery(courseList, user, current);
        return Result.ok(page);
    }
    @Override
    public Result searchCourseBySort(String sort, Integer current, String user) {
        List<Course> courseList = courseMapper.searchCourse(null, sort, null);
        if(courseList.isEmpty()){
            return Result.fail("没有搜索到相关类型课程");
        }
        Page<Course> page = coursePageQuery(courseList, user, current);
        return Result.ok(page);
    }
    @Override
    public Result searchCourseByUserId(Long userId, Integer current) {
        List<Course> courseList = courseMapper.searchCourse(null, null, userId);
        if(courseList.isEmpty()){
            return Result.fail("没有发布课程");
        }
        Page<Course> page = coursePageQuery(courseList, String.valueOf(userId), current);
        return Result.ok(page);
    }
    @Override
    public Result joinCourse(Long id, String userId) {
        String userKey = COURSE_JOIN_USER_KEY_PREFIX + userId;
        String courseKey = COURSE_JOIN_ID_KEY_PREFIX + id.toString();
        if(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(userKey, id.toString()))) {
            Long result = redisTemplate.opsForSet().remove(userKey, id.toString());
            redisTemplate.opsForSet().remove(courseKey, userId);
            if (result != 0) {
                return Result.ok("取消课程成功");
            } else {
                return Result.fail("取消课程失败");
            }
        }
        else{
            Long result = redisTemplate.opsForSet().add(userKey, id.toString());
            redisTemplate.opsForSet().add(courseKey, userId);
            if (result > 0) {
                return Result.ok("加入课程成功");
            }
            else {
                return Result.fail("加入课程失败");
            }
        }
    }

    @Override
    public Result queryMyCourse(String user, Integer current) {
        String key = COURSE_JOIN_USER_KEY_PREFIX + user;
        if(Boolean.TRUE.equals(redisTemplate.hasKey(key))){
            Set<String> collectCourseIds = redisTemplate.opsForSet().members(key);
            if (collectCourseIds != null && !collectCourseIds.isEmpty()) {
                Set<Long> collectCourseIdsLong = collectCourseIds.stream()
                        .map(Long::valueOf)
                        .collect(Collectors.toSet());
                List<Course> courseList = courseMapper.queryJoinCourseList(collectCourseIdsLong);
                Page<Course> page = coursePageQuery(courseList, user, current);
                return Result.ok(page);
            }
        }
        return Result.ok("暂无加入课程");
    }

    @Override
    public Result deleteCourse(Long id, Long userId) {
        Course course = courseMapper.queryCourseById(id);
        if(course == null){
            return Result.fail("课程不存在");
        }
        if(!Objects.equals(course.getUserId(), userId)){
            return Result.fail("不能删除别人的课程");
        }
        int result = courseMapper.deleteCourse(id);
        if (result > 0) {
            if(course.getImages() != null && !course.getImages().isEmpty()){
                boolean deleteFile = constFuc.deleteFile(course.getImages());
            }
            String key = COURSE_LIKES_KEY_PREFIX + id.toString();
            if(Boolean.TRUE.equals(redisTemplate.hasKey(key))){
                redisTemplate.delete(key);
            }
            String commentKey = "course:comments:" + id.toString();
            if(Boolean.TRUE.equals(redisTemplate.hasKey(commentKey))) {
                Set<String> keys = redisTemplate.keys(commentKey + "*");
                if (keys != null && !keys.isEmpty()) {
                    redisTemplate.delete(keys);
                    System.out.println("删除评论缓存成功");
                }
            }
            String joinKey = COURSE_JOIN_ID_KEY_PREFIX + id.toString();
            if(Boolean.TRUE.equals(redisTemplate.hasKey(joinKey))){
                Set<String> users = redisTemplate.opsForSet().members(joinKey);
                if (users != null && !users.isEmpty()) {
                    for(String user : users){
                        redisTemplate.opsForSet().remove(COURSE_JOIN_USER_KEY_PREFIX + user, id.toString());
                    }
                }
                redisTemplate.delete(joinKey);
            }
            String pattern = VIDEO_KEY_PREFIX + id.toString() + ":*";
            Set<String> videoKey = stringRedisTemplate.keys(pattern);
            if(videoKey != null && !videoKey.isEmpty()){
                redisTemplate.delete(videoKey);
            }
            String finishKey = FINISH_KEY_PREFIX + id.toString();
            if(Boolean.TRUE.equals(redisTemplate.hasKey(finishKey))){
                redisTemplate.delete(finishKey);
            }
            return Result.ok("删除成功");
        }
        else {
            return Result.fail("删除失败");
        }
    }
    @Override
    public Result queryCourseDetail(Long id, String user) {
        Course course = courseMapper.queryCourseById(id);
        if(course == null){
            return Result.fail("课程不存在");
        }
        if(user != null && !user.equals("")){
            String key = COURSE_LIKES_KEY_PREFIX + course.getId().toString();
            String joinKey = COURSE_JOIN_ID_KEY_PREFIX + course.getId().toString();
            course.setIsLike(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, user)));
            course.setIsJoin(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(joinKey, user)));
            Long count = redisTemplate.opsForSet().size(joinKey);
            course.setCountJoin(count);
            if(course.getImages() != null && !course.getImages().isEmpty()){
                course.setImages(formatPath(course.getImages()));
            }
        }
        return Result.ok(course);
    }
    @Override
    public Result queryTime(Long userId){
        Long learnTime = courseMapper.queryTime(userId);
        if(learnTime == null){
            return Result.ok(0L);
        }
        return Result.ok(learnTime);
    }



     public Page<Course> coursePageQuery(List<Course> courseList, String user, Integer current) {
         Page<Course> page = new Page<>(current, 12);
         int start = (current - 1) * 12;
         int end = Math.min(start + 12, courseList.size());
         if(start > end){
             start = 0;
             end = Math.min(start + 12, courseList.size());
             page.setCurrent(1);
         }
         List<Course> records = courseList.subList(start, end);
         if (user != null && !user.equals("")) {
             for (Course course : records) {
                 String key = COURSE_LIKES_KEY_PREFIX + course.getId().toString();
                 String joinKey = COURSE_JOIN_ID_KEY_PREFIX + course.getId().toString();
                 course.setIsLike(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(key, user)));
                 course.setIsJoin(Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(joinKey, user)));
                 Long count = redisTemplate.opsForSet().size(joinKey);
                 course.setCountJoin(count);
                 if(course.getImages() != null && !course.getImages().isEmpty()){
                     course.setImages(formatPath(course.getImages()));
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
