package com.live.sports.event.tracker.scheduler;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.diffblue.cover.annotations.MethodsUnderTest;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Date;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {EventJobScheduler.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class EventJobSchedulerDiffblueTest {
    @Autowired
    private EventJobScheduler eventJobScheduler;

    @MockitoBean
    private Scheduler scheduler;

    /**
     * Test {@link EventJobScheduler#run(String[])}.
     * <p>
     * Method under test: {@link EventJobScheduler#run(String[])}
     */
    @Test
    @DisplayName("Test run(String[])")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"void EventJobScheduler.run(String[])"})
    void testRun() throws Exception {
        // Arrange
        when(scheduler.scheduleJob(Mockito.<JobDetail>any(), Mockito.<Trigger>any()))
                .thenReturn(Date.from(LocalDate.of(1970, 1, 1).atStartOfDay().atZone(ZoneOffset.UTC).toInstant()));

        // Act
        eventJobScheduler.run("Args");

        // Assert
        verify(scheduler).scheduleJob(isA(JobDetail.class), isA(Trigger.class));
    }
}
