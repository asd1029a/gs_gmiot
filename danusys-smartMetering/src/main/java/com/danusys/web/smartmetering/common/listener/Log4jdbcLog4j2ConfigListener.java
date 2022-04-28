/*package com.danusys.smartmetering.common.listener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.InputStream;
import java.util.Properties;

public class Log4jdbcLog4j2ConfigListener implements ServletContextListener {

	public Log4jdbcLog4j2ConfigListener() {

	}

    @Override
    public void contextInitialized(ServletContextEvent sce) {
    	String active = System.getProperty("spring.profiles.active");
        InputStream is = getClass().getResourceAsStream("/message/global-"+ active +".properties");
        Properties props = new Properties();

        try {
			props.load(is);
			//String dbType = (String) props.get("system.dbType1");
			//System.setProperty("log4jdbc.log4j2.properties.file", "/message/log4jdbc.log4j2-" + dbType + ".properties");

			String log4jPath = "/log4j2-" + active + ".xml";

		//	LoggerContext context = (LoggerContext) LogManager.getContext(false);
		//	context.setConfigLocation(this.getClass().getResource(log4jPath).toURI());

			//System.out.println(this.getClass().getResource(log4jPath).toURI());
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {

    }
}*/