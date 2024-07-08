package com.course.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.course.constfuc.ConstFuc;
import com.course.dto.Result;
import com.course.entity.Course;
import com.course.entity.Video;
import com.course.entity.VideoTime;
import com.course.mapper.CourseMapper;
import com.course.mapper.VideoMapper;
import com.course.service.IVideoService;
import com.example.bkapi.feign.userClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * @ClassName:VideoServiceImpl
 * @Author:DC
 * @Date:2024/7/5 15:13
 * @version:1.0
 * @Description:视频接口实现
 */
@Service
public class VideoServiceImpl extends ServiceImpl<VideoMapper, Video> implements IVideoService {
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private ConstFuc constFuc;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Resource
    private userClient userclient;
    private static final String COURSE_JOIN_ID_KEY_PREFIX = "course:join:id:";
    private static final String VIDEO_KEY_PREFIX = "course:video:";
    // course:video:{courseId}:{videoId} -> {userId},{time}
    private static final String FINISH_KEY_PREFIX = "course:finish:";

    @Override
    public Result saveVideo(Video video, Long userId) {
        Course course = courseMapper.queryCourseById(video.getCourseId());
        if(course == null){
            return Result.fail("课程不存在");
        }
        if(!Objects.equals(course.getUserId(), userId)){
            return Result.fail("只能上传自己的课程视频");
        }
        Long sorting = videoMapper.countVideo(video.getCourseId());
        video.setSorting(sorting + 1);
        video.setCreateTime(LocalDateTime.now());
        System.out.println(video);
        int result = videoMapper.saveVideo(video);
        if(result > 0){
            return Result.ok("视频添加成功");
        }else{
            return Result.fail("视频添加失败");
        }
    }
    @Override
    public Result queryVideo(Long courseId, String userId) {
        if(courseId == null || courseId <= 0){
            return Result.fail("课程id不能为空");
        }
        List<Video> videos = videoMapper.listVideoByCourseId(courseId);
        if(videos == null || videos.size() == 0){
            return Result.fail("课程下没有视频");
        }
        if(userId == null || userId.isEmpty()){
            for(Video video : videos){
                video.setVideos(formatPath(video.getVideos()));
            }
        }
        else{
            for(Video video : videos){
                String videoKey = VIDEO_KEY_PREFIX + courseId.toString() + ":" + video.getId().toString();
                Object timeObject = stringRedisTemplate.opsForHash().get(videoKey, userId);
                if(timeObject != null && Long.parseLong(timeObject.toString()) >= video.getAllTime()){
                    video.setIsFinish(true);
                }
                else{
                    if (timeObject != null) {
                        video.setAssignedTime(Long.parseLong(timeObject.toString()));
                    }
                }
                video.setVideos(formatPath(video.getVideos()));
            }
        }

        return Result.ok(videos);
    }
    @Override
    public Result updateVideo(Video video) {
        Video oldVideo = videoMapper.getVideoById(video.getId());
        if(oldVideo == null){
            return Result.fail("视频不存在");
        }
        if(video.getVideos() != null && !video.getVideos().equals("")){
            if(oldVideo.getVideos() != null && !oldVideo.getVideos().equals("")){
                boolean isDelete = constFuc.deleteFile(oldVideo.getVideos());
                if(!isDelete){
                    return Result.fail("视频文件删除失败");
                }
            }
        }
        int result = videoMapper.updateVideo(video);
        if(result > 0){
            return Result.ok("视频更新成功");
        }else{
            return Result.fail("视频更新失败");
        }
    }

    @Override
    public Result recordVideoTime(Long courseId, Long videoId, Long time, String userId) {
        Video video = videoMapper.selectVideo(courseId,videoId);
        if(video == null){
            return Result.fail("视频不存在");
        }
        if(time == null || time <= 0){
            return Result.fail("时间不能为空");
        }
        VideoTime videoTime = new VideoTime();
        videoTime.setTime(time);
        videoTime.setUserId(userId);
        String courseJoinIdKey = COURSE_JOIN_ID_KEY_PREFIX + courseId.toString();
        if(!Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(courseJoinIdKey, userId))){
            return Result.fail("用户未加入课程不用记录时间");
        }
        String videoKey = VIDEO_KEY_PREFIX + courseId.toString() + ":" + videoId.toString();
        stringRedisTemplate.opsForHash().put(videoKey,userId,time.toString());
        String finishKey = FINISH_KEY_PREFIX + courseId.toString();
        boolean isMember = Boolean.TRUE.equals(redisTemplate.opsForSet().isMember(finishKey, userId));
        if(video.getAllTime() <= time && !isMember){
            boolean isFinish = isCourseFinish(courseId,userId);
            if(isFinish) {
                Course course = courseMapper.queryCourseById(courseId);
                userclient.updatecredit(Long.valueOf(userId), Double.valueOf(course.getCredit()));
                userclient.updatemoney(Long.valueOf(userId), -1 * Double.valueOf(course.getCredit()));
            }
        }
        return Result.ok("记录成功");
    }
    @Override
    public Result deleteVideo(Long videoId, Long userId) {
        Video video = videoMapper.getVideoById(videoId);
        if(video == null){
            return Result.fail("视频不存在");
        }
        Course course = courseMapper.queryCourseById(video.getCourseId());
        if(course == null){
            return Result.fail("课程不存在");
        }
        if(!Objects.equals(course.getUserId(), userId)){
            return Result.fail("只能删除自己的课程视频");
        }
        int result = videoMapper.deleteVideo(videoId);
        if(result > 0){
            result = videoMapper.updateVideoSorting(video.getCourseId(), video.getSorting());
            String videoKey = VIDEO_KEY_PREFIX + video.getCourseId().toString() + ":" + videoId.toString();
            stringRedisTemplate.delete(videoKey);
            if(result > 0){
                System.out.println("视频排序已更新");
            }
            return Result.ok("视频删除成功");
        }else{
            return Result.fail("视频删除失败");
        }
    }

    public boolean isCourseFinish(Long courseId, String userId) {
        List<Video> videos = videoMapper.listVideoByCourseId(courseId);
        if(videos == null || videos.size() == 0){
            return false;
        }
        for(Video video : videos){
            String videoKey = VIDEO_KEY_PREFIX + courseId.toString() + ":" + video.getId().toString();
            Object timeObject = stringRedisTemplate.opsForHash().get(videoKey, userId);
            if(timeObject == null || Long.parseLong(timeObject.toString()) < video.getAllTime()){
                return false;
            }
        }
        redisTemplate.opsForSet().add(FINISH_KEY_PREFIX + courseId.toString(), userId);
        return true;
    }


    public String formatPath(String path) {
        if (path != null && !path.isEmpty()) {
            return path.replace("\\", "/");
        }
        return null;
    }
}
