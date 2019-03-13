package com.oasis.onebox.listener;

import com.oasis.onebox.download.DownloadTask;
import com.oasis.onebox.tool.RSA;
import com.oasis.onebox.entity.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.util.Set;

public class InitialListener implements ServletContextListener{
    private static final Log logger = LogFactory.getLog(User.class);
    public static String realPathHead = "";
    public static Set<String> nextPathHead;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        realPathHead = servletContextEvent.getServletContext().getRealPath("/file");
        logger.info("RealPathHead::"+realPathHead);
        RSA.getInstance();
        //register test
        User u2 = new User();
        boolean ton = u2.registerUser("user2","123456");
        logger.info("registerUser::"+ton);
        //download test
        DownloadTask.initDownloadTasks();
        logger.info("initDownload success");
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}