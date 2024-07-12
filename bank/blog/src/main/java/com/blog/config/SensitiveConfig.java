package com.blog.config;

import com.github.houbb.sensitive.word.api.IWordAllow;
import com.github.houbb.sensitive.word.api.IWordDeny;
import com.github.houbb.sensitive.word.bs.SensitiveWordBs;
import com.github.houbb.sensitive.word.support.allow.WordAllows;
import com.github.houbb.sensitive.word.support.deny.WordDenys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class SensitiveConfig {

    // 配置默认敏感词 + 自定义敏感词
    IWordDeny wordDeny = WordDenys.chains(WordDenys.system(), new MyWordDeny());
    // 配置默认非敏感词 + 自定义非敏感词
    IWordAllow wordAllow = WordAllows.chains(WordAllows.system(), new MyWordAllow());

    @Bean
    public SensitiveWordBs sensitiveWordBs() {
        return SensitiveWordBs.newInstance()
                // 忽略大小写
                .ignoreCase(true)
                // 忽略半角圆角
                .ignoreWidth(true)
                // 忽略数字的写法
                .ignoreNumStyle(true)
                // 忽略中文的书写格式：简繁体
                .ignoreChineseStyle(true)
                // 忽略英文的书写格式
                .ignoreEnglishStyle(true)
                // 忽略重复词
                .ignoreRepeat(false)
                // 是否启用数字检测
                .enableNumCheck(true)
                // 是否启用邮箱检测
                .enableEmailCheck(true)
                // 是否启用链接检测
                .enableUrlCheck(true)
                // 数字检测，自定义指定长度
                .numCheckLen(8)
                // 配置自定义敏感词
                .wordDeny(wordDeny)
                // 配置非自定义敏感词
                .wordAllow(wordAllow)
                .init();
    }
    @Slf4j
    public static class MyWordDeny implements IWordDeny {

        @Override
        public List<String> deny() {
            List<String> list = new ArrayList<>();
            try {
                Resource mySensitiveWords = new ClassPathResource("mySensitiveWords.txt");
                Path mySensitiveWordsPath = Paths.get(mySensitiveWords.getFile().getPath());
                list = Files.readAllLines(mySensitiveWordsPath, StandardCharsets.UTF_8);
            } catch (IOException ioException) {
                log.error("读取敏感词文件错误！" + ioException.getMessage());
            }
            return list;
        }

    }
    @Slf4j
    public static class MyWordAllow implements IWordAllow {

        @Override
        public List<String> allow() {
            List<String> list = new ArrayList<>();
            ;
            try {
                Resource myAllowWords = new ClassPathResource("myNotSensitiveWords.txt");
                Path myAllowWordsPath = Paths.get(myAllowWords.getFile().getPath());
                list = Files.readAllLines(myAllowWordsPath, StandardCharsets.UTF_8);
            } catch (IOException ioException) {
                log.error("读取非敏感词文件错误！" + ioException.getMessage());
            }
            return list;
        }

    }



}

