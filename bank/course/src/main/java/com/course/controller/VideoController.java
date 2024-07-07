package com.course.controller;

import com.course.constfuc.ConstFuc;
import com.course.dto.Result;
import com.course.entity.Video;
import com.course.service.IVideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @ClassName:VideoController
 * @Author:DC
 * @Date:2024/7/5 14:44
 * @version:1.0
 * @Description:视频控制器
 */
@RestController
@RequestMapping("/course/video")
public class VideoController {
    @Autowired
    private ConstFuc constFuc;
    @Autowired
    private IVideoService videoService;
    static final String COURSE_VIDEO_UPLOAD_PATH = "course/src/main/resources/static/video/";
    @PostMapping("/save")
    public Result saveVideo(@ModelAttribute Video video,
                            @RequestParam("file") MultipartFile file,
                            @RequestHeader(value = "user-info",required = false) String user){
        String imagesPath = null;
        if (user == null) {
            return Result.fail("请先登录");
        }
        // 保存文件
        if (file.isEmpty()) {
            return Result.fail("视频文件不能为空");
        }
        else{
            Long time = constFuc.getDurationBackMillis(file);
            if(time == null || time == 0){
                return Result.fail("视频上传失败,时长获取失败");
            }
            System.out.println(time);
            video.setAllTime(time);
            imagesPath = constFuc.saveFile(file,COURSE_VIDEO_UPLOAD_PATH);
            if (imagesPath == null) {
                return Result.fail("视频上传失败");
            }
            video.setVideos(imagesPath);
        }
        Result result = videoService.saveVideo(video, Long.valueOf(user));
        if(!result.getSuccess()){
            boolean delete = constFuc.deleteFile(imagesPath);
        }

        return result;
    }
    @GetMapping("/list")
    public Result listVideo(@RequestParam("courseId") Long courseId,
                            @RequestHeader(value = "user-info",required = false) String user){
        if (user == null) {
            return Result.fail("请先登录");
        }
        return videoService.queryVideo(courseId, user);
    }
    @PostMapping("/update")
    public Result updateVideo(@ModelAttribute Video video,
                             @RequestParam("file") MultipartFile file,
                             @RequestHeader(value = "user-info",required = false) String user){
        String imagesPath = null;
        if (user == null) {
            return Result.fail("请先登录");
        }
        // 保存文件
        if (file.isEmpty()) {
            video.setVideos(null);
        }
        else{
            Long time = constFuc.getDurationBackMillis(file);
            if(time == null || time == 0){
                return Result.fail("视频上传失败,时长获取失败");
            }
            System.out.println(time);
            video.setAllTime(time);
            imagesPath = constFuc.saveFile(file,COURSE_VIDEO_UPLOAD_PATH);
            if (imagesPath == null) {
                return Result.fail("视频上传失败");
            }
            video.setVideos(imagesPath);
        }
        Result result = videoService.updateVideo(video);
        if(!result.getSuccess() && !file.isEmpty() && imagesPath!= null){
            boolean delete = constFuc.deleteFile(imagesPath);
        }
        return result;
    }

    @PostMapping("/record")
    public Result recordVideo(@RequestParam("courseId") Long courseId,
                             @RequestParam("id") Long id,
                             @RequestParam("time") Long time,
                             @RequestHeader(value = "user-info",required = false) String user){
        if (user == null) {
            return Result.fail("请先登录");
        }
        return videoService.recordVideoTime(courseId,id,time,user);
    }

    @PostMapping("/delete")
    public Result deleteVideo(@RequestParam("id") Long id,
                             @RequestHeader(value = "user-info",required = false) String user){
        if (user == null) {
            return Result.fail("请先登录");
        }
        return videoService.deleteVideo(id, Long.valueOf(user));
    }

}
