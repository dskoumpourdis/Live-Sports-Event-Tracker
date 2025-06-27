package com.live.sports.event.tracker.scheduler;

import lombok.RequiredArgsConstructor;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Component
@RequiredArgsConstructor
public class EventJobScheduler implements CommandLineRunner {

    private final Scheduler scheduler;

    @Override
    public void run(String... args) throws Exception {
        JobDetail jobDetail = newJob(EventJob.class)
                .withIdentity("eventJob")
                .storeDurably()
                .build();

        Trigger trigger = newTrigger()
                .forJob(jobDetail)
                .withIdentity("sampleTrigger")
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(10)
                        .repeatForever())
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }
}
