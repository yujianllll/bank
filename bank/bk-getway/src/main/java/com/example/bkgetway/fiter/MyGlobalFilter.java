package com.example.bkgetway.fiter;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.example.bkgetway.entity.UserDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.annotation.Order;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.PathContainer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Order(0)
@Component
public class MyGlobalFilter implements GlobalFilter {
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static final String LOGIN_URI = "/user/login";
    private static final String CODE_URI = "/user/code";
    private static final String memo_URI = "/user/memo";
    private static final String SHOP_URI = "/solde/**";
    private static final String LOGIN_URIBYPASS = "/user/loginbypassword";
    private final PathPattern shopPattern = new PathPatternParser().parse(SHOP_URI);
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request =  exchange.getRequest();
        String path = request.getURI().getPath();
        // 如果请求是登录或者验证码请求，不进行拦截
        if (LOGIN_URI.equals(path) || CODE_URI.equals(path)||shopPattern.matches(PathContainer.parsePath(path))||LOGIN_URIBYPASS.equals(path)) {
            return chain.filter(exchange);
        }
        HttpHeaders headers = request.getHeaders();
        System.out.println("headers"+headers);
        List<String> token = request.getHeaders().get("authorization");
        String token1 = null;
        if (token != null) {
            token1 = token.get(0);
        }

        if (StrUtil.isBlank(token1)) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        String token2 = "login:token:" + token1;
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(token2);
        // 3.判断用户是否存在
        if (userMap.isEmpty()) {
            ServerHttpResponse response = exchange.getResponse();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        // 5.将查询到的hash数据转为UserDTO
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
        // 将用户信息存放到ServerWebExchange中，这样后续的服务可以获取到
        ServerWebExchange updatedExchange = exchange.mutate()
                .request(builder -> builder.header("user-info", userDTO.getId().toString()))
                .build();
//        // 从ServerWebExchange中获取存储的用户信息
//        String userInfo = exchange.getRequest().getHeaders().getFirst("user-info");
        System.out.println(token1);
        return chain.filter(updatedExchange);
    }
//    @GetMapping("/secure")
//    @ResponseBody
//    public String secureEndpoint(ServerWebExchange exchange) {
//        // 从ServerWebExchange中获取存储的用户信息
//        String userInfo = exchange.getRequest().getHeaders().getFirst("user-info");
//
//        // 在实际应用中，根据userInfo进行相应的业务逻辑处理，例如权限验证等
//        if (userInfo != null) {
//            return "Access granted for user: " + userInfo;
//        } else {
//            return "Access denied";
//        }
//    }
}
