package com.course.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.course.dto.Result;
import com.course.entity.Course;

/**
 * @ClassName:ICourseService
 * @Author:DC
 * @Date:2024/7/4 15:42
 * @version:1.0
 * @Description:课程接口
 */
public interface ICourseService extends IService<Course> {
    Result saveCourse(Course course);
    Result queryCourse(Integer current, String user);
    Result updateCourse(Course course, Long userId);
    Result likeCourse(Long id, String userId);
    Result searchCourseByTitle(String title, Integer current, String user);
    Result searchCourseBySort(String sort, Integer current, String user);
    Result searchCourseByUserId(Long user, Integer current);
    Result joinCourse(Long id, String user);
    Result queryMyCourse(String user, Integer current);
    Result deleteCourse(Long id, Long userId);
}
