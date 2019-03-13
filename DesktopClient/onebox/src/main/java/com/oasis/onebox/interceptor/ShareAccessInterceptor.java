package com.oasis.onebox.interceptor;

import com.oasis.onebox.entity.FileShare;
import com.oasis.onebox.service.ShareFileService;
import com.oasis.onebox.tool.StringTool;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ShareAccessInterceptor implements HandlerInterceptor {
    private static final String LINK_PARAMETER = "shareid";
    private static final String PWD_PARAMETER = "password";
    public static final String SHARE_FILE = "fileshare";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object o) throws Exception {
        String shareID = request.getParameter(LINK_PARAMETER);
        if (StringTool.isNullOrEmpty(shareID)) {
            constructResponse(response, HttpServletResponse.SC_BAD_REQUEST, "shareId is null");
            return false;
        }
        String password = request.getParameter(PWD_PARAMETER);
        if (StringTool.isNullOrEmpty(password)) {
            constructResponse(response, HttpServletResponse.SC_BAD_REQUEST, "password is null");
            return false;
        }
        FileShare fileShare = ShareFileService.searchShareByID(shareID);
        if (fileShare == null) {
            constructResponse(response, HttpServletResponse.SC_NOT_FOUND, "file share is canceled");
            return false;
        } else if (!fileShare.getPassword().equals(password)) {
            constructResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "incorrect password");
            response.getWriter().write("incorrect password");
            return false;
        }
        request.setAttribute(SHARE_FILE, fileShare);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }

    private static void constructResponse(HttpServletResponse response, int responseCode, String msg) throws IOException {
        response.setContentType("text/plain;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(msg);
    }
}
