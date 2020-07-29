package com.boss.springcloud.controller.intercepter;

import com.boss.springcloud.annotation.AuthRequired;
import com.boss.springcloud.annotation.LoginRequired;
import com.boss.springcloud.entity.dto.UserDTO;
import com.boss.springcloud.util.CommenUtil;
import com.boss.springcloud.util.HostHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Component
public class UserIntercepter implements HandlerInterceptor {
    private Logger logger =LoggerFactory.getLogger(UserIntercepter.class);

    @Autowired
    private HostHolder hostHolder;

    @Autowired
    private HttpSession httpSession;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {

            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            AuthRequired authRequired = method.getAnnotation(AuthRequired.class);
            UserDTO user = (UserDTO) httpSession.getAttribute("user");
            //logger.info(hostHolder.getUser().toString());
            //登录验证
            if (!loginValidate(loginRequired, response, request, user)) {
                return false;
            }
            //权限验证
            if(!authValidate(authRequired, response, request, user)) {
                return false;
            }
        }
        return true;
    }

    //login验证
    public boolean loginValidate(LoginRequired loginRequired, HttpServletResponse response, HttpServletRequest request, UserDTO user) throws IOException {
        if (loginRequired != null && user == null) {
            //response.sendRedirect(request.getContextPath() + "/login");
            response.setHeader("content-type", "text/json;charset=UTF-8");
            Map<String, Object> map= new HashMap<>();
            map.put("msg", "您还未登录，请先登录。");
            map.put("url", request.getContextPath()+"/user/login");
            String msg = CommenUtil.getJSONString(0, "登录验证", map);
            response.getWriter().write(msg);
            return false;
        }
        return true;
    }
    //权限验证
    public boolean authValidate (AuthRequired authRequired, HttpServletResponse response, HttpServletRequest request, UserDTO user)throws IOException {
        //带有权限认证注解，并且权限低于所要求的的权限
        if (authRequired != null && user.getAuthorityId() > authRequired.value().getAuthority_id()) {
            response.setHeader("content-type", "text/json;charset=UTF-8");
            Map<String, Object> map= new HashMap<>();
            map.put("msg", "您没有权限操作哦。");
            String msg = CommenUtil.getJSONString(0, "权限认证", map);
            response.getWriter().write(msg);
            return false;
        }
        return true;
    }
}
