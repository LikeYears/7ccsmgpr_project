package com.oasis.onebox.interceptor;

import com.oasis.onebox.entity.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MobileInterceptor implements HandlerInterceptor {
    //cookie token in android
    public static final String TOKEN_NAME = "onebox";
    public static final String LOGIN_USER = "loginUser";
    @Override
    public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2) throws Exception {
        String token = arg0.getParameter(TOKEN_NAME);

        User u = User.checkMobileToken(token);
        if (u == null) {
            setAuthFailResponse(arg1);
            return false;
        }
        arg0.setAttribute(LOGIN_USER, u);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    public void setAuthFailResponse(HttpServletResponse arg1) throws Exception {
        arg1.setContentType("text/plain;charset=UTF-8");
        arg1.setStatus(401);
        arg1.getWriter().write("Auth Fail, Please Login Again");
    }
}
