package com.course.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.course.entity.Course;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @ClassName:CourseMapper
 * @Author:DC
 * @Date:2024/7/4 15:40
 * @version:1.0
 * @Description:课程映射
 */
public interface CourseMapper extends BaseMapper<Course> {
    int insertCourse(Course course);
    List<Course> queryCourseList();
    Course queryCourseById(Long id);
    int updateCourse(Course course);
    int likeCourse(Long id);
    int unLikeCourse(Long id);
    int commentCourse(Long id);
    int unCommentCourse(Long id);
    List<Course> searchCourse(String title, String sort, Long userId);
    List<Course> queryJoinCourseList(@Param("ids") Set<Long> ids);
    int deleteCourse(Long id);
    Long queryTime(Long id);
    int updateTime(Long id, Long time);

}
