package com.danusys.web.commons.api.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.atomic.AtomicInteger;

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
    private Map<Integer, ScheduledFuture<?>> scheduledMap = new ConcurrentHashMap<>();
    private AtomicInteger seq = new AtomicInteger(0);

    public DynamicScheduler(ThreadPoolTaskScheduler threadPoolTaskScheduler) {
        this.scheduler = threadPoolTaskScheduler;
    }

    public void registerScheduler(String cron, Runnable runnable) {
        ScheduledFuture<?> task = scheduler.schedule(runnable, setTrigger(cron));
        scheduledMap.put(seq.getAndIncrement(), task);
    }

    public void stopScheduler() {
        for(int i=0; i<seq.get(); i++) {
            scheduledMap.get(i).cancel(true);
        }
        seq.set(0);
    }

    private Trigger setTrigger(String cron) {
        return new CronTrigger(cron);
    }
}


// 0 0/30 24 * 1-5 *