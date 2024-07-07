package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.entity.CourseComment;
import org.apache.ibatis.annotations.Options;

import java.util.List;

/**
 * @ClassName:CourseCommentMapper
 * @Author:DC
 * @Date:2024/7/5 0:04
 * @version:1.0
 * @Description:课程评论映射
 */
public interface CourseCommentMapper extends BaseMapper<CourseComment> {
    @Options(useGeneratedKeys = true, keyProperty = "id", keyColumn = "id")
    int insertComment(CourseComment courseComment);
    int likeComment(Long id);
    int unLikeComment(Long id);
    List<CourseComment> getCommentByCourseId(Long courseId);
    CourseComment getCommentById(Long id);
    int deleteComment(Long id);

}
