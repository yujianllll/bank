package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.entity.Video;

import java.util.List;

/**
 * @ClassName:VideoMapper
 * @Author:DC
 * @Date:2024/7/5 14:45
 * @version:1.0
 * @Description:视频映射接口
 */
public interface VideoMapper  extends BaseMapper<Video> {
    int saveVideo(Video video);
    Long countVideo(Long courseId);
    List<Video> listVideoByCourseId(Long courseId);
    Video getVideoById(Long videoId);
    int updateVideo(Video video);
    Video selectVideo(Long courseId, Long id);
    int deleteVideo(Long videoId);
    int updateVideoSorting(Long courseId, Long sorting);
}
