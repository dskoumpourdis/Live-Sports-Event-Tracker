package com.live.sports.event.tracker.scheduler;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventJobSchedulerTest {

    @Mock
    private Scheduler scheduler;

    @InjectMocks
    private EventJobScheduler eventJobScheduler;

    private JobKey jobKey;

    @BeforeEach
    void setUp() {
        jobKey = new JobKey("eventJob", "eventGroup");
    }

    @Test
    void run_shouldScheduleJobSuccessfully() throws Exception {
        // Given
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);

        // When
        eventJobScheduler.run();

        // Then
        verify(scheduler).checkExists(any(JobKey.class));
        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
        verify(scheduler, never()).deleteJob(any(JobKey.class));
    }

    @Test
    void run_shouldDeleteExistingJobBeforeScheduling() throws Exception {
        // Given
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);
        when(scheduler.deleteJob(any(JobKey.class))).thenReturn(true);

        // When
        eventJobScheduler.run();

        // Then
        verify(scheduler).checkExists(any(JobKey.class));
        verify(scheduler).deleteJob(any(JobKey.class));
        verify(scheduler).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void run_shouldRetryOnSchedulerException() throws Exception {
        // Given
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
                .thenThrow(new SchedulerException("Scheduler not ready"))
                .thenAnswer(invocation -> null); // Success on second attempt

        // When
        eventJobScheduler.run();

        // Then
        verify(scheduler, times(2)).checkExists(any(JobKey.class));
        verify(scheduler, times(2)).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void run_shouldRetryMultipleTimesBeforeSuccess() throws Exception {
        // Given
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
                .thenThrow(new SchedulerException("First failure"))
                .thenThrow(new SchedulerException("Second failure"))
                .thenThrow(new SchedulerException("Third failure"))
                .thenAnswer(invocation -> null); // Success on fourth attempt

        // When
        eventJobScheduler.run();

        // Then
        verify(scheduler, times(4)).checkExists(any(JobKey.class));
        verify(scheduler, times(4)).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void run_shouldFailAfterMaxRetries() throws Exception {
        // Given
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
                .thenThrow(new SchedulerException("Persistent scheduler error"));

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> eventJobScheduler.run());

        assertEquals("Failed to schedule EventJob after 5 attempts", exception.getMessage());
        assertInstanceOf(SchedulerException.class, exception.getCause());
        assertEquals("Persistent scheduler error", exception.getCause().getMessage());

        verify(scheduler, times(5)).checkExists(any(JobKey.class));
        verify(scheduler, times(5)).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void run_shouldHandleExistingJobDeletionFailure() throws Exception {
        // Given
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(true);
        when(scheduler.deleteJob(any(JobKey.class)))
                .thenThrow(new SchedulerException("Failed to delete job"))
                .thenReturn(true); // Success on retry

        // When
        eventJobScheduler.run();

        // Then
        verify(scheduler, times(2)).checkExists(any(JobKey.class));
        verify(scheduler, times(2)).deleteJob(any(JobKey.class));
        verify(scheduler, times(1)).scheduleJob(any(JobDetail.class), any(Trigger.class));
    }

    @Test
    void run_shouldHandleInterruptedRetry() throws Exception {
        // Given
        when(scheduler.checkExists(any(JobKey.class))).thenReturn(false);
        when(scheduler.scheduleJob(any(JobDetail.class), any(Trigger.class)))
                .thenThrow(new SchedulerException("Scheduler error"));

        // Interrupt the current thread to simulate interruption during retry delay
        Thread.currentThread().interrupt();

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> eventJobScheduler.run());

        assertEquals("Job scheduling retry interrupted", exception.getMessage());
        assertInstanceOf(InterruptedException.class, exception.getCause());

        verify(scheduler, times(1)).checkExists(any(JobKey.class));
        verify(scheduler, times(1)).scheduleJob(any(JobDetail.class), any(Trigger.class));

        // Clear interrupt flag for cleanup
        Thread.interrupted();
    }

    @Test
    void run_shouldHandleCheckExistsFailure() throws Exception {
        // Given
        when(scheduler.checkExists(any(JobKey.class)))
                .thenThrow(new SchedulerException("Check exists failed"))
                .thenReturn(false); // Success on retry

        // When
        eventJobScheduler.run();

        // Then
        verify(scheduler, times(2)).checkExists(any(JobKey.class));
        verify(scheduler, times(1)).scheduleJob(any(JobDetail.class), any(Trigger.class));
        verify(scheduler, never()).deleteJob(any(JobKey.class));
    }
}