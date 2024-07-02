package com.example.bkgetway.config;

/**
 * @ClassName:TestController
 * @Author:DC
 * @Date:2024/7/1 17:13
 * @version:1.0
 * @Description:ada
 */
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;

@RestController
public class TestController {
    @RequestMapping("/test")
    public HashMap<String, Object> test(HttpServletResponse response) {
        // 设置跨域
        response.setHeader("Access-Control-Allow-Origin", "*");
        return new HashMap<String, Object>() {{
            put("state", 200);
            put("data", "success");
            put("msg", "");
        }};
    }
}

