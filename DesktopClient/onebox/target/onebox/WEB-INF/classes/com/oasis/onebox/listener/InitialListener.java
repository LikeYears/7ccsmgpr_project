package com.oasis.onebox.listener;

import com.oasis.onebox.tool.RSA;
import com.oasis.onebox.entity.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class InitialListener implements ServletContextListener{
    private static final Log logger = LogFactory.getLog(User.class);
    public static String realPathHead = "";

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        realPathHead = servletContextEvent.getServletContext().getRealPath("/file");
        logger.info("RealPathHead::"+realPathHead);
        RSA.getInstance();
        User u2 = new User();
        boolean ton = u2.registerUser("user2","123456");
        logger.info("registerUser::"+ton);
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}