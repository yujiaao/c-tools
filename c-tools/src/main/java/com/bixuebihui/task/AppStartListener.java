package com.bixuebihui.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.http.HttpServlet;


/**
 * @author xwx
 */
public class AppStartListener extends HttpServlet implements ServletContextListener {
    private final static Logger LOG = LoggerFactory.getLogger(AppStartListener.class);
    /**
     *
     */
    private static final long serialVersionUID = 3105513079977240099L;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        try {
            LOG.info("[CYC] 自动监控任务启动:" + ScheduleMonitor.run());
        } catch (Exception e) {
            LOG.warn("", e);
        }
        event.getServletContext().log("AppStartListener Launch!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        try {
            ScheduleMonitor.shutdown();
        } catch (Exception e) {
            LOG.warn("contextDestroyed", e);
        }
        event.getServletContext().log("Destory AppStartListener!");
        LOG.info("[CYC] Application Listener has been destoryed!");
    }


}
