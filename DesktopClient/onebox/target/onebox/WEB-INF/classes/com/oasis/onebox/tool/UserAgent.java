package com.oasis.onebox.tool;

import javax.servlet.http.HttpServletRequest;

public class UserAgent {

    public static String getIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.equals("")) {
            ip = "unknown";
        } else {
            ip = request.getHeader("X-Real-IP");
            if (ip == null || ip.equals("")) {
                ip = "unknown";
            }
        }
        return ip;
    }


    public static int getUserAgentID(HttpServletRequest request) {
        return eu.bitwalker.useragentutils.UserAgent.parseUserAgentString(request.getHeader("User-Agent")).getId();
    }
}
