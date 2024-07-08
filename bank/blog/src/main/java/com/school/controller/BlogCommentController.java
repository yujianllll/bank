package com.school.controller;

import com.school.constfuc.ConstFuc;
import com.school.dto.Result;
import com.school.entity.BlogComment;
import com.school.service.IBlogCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @ClassName:BlogCommentController
 * @Author:DC
 * @Date:2024/7/1 15:56
 * @version:1.0
 * @Description:博客评论控制器
 */
@RestController
@RequestMapping("/blog/comment")
public class BlogCommentController {
    @Resource
    private IBlogCommentService blogCommentService;
    @Autowired
    private ConstFuc constFuc;
    static final String DIR = "comments";

    @PostMapping("/save") // 保存博客评论
    public Result saveBlogComment(@ModelAttribute BlogComment blogComment,
                                  @RequestParam("file") MultipartFile file,
                           @RequestHeader(value = "user-info",required = false) String user) {
        String imagesPath = null;
        if (user == null) {
            return Result.fail("请先登录");
        }
        blogComment.setUserId(Long.valueOf(user));

        if(file.isEmpty()){
            blogComment.setImages("");
        }
        else{
            imagesPath = constFuc.saveFile(file,DIR);
            if (imagesPath == null) {
                return Result.fail("图片上传失败");
            }
        }
        blogComment.setImages(imagesPath);
        Result result = blogCommentService.saveComment(blogComment);
        if (!result.getSuccess() && !file.isEmpty() && imagesPath!= null) {
            boolean deleteFile = constFuc.deleteFile(imagesPath);
        }
        return result;
    }

    @PostMapping("/delete") // 删除博客评论
    public Result deleteBlogComment(@RequestParam("id") Long id,
                                    @RequestHeader(value = "user-info",required = false) String user) {
        return blogCommentService.deleteComment(id, Long.valueOf(user));
    }


    @GetMapping("/list/parent") // 获取博客一级评论列表
    public Result getBlogCommentList(@RequestParam("blogId") Long blogId,
                                     @RequestParam(value = "current", defaultValue = "1") Integer current,
                                     @RequestHeader(value = "user-info",required = false) String user) {
        return blogCommentService.queryCommentByBlogId(blogId, current, user);
    }
    @GetMapping("/list/son") // 获取博客子评论列表
    public Result getBlogCommentListSon(@RequestParam("blogId") Long blogId,
                                         @RequestParam("parentId") Long parentId,
                                        @RequestParam(value = "current", defaultValue = "1") Integer current,
                                        @RequestHeader(value = "user-info",required = false) String user) {
        return blogCommentService.querySonCommentByBlogId(blogId, parentId, current, user);
    }
    @PostMapping("/like") // 点赞博客评论
    public Result likeBlogComment(@RequestParam("id") Long id,
                                  @RequestParam("blogId") Long blogId,
                                  @RequestHeader(value = "user-info",required = false) String user) {
        return blogCommentService.likeComment(id, blogId, user);
    }
    @GetMapping("/count") // 获取用户评论数量
    public Result getBlogCommentCount(@RequestParam(value = "current", defaultValue = "1") Integer current,
                                      @RequestHeader(value = "user-info",required = false) String user) {
        if(user==null || user.isEmpty()){
            return Result.fail("请先登录");
        }
        return blogCommentService.queryCommentByUserId(Long.valueOf(user), current);
    }
}
