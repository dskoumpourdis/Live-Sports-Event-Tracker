package com.live.sports.event.tracker.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventJobScheduler implements CommandLineRunner {

    private final Scheduler scheduler;

    // Retry configuration for job scheduling
    private static final int MAX_SCHEDULE_RETRIES = 5;
    private static final long SCHEDULE_RETRY_DELAY_MS = 2000; // 2 seconds
    private static final double SCHEDULE_BACKOFF_MULTIPLIER = 1.5;

    @Override
    public void run(String... args) throws Exception {
        scheduleJobWithRetry();
    }

    private void scheduleJobWithRetry() throws Exception {
        int attempt = 0;
        long delay = SCHEDULE_RETRY_DELAY_MS;
        Exception lastException = null;

        while (attempt < MAX_SCHEDULE_RETRIES) {
            try {
                scheduleJob();
                if (attempt > 0) {
                    log.info("Successfully scheduled EventJob after {} retries", attempt);
                } else {
                    log.info("Successfully scheduled EventJob on first attempt");
                }
                return;

            } catch (SchedulerException e) {
                attempt++;
                lastException = e;
                log.warn("Attempt {} to schedule EventJob failed: {}", attempt, e.getMessage());

                if (attempt >= MAX_SCHEDULE_RETRIES) {
                    log.error("Failed to schedule EventJob after {} attempts", MAX_SCHEDULE_RETRIES, e);
                    throw new Exception("Failed to schedule EventJob after " + MAX_SCHEDULE_RETRIES + " attempts", e);
                }

                // Wait before retrying with exponential backoff
                try {
                    log.info("Waiting {}ms before retry attempt {}", delay, attempt + 1);
                    Thread.sleep(delay);
                    delay = (long) (delay * SCHEDULE_BACKOFF_MULTIPLIER);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("Job scheduling retry interrupted");
                    throw new Exception("Job scheduling retry interrupted", ie);
                }
            }
        }

        // This should never be reached due to the logic above, but just in case
        throw new Exception("Failed to schedule EventJob", lastException);
    }

    private void scheduleJob() throws SchedulerException {
        log.info("Attempting to schedule EventJob...");

        JobDetail jobDetail = newJob(EventJob.class)
                .withIdentity("eventJob", "eventGroup")
                .storeDurably()
                .requestRecovery(true) // Recover job if scheduler restarts
                .build();

        Trigger trigger = newTrigger()
                .forJob(jobDetail)
                .withIdentity("eventTrigger", "eventGroup")
                .withSchedule(simpleSchedule()
                        .withIntervalInSeconds(10)
                        .withMisfireHandlingInstructionFireNow() // Handle misfires by executing immediately
                        .repeatForever())
                .build();

        // Check if job already exists and remove it first
        if (scheduler.checkExists(jobDetail.getKey())) {
            log.info("Job {} already exists, removing it first", jobDetail.getKey());
            scheduler.deleteJob(jobDetail.getKey());
        }

        scheduler.scheduleJob(jobDetail, trigger);
        log.info("EventJob scheduled successfully with 10-second interval");
    }
}