package com.reggie.filter;
import com.alibaba.fastjson.JSON;
import com.reggie.common.MyBaseContext;
import com.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径通配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        //获取本次请求URI
        String requesturi = request.getRequestURI();
        log.info("接收到请求：{}",requesturi);
        //无需拦截的uri
        String[] uris = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };

        //不需要处理的uri，直接放行
        boolean check = check(uris,requesturi);
        if(check){
            //log.info("拦截到不需要处理的请求：{}",requesturi);
            filterChain.doFilter(request,response);
            return;
        }

        //判断商户端登录状态，如果已经登录也直接放行
        if((request.getSession().getAttribute("employee")) != null){
            //客户端的每次http请求都会在服务端分配新的线程，其中过滤器dofilter，controller的update
            // 和metaobjecthandler的updatefill同属一个线程
            long id = Thread.currentThread().getId();
            //log.info("线程：{}",id);

            Long empId = (Long) request.getSession().getAttribute("employee");
            MyBaseContext.setCurrentId(empId);

            filterChain.doFilter(request,response);
            return;
        }
        //判断移动端登录状态，如果已经登录也直接放行
        if((request.getSession().getAttribute("user")) != null){
            //客户端的每次http请求都会在服务端分配新的线程，其中过滤器dofilter，controller的update
            // 和metaobjecthandler的updatefill同属一个线程
            long id = Thread.currentThread().getId();
            //log.info("线程：{}",id);

            Long userId = (Long) request.getSession().getAttribute("user");
            MyBaseContext.setCurrentId(userId);

            filterChain.doFilter(request,response);
            return;
        }
        //因前端配置了响应拦截器，request.js，所以应以输出流的方式向客户端响应数据
        //log.info("拦截到未登录的请求：{}",requesturi);
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));

        return;
    }

    //本次请求是否放行
    public boolean check(String[] uris,String requesturi){
        for(String uri : uris){
            boolean match = PATH_MATCHER.match(uri, requesturi);
            if(match){
                return true;
            }
        }
        return false;
    }
}
