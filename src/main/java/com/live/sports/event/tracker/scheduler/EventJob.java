package com.live.sports.event.tracker.scheduler;

import com.live.sports.event.tracker.domain.Event;
import com.live.sports.event.tracker.domain.EventStatus;
import com.live.sports.event.tracker.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EventJob implements Job {

    private final EventRepository eventRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private final String URL = "http://localhost:8080/mockScore";
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC = "score-topic";

    // Retry configuration
    private static final int MAX_RETRIES = 3;
    private static final long RETRY_DELAY_MS = 1000; // 1 second
    private static final double BACKOFF_MULTIPLIER = 2.0;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        try {
            List<Event> events = eventRepository.findAllByStatus(EventStatus.LIVE);
            log.info("Processing {} live events", events.size());

            int successCount = 0;
            int failureCount = 0;

            for (Event event : events) {
                if (processEventWithRetry(event)) {
                    successCount++;
                } else {
                    failureCount++;
                }
            }

            log.info("Job completed. Success: {}, Failures: {}", successCount, failureCount);

            // If all events failed and there were events to process, refire the job
            if (failureCount > 0 && successCount == 0 && !events.isEmpty()) {
                JobExecutionException jobException = new JobExecutionException(
                        String.format("All %d events failed to process after retries", failureCount)
                );
                boolean willRefire = jobException.refireImmediately();
                log.warn("Job failed completely, will refire immediately: {}", willRefire);
                throw jobException;
            }

        } catch (JobExecutionException e) {
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during job execution", e);
            JobExecutionException jobException = new JobExecutionException("Unexpected error during job execution", e);
            boolean willRefire = jobException.refireImmediately();
            log.warn("Job failed with unexpected error, will refire immediately: {}", willRefire);
            throw jobException;
        }
    }

    private boolean processEventWithRetry(Event event) {
        int attempt = 0;
        long delay = RETRY_DELAY_MS;

        while (attempt < MAX_RETRIES) {
            try {
                String response = restTemplate.getForObject(URL + "/" + event.getEventId(), String.class);
                kafkaTemplate.send(TOPIC, response);

                if (attempt > 0) {
                    log.info("Successfully processed event {} after {} retries", event.getEventId(), attempt);
                } else {
                    log.debug("Successfully processed event {} on first attempt", event.getEventId());
                }
                return true;

            } catch (Exception e) {
                attempt++;
                log.warn("Attempt {} failed for event {}: {}", attempt, event.getEventId(), e.getMessage());

                if (attempt >= MAX_RETRIES) {
                    log.error("Failed to process event {} after {} attempts", event.getEventId(), MAX_RETRIES, e);
                    return false;
                }

                // Wait before retrying with exponential backoff
                try {
                    Thread.sleep(delay);
                    delay = (long) (delay * BACKOFF_MULTIPLIER);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("Retry interrupted for event {}", event.getEventId());
                    return false;
                }
            }
        }
        return false;
    }
}