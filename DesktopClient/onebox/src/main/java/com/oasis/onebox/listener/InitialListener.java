package com.oasis.onebox.listener;

import com.oasis.onebox.tool.RSA;
import com.oasis.onebox.entity.User;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class InitialListener implements ServletContextListener{
    private static final Log logger = LogFactory.getLog(User.class);

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        RSA.getInstance();
        readConfig();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }

    private void readConfig() {
//        //register test
//        boolean register = User.registerUser("test","123456","/test");
//        logger.info("REGISTER:"+register);

    }

}
