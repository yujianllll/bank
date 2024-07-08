package com.school.controller;

import com.school.constfuc.ConstFuc;
import com.school.dto.Result;
import com.school.entity.Blog;
import com.school.service.IBlogService;
import com.school.util.SensitiveWordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;

/**
 * @ClassName:BlogController
 * @Author:DC
 * @Date:2024/6/29 16:52
 * @version:1.0
 * @Description:博客接口
 */

@RestController
@RequestMapping("/blog")
public class BlogController {
    @Resource
    private IBlogService blogService;
    @Autowired
    private ConstFuc constFuc;
    @Resource
    private SensitiveWordUtil sensitiveWordUtil;

    @GetMapping("/list")  //查询热门博客
    public Result queryHotBlog(@RequestParam(value = "current", defaultValue = "1") Integer current,
                               @RequestHeader(value = "user-info",required = false) String user) {
        return blogService.queryHotBlog(current, user);
    }

    @PostMapping("/save")  //保存博客
    public Result saveBlog(@ModelAttribute Blog blog, @RequestParam("file") MultipartFile file,
                           @RequestHeader(value = "user-info",required = false) String user) {
        // 设置用户信息
        String imagesPath = null;
        if (user == null) {
            return Result.fail("请先登录");
        }
        blog.setUserId(Long.valueOf(user));
        // 保存文件
        if (file.isEmpty()) {
            blog.setImages("");
        }
        else{
            imagesPath = constFuc.saveFile(file,null);
            if (imagesPath == null) {
                return Result.fail("图片上传失败");
            }
            blog.setImages(imagesPath);
        }
        if(sensitiveWordUtil.contains(blog.getContent()))
        {
            blog.setContent(sensitiveWordUtil.replace(blog.getContent()));
        }
        if(sensitiveWordUtil.contains(blog.getTitle()))
        {
            blog.setTitle(sensitiveWordUtil.replace(blog.getTitle()));
        }
        Result result = blogService.saveBlog(blog);
        if (!result.getSuccess() && !file.isEmpty() && imagesPath!= null) {
            boolean deleteFile = constFuc.deleteFile(imagesPath);
        }
        return result;
    }
    @GetMapping("/getlist")  //查询用户的博客列表
    public Result queryBlogByUserId(@RequestParam(value = "current", defaultValue = "1") Integer current
                                    ,@RequestHeader(value = "user-info",required = false) String user) {
        return blogService.queryBlogByUserId(current, Long.valueOf(user));
    }

    @PostMapping("/update")  //更新博客
    public Result updateBlog(@ModelAttribute Blog blog, @RequestParam("file") MultipartFile file,
                             @RequestHeader(value = "user-info",required = false) String user) {
        String imagesPath = null;
        if (file.isEmpty() && blog.getImages() == null) {
            blog.setImages("删除图片");
        }
        else if (file.isEmpty() && blog.getImages() != null) {
            blog.setImages("不修改图片");
        }
        else{
            imagesPath = constFuc.saveFile(file,null);
            if (imagesPath == null) {
                return Result.fail("图片上传失败");
            }
            blog.setImages(imagesPath);
        }
        if(sensitiveWordUtil.contains(blog.getContent()))
        {
            blog.setContent(sensitiveWordUtil.replace(blog.getContent()));
        }
        if(sensitiveWordUtil.contains(blog.getTitle()))
        {
            blog.setTitle(sensitiveWordUtil.replace(blog.getTitle()));
        }
        Result result = blogService.updateBlog(blog, Long.valueOf(user));
        if (!result.getSuccess() && !file.isEmpty() && imagesPath!= null) {
            boolean deleteFile = constFuc.deleteFile(imagesPath);
        }
        return result;
    }

    @PostMapping("/delete")  //删除博客
    public Result deleteBlog(@RequestParam("id") Long id,
                             @RequestHeader(value = "user-info",required = false) String user) {
        return blogService.deleteBlog(id, Long.valueOf(user));
    }


    @PostMapping("/like")  //博客点赞
    public Result likeBlog(@RequestParam("id") Long id,
                          @RequestHeader(value = "user-info",required = false) String user) {
        return blogService.likeBlog(id, Long.valueOf(user));
    }
    @GetMapping("/search")  //博客搜索
    public Result searchBlog(@RequestParam("title") String title,
                             @RequestParam(value = "current", defaultValue = "1") Integer current,
                             @RequestHeader(value = "user-info",required = false) String user) {
        return blogService.searchBlogByTitle(title, current, user);
    }
    @GetMapping("/collect")  //博客收藏
    public Result collectBlog(@RequestParam("id") Long id,
                             @RequestHeader(value = "user-info",required = false) String user) {
        return blogService.collectBlog(id, user);
    }
    @GetMapping("/querycollect")  //查看收藏列表
    public Result queryCollectBlog(@RequestParam(value = "current", defaultValue = "1") Integer current,
            @RequestHeader(value = "user-info",required = false) String user) {
        return blogService.queryCollectBlog(current, user);
    }


}
