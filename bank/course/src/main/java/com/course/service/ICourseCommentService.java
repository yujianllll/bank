package com.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.course.dto.Result;
import com.course.entity.CourseComment;

/**
 * @ClassName:ICourseCommentService
 * @Author:DC
 * @Date:2024/7/5 0:06
 * @version:1.0
 * @Description:课程服务接口
 */
public interface ICourseCommentService extends IService<CourseComment> {
    Result saveCourseComment(CourseComment courseComment, String userId);
    Result likeCourseComment(Long id, Long courseId, String userId);
    Result queryCourseComment(Long courseId, String userId, Integer current);
    Result deleteCourseComment(Long id, String userId);
}
