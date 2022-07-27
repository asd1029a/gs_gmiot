package com.danusys.web.commons.api.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

/**
 * Project : danusys-webservice-parent
 * Created by Intellij IDEA
 * Developer : ndw85
 * Date : 2022/07/13
 * Time : 16:50
 */
@Slf4j
@Component
public class DynamicScheduler {
    private ThreadPoolTaskScheduler scheduler;

    public void createScheduler(String cron, Runnable runnable) {
        scheduler = new ThreadPoolTaskScheduler();
        scheduler.initialize();
        scheduler.schedule(runnable, setTrigger(cron));
    }

    private Trigger setTrigger(String cron) {
        return new CronTrigger(cron);
    }
}


// 0 0/30 24 * 1-5 *