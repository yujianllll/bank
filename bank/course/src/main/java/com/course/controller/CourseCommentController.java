package com.course.controller;

import com.course.constfuc.ConstFuc;
import com.course.dto.Result;
import com.course.entity.CourseComment;
import com.course.service.ICourseCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName:CourseCommentController
 * @Author:DC
 * @Date:2024/7/5 0:02
 * @version:1.0
 * @Description:课程评论控制器
 */
@RestController
@RequestMapping("/course/comment")
public class CourseCommentController {
    @Autowired
    private ConstFuc constFuc;
    @Autowired
    private ICourseCommentService courseCommentService;
    static final String COMMENT_IMAGE_UPLOAD_PATH = "course/src/main/resources/static/image/comment/";
    @PostMapping("/save")
    public Result saveComment(@ModelAttribute CourseComment courseComment,
                              @RequestParam("file") MultipartFile file,
                              @RequestHeader(value = "user-info",required = false) String user) {
        String imagesPath = null;
        if(user==null || user.isEmpty()){
            return Result.fail("请先登录！");
        }
        courseComment.setUserId(Long.valueOf(user));

        if(file.isEmpty()){
            courseComment.setImages("");
        }
        else{
            imagesPath = constFuc.saveFile(file,COMMENT_IMAGE_UPLOAD_PATH);
            if (imagesPath == null) {
                return Result.fail("图片上传失败");
            }
            courseComment.setImages(imagesPath);
        }
        Result result = courseCommentService.saveCourseComment(courseComment, user);
        if (!result.getSuccess() && !file.isEmpty() && imagesPath!= null) {
            boolean deleteFile = constFuc.deleteFile(imagesPath);
        }
        return result;

    }
    @GetMapping("/like")
    public Result likeCourseComment(@RequestParam("id") Long id,
                                    @RequestParam("courseId") Long courseId,
                                    @RequestHeader(value = "user-info",required = false) String user){
        if(user==null || user.isEmpty()){
            return Result.fail("请先登录！");
        }
        return courseCommentService.likeCourseComment(id, courseId, user);
    }
    @GetMapping("/list")
    public Result listCourseComment(@RequestParam("courseId") Long courseId,
                                    @RequestParam(value = "current", defaultValue = "1") Integer current,
                                    @RequestHeader(value = "user-info",required = false) String user){
        return courseCommentService.queryCourseComment(courseId, user, current);
    }
    @PostMapping("/delete")
    public Result deleteCourseComment(@RequestParam("id") Long id,
                                      @RequestHeader(value = "user-info",required = false) String user) {
        if (user == null || user.isEmpty()) {
            return Result.fail("请先登录！");
        }
        return courseCommentService.deleteCourseComment(id, user);
    }
}
