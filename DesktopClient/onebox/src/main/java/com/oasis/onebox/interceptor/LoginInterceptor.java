package com.oasis.onebox.interceptor;

import com.oasis.onebox.tool.StringTool;
import com.oasis.onebox.entity.User;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor{
    //cookie token in web
    public static final String TOKEN_NAME = "onebox";
    public static final String LOGIN_USER = "loginUser";

    public void afterCompletion(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, Exception arg3)
            throws Exception {

    }

    public void postHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2, ModelAndView arg3)
            throws Exception {

    }

    //get token from cookie to auth
    public boolean preHandle(HttpServletRequest arg0, HttpServletResponse arg1, Object arg2) throws Exception {
        String token = arg0.getParameter(TOKEN_NAME);
        if (StringTool.isNullOrEmpty(token)) {
            Cookie[] cookies = arg0.getCookies();
            if (cookies != null) {
                for (Cookie c : cookies) {
                    if (c.getName().equals(TOKEN_NAME)) {
                        token = c.getValue();
                        break;
                    }
                }
            }
            if (StringTool.isNullOrEmpty(token)) {
                setAuthFailResponse(arg1);
                return false;
            }
        }
        User u = User.checkToken(token, arg0);
        if (u == null) {
            setAuthFailResponse(arg1);
            return false;
        }
        arg0.setAttribute(LOGIN_USER, u);
        return true;
    }

    public void setAuthFailResponse(HttpServletResponse arg1) throws Exception {
        arg1.setContentType("text/plain;charset=UTF-8");
        arg1.setStatus(401);
        arg1.getWriter().write("Auth Fail, Please Login Again");
    }
}
