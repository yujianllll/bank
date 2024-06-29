package com.example.school.controller;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.example.school.dto.Result;
import com.example.school.entity.LoginFormDTO;
import com.example.school.entity.User;
import com.example.school.entity.UserDTO;
import com.example.school.entity.UserHolder;
import com.example.school.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import static com.example.school.utils.RegexUtils.isPassword;

@Slf4j
@RestController
@RequestMapping("/user")
public class Usercontroller {
    @Resource
    private IUserService userService;

    @PostMapping("code")
    public Result sendCode(@RequestParam("phone") String phone, HttpSession session) {
        // 发送短信验证码并保存验证码
        return userService.sendCode(phone, session);
    }

    @PostMapping("/login")
    public Result login(@RequestBody LoginFormDTO loginForm, HttpSession session) {
        // 实现登录功能
//        return userService.login(loginForm, session);
        return userService.login(loginForm, session);
//        return Result.ok();
    }
    @PostMapping("loginbypassword")
    public Result loginbypassword(@RequestBody LoginFormDTO loginFormDTO,HttpSession session)
    {
        return userService.loginbypassword(loginFormDTO,session);
    }
    @PostMapping("/logout")
    public Result logout() {
        // TODO 实现登出功能
        UserHolder.removeUser();
        return Result.ok();
    }
    @PostMapping("memo")
    public Result setname(@RequestParam("password") String password)
    {
        UserDTO userDTO = UserHolder.getUser();
        if(!isPassword(password))
        {
            return Result.fail("请输入密码为4-32位，包含数字或者下划线或者字母三选二的密码");
        }
        User user = userService.query().eq("id",userDTO.getId()).one();
        // 创建 UpdateWrapper，设置更新条件和更新字段
        UpdateWrapper<User> wrapper = new UpdateWrapper<User>();
        wrapper.eq("id", userDTO.getId())  // 设置更新条件：id = 111
                .set("password", password).set("maxchange",user.getMaxchange()+1);  // 设置要更新的字段和新的密码值

        // 调用 update 方法执行更新操作
        boolean updated = userService.update(null, wrapper);

        if (updated) {
            return Result.ok("密码更新成功");
        } else {
            return Result.fail("密码更新失败");
        }
    }
    @GetMapping("/me")
    public Result me(@RequestParam("phone") String phone) {
        // 获取当前登录的用户并返回
        User user1 = userService.query().eq("phone",phone).one();
        UserDTO user = UserHolder.getUser();
        return Result.ok(user1);
    }
    @PostMapping("retryphone")
    public Result retryphone(@RequestBody LoginFormDTO loginFormDTO,HttpSession session)
    {
        return userService.refindphone(loginFormDTO,session);
    }
    //更换绑定手机号

}
