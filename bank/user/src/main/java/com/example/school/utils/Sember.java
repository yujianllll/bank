package com.example.school.utils;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class Sember {

    private static String username = "jhy123_";


    private static String apiKey = "c15fae1c98854433addae5e356a1628b";
//
//    private static final OkHttpClient httpClient = new OkHttpClient();
//    @Value("${smsbao.username}")
//    private  String username;
//
//    @Value("${smsbao.apiKey}")
//    private  String apiKey;

    private  final OkHttpClient httpClient = new OkHttpClient();
    public boolean sendVerificationCode(String phoneNumber, String verificationCode) {
        // 注意：需要对 content 进行 URL 编码，确保特殊字符被正确处理
        String content = "【小鱼小计】您的验证码是" + verificationCode + ",120秒内有效";

        // 构造请求URL
        String apiUrl = "https://api.smsbao.com/sms";
        String url = String.format("%s?u=%s&p=%s&m=%s&c=%s", apiUrl, username, apiKey, phoneNumber, content);
        System.out.println(url);
        // 创建请求对象
        Request request = new Request.Builder()
                .url(url)
                .build();

        // 发送请求并处理响应
        try (Response response = httpClient.newCall(request).execute()) {
            String responseBody = response.body().string();
            return response.isSuccessful() && responseBody.startsWith("0");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}

