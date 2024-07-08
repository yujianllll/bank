package com.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.course.dto.Result;
import com.course.entity.Video;

/**
 * @ClassName:IVideoService
 * @Author:DC
 * @Date:2024/7/5 15:08
 * @version:1.0
 * @Description:视频服务接口
 */
public interface IVideoService extends IService<Video> {
    Result saveVideo(Video video, Long userId);  // 保存视频
    Result queryVideo(Long courseId, String userId);  // 查询课程的视频
    Result updateVideo(Video video);  // 更新视频信息
    Result recordVideoTime(Long courseId, Long videoId, Long time, String userId);  // 记录用户观看视频时间
    Result deleteVideo(Long videoId, Long userId);  // 删除视频

}
