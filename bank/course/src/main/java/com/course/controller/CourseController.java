package com.course.controller;

import com.course.constfuc.ConstFuc;
import com.course.dto.Result;
import com.course.entity.Course;
import com.course.service.ICourseService;
import com.example.bkapi.feign.userClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

/**
 * @ClassName:CourseController
 * @Author:DC
 * @Date:2024/7/3 22:58
 * @version:1.0
 * @Description:课程控制器
 */
@RestController
@RequestMapping("/course")
public class CourseController {
    @Autowired
    private ConstFuc constFuc;
    @Resource
    userClient userclient;
    @Autowired
    private ICourseService courseService;
    static final String COURSE_IMAGE_UPLOAD_PATH = "course/src/main/resources/static/image/course/";


    @PostMapping("/save")  // 保存课程
    public Result saveCourse(@ModelAttribute Course course,
                           @RequestParam("file") MultipartFile file,
                           @RequestHeader(value = "user-info",required = false) String user) {

        String imagesPath = null;
        if (user == null) {
            return Result.fail("请先登录");
        }
        course.setUserId(Long.valueOf(user));
        // 保存文件
        if (file.isEmpty()) {
            return Result.fail("课程封面图片不能为空");
        }
        else{
            imagesPath = constFuc.saveFile(file,COURSE_IMAGE_UPLOAD_PATH);
            if (imagesPath == null) {
                return Result.fail("图片上传失败");
            }
            course.setImages(imagesPath);
        }
        Result result = courseService.saveCourse(course);
        if(!result.getSuccess()){
            boolean delete = constFuc.deleteFile(imagesPath);
        }

        return result;

    }
    @GetMapping("/list")  // 查询课程列表
    public Result queryCourse(@RequestParam(value = "current", defaultValue = "1") Integer current
                             ,@RequestHeader(value = "user-info",required = false) String user) {
        return courseService.queryCourse(current,user);
    }
    @PostMapping("/update") // 更新课程
    public Result updateCourse(@ModelAttribute Course course,
                           @RequestParam("file") MultipartFile file,
                           @RequestHeader(value = "user-info",required = false) String user) {
        String imagesPath = null;
        if (user == null) {
            return Result.fail("请先登录");
        }
        if (file.isEmpty() && course.getImages() != null) {
            course.setImages(null);
        }
        else if (file.isEmpty() && course.getImages() == null) {
            return Result.fail("课程封面图片不能为空");
        }
        else{
            imagesPath = constFuc.saveFile(file,COURSE_IMAGE_UPLOAD_PATH);
            if (imagesPath == null) {
                return Result.fail("图片上传失败");
            }
            course.setImages(imagesPath);
        }
        Result result = courseService.updateCourse(course, Long.valueOf(user));
        if (!result.getSuccess() && !file.isEmpty() && imagesPath!= null) {
            boolean deleteFile = constFuc.deleteFile(imagesPath);
        }
        return result;
    }
    @PostMapping("/like")  // 点赞课程
    public Result likeCourse(@RequestParam("id") Long id,
                            @RequestHeader(value = "user-info",required = false) String user) {
        if (user == null) {
            return Result.fail("请先登录");
        }
        return courseService.likeCourse(id, user);
    }
    @GetMapping("/search/title")  // 根据课程标题搜索课程
    public Result searchCourseByTitle(@RequestParam("title") String title,
                                     @RequestParam(value = "current", defaultValue = "1") Integer current,
                                     @RequestHeader(value = "user-info",required = false) String user) {
        if (title == null || title.trim().equals("")) {
            return Result.fail("搜索内容不能为空");
        }
        return courseService.searchCourseByTitle(title, current, user);
    }
    @GetMapping("/search/sort")  // 根据课程分类搜索课程
    public Result searchCourseByCategory(@RequestParam("sort") String sort,
                                     @RequestParam(value = "current", defaultValue = "1") Integer current,
                                     @RequestHeader(value = "user-info",required = false) String user) {
        if (sort == null || sort.trim().equals("")) {
            return Result.fail("搜索内容不能为空");
        }
        return courseService.searchCourseBySort(sort, current, user);
    }
    @GetMapping("/getlist")  // 根据用户id查询发布的课程列表
    public Result searchCourseByUserId(@RequestParam(value = "current", defaultValue = "1") Integer current,
                                     @RequestHeader(value = "user-info",required = false) String user) {
        return courseService.searchCourseByUserId(Long.valueOf(user), current);
    }
    @GetMapping("/join")  // 加入课程
    public Result joinCourse(@RequestParam("id") Long id,
                            @RequestHeader(value = "user-info",required = false) String user) {
        if (user == null) {
            return Result.fail("请先登录");
        }
        return courseService.joinCourse(id, user);
    }

    @GetMapping("/joinlist")  // 查询已加入的课程列表
    public Result getJoinCourse(@RequestParam(value = "current", defaultValue = "1") Integer current,
                                @RequestHeader(value = "user-info",required = false) String user) {
        if (user == null) {
            return Result.fail("请先登录");
        }
        return courseService.queryMyCourse(user, current);
    }

    @PostMapping("/delete")  // 删除课程
    public Result deleteCourse(@RequestParam("id") Long id,
                            @RequestHeader(value = "user-info",required = false) String user) {
        return courseService.deleteCourse(id, Long.valueOf(user));
    }
    @GetMapping("/detail")  // 查询课程详情
    public Result queryCourseDetail(@RequestParam("id") Long id,
                                    @RequestHeader(value = "user-info",required = false) String user) {
        if (user == null) {
            return Result.fail("请先登录");
        }
        return courseService.queryCourseDetail(id, user);
    }
    //管理员查看所有未审核的模块
    @GetMapping("/selectisok")
    public Result selectisok()
    {
        List<Course> courses = courseService.query().eq("state",0).list();
        return Result.ok(courses);
    }
    //管理员定义课程价值
    @PostMapping("/value")
    public Result value(@RequestBody Course course)
    {
        courseService.lambdaUpdate()
                .set(Course::getState,1)
                .set(Course::getCredit,course.getCredit())
                .eq(Course::getId,course.getId())
                .update();

        return Result.ok();
    }

    @GetMapping("/time")
    public Result time(@RequestHeader(value = "user-info",required = false) String user)
    {
        if (user == null) {
            return Result.fail("请先登录");
        }
        return courseService.queryTime(Long.valueOf(user));
    }


}
