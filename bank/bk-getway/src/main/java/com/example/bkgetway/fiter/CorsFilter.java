package com.example.bkgetway.fiter;

/**
 * @ClassName:CorsFilter
 * @Author:DC
 * @Date:2024/7/1 14:50
 * @version:1.0
 * @Description:跨域
 */
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 配置tomcat允许跨域访问
 *
 * @author Administrator
 *
 */
public class CorsFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        // Access-Control-Allow-Origin就是我们需要设置的域名
        // Access-Control-Allow-Headers跨域允许包含的头。
        // Access-Control-Allow-Methods是允许的请求方式
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");// *,任何域名
        httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE");

        //允许客户端发一个新的请求头jwt
        httpResponse.setHeader("Access-Control-Allow-Headers","responseType,Origin,X-Requested-With, Content-Type, Accept, jwt");
        //允许客户端处理一个新的响应头jwt
        httpResponse.setHeader("Access-Control-Expose-Headers", "jwt,Content-Disposition");

        //httpResponse.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Content-Type, Accept");
        //httpResponse.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE");

        // axios的ajax会发两次请求，第一次提交方式为：option，直接返回即可
        if ("OPTIONS".equals(req.getMethod())) {
            return;
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
    @Override
    public void destroy() {

    }
}

