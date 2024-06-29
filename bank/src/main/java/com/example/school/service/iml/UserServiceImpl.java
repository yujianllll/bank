package com.example.school.service.iml;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.school.dto.Result;
import com.example.school.entity.LoginFormDTO;
import com.example.school.entity.User;
import com.example.school.entity.UserDTO;
import com.example.school.mapper.UserMapper;
import com.example.school.service.IUserService;
import com.example.school.utils.RegexUtils;
import com.example.school.utils.Sember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.example.school.utils.RedisConstants.*;

@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public Result sendCode(String phone, HttpSession session) {
        // 1.校验手机号
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 2.如果不符合，返回错误信息
            return Result.fail("手机号格式错误！");
        }
        // 3.符合，生成验证码
        String code = RandomUtil.randomNumbers(6);

//        // 4.保存验证码到 session
//        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);

        // 5.发送验证码
        Sember sember = new Sember();
        boolean sent = false;
        sent = sember.sendVerificationCode(phone, code);
        // 4.保存验证码到 session
        stringRedisTemplate.opsForValue().set(LOGIN_CODE_KEY + phone, code, LOGIN_CODE_TTL, TimeUnit.MINUTES);

//        boolean sent = true;
        if (sent) {
            log.debug("发送短信验证码成功，验证码：{}", code);
            return Result.ok("发送成功");
        }else{
            log.debug("发送短信验证码失败，验证码：{}", code);
            return Result.fail("发送失败");
        }
    }

    @Override
    public Result login(LoginFormDTO loginForm, HttpSession session) {
        // 1.校验手机号
        String phone = loginForm.getPhone();
        if (RegexUtils.isPhoneInvalid(phone)) {
            // 2.如果不符合，返回错误信息
            return Result.fail("手机号格式错误！");
        }
        //从redis里面取出验证码
        String cachecode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY+phone);
        String code = loginForm.getCode();
        if (cachecode == null || !cachecode.equals(code)) {
            // 不一致，报错
            return Result.fail("验证码错误");
        }

        // 4.一致，根据手机号查询用户 select * from tb_user where phone = ?
        User user = query().eq("phone", phone).one();//mybatisplus

        // 5.判断用户是否存在
        if (user == null) {
            // 6.不存在，创建新用户并保存
            user = createUserWithPhone(phone);
        }
        // 7.保存用户信息到 redis中
        // 7.1.随机生成token，作为登录令牌
        String token = UUID.randomUUID().toString(true);
        // 7.2.将User对象转为HashMap存储
        UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);//转成UserDTO对象
        Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                CopyOptions.create()
                        .setIgnoreNullValue(true)
                        .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
        // 7.3.存储
        String tokenKey = LOGIN_USER_KEY + token;
//        for(Map.Entry<String, Object> mp: userMap.entrySet())
        stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
        // 7.4.设置token有效期
        stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);

        // 8.返回token
        return Result.ok(token);
    }

    @Override
    public Result loginbypassword(LoginFormDTO loginFormDTO, HttpSession session) {
        String phone = loginFormDTO.getPhone();
        User user = query().eq("phone",phone).one();
        if(user==null)
        {
            return Result.fail("输入用户无效");
        }
        else if(loginFormDTO.getPassword().equals(user.getPassword())){
            // 7.1.随机生成token，作为登录令牌
            String token = UUID.randomUUID().toString(true);
            // 7.2.将User对象转为HashMap存储
            UserDTO userDTO = BeanUtil.copyProperties(user, UserDTO.class);//转成UserDTO对象
            Map<String, Object> userMap = BeanUtil.beanToMap(userDTO, new HashMap<>(),
                    CopyOptions.create()
                            .setIgnoreNullValue(true)
                            .setFieldValueEditor((fieldName, fieldValue) -> fieldValue.toString()));
            // 7.3.存储
            String tokenKey = LOGIN_USER_KEY + token;
//        for(Map.Entry<String, Object> mp: userMap.entrySet())
            stringRedisTemplate.opsForHash().putAll(tokenKey, userMap);
            // 7.4.设置token有效期
            stringRedisTemplate.expire(tokenKey, LOGIN_USER_TTL, TimeUnit.MINUTES);

            // 8.返回token
            return Result.ok(token);
        }
        else{
            return Result.fail("输入密码错误");
        }
    }

    @Override
    public Result refindphone(LoginFormDTO loginFormDTO, HttpSession session) {
        String lastphone = loginFormDTO.getPhone();
        // 3.符合，生成验证码
        String cachecode = stringRedisTemplate.opsForValue().get(LOGIN_CODE_KEY+lastphone);
        User user = query().eq("phone",lastphone).one();
        System.out.println(cachecode);
        System.out.println(loginFormDTO.getCode());
        if(!cachecode.equals(loginFormDTO.getCode()))
        {
            return Result.fail("验证码输入错误");
        }
        else if(!user.getPassword().equals(loginFormDTO.getPassword()))
        {
            return Result.fail("密码输入错误");
        }
        UpdateWrapper<User> wrapper = new UpdateWrapper<>();
        wrapper.eq("phone",lastphone).set("phone",loginFormDTO.getNewphone()).set("maxchange",user.getMaxchange()+1);
        boolean updated = update(null,wrapper);
        if(!updated)
        {
            return Result.fail("更新失败，请稍后尝试");
        }
        String exchangeName = "finduser";
        loginFormDTO.setMaxchange(user.getMaxchange()+1);
        rabbitTemplate.convertAndSend(exchangeName,null,loginFormDTO);
        return Result.ok("修改成功");
    }

    private User createUserWithPhone(String phone) {
        // 1.创建用户
        User user = new User();
        user.setPhone(phone);
        user.setNickName("user_" + RandomUtil.randomString(10));
        user.setMaxchange(1);
        // 2.保存用户 mybatisplus的保存
        save(user);
        return user;
    }
}
