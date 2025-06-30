package com.live.sports.event.tracker.scheduler;

import com.live.sports.event.tracker.domain.Event;
import com.live.sports.event.tracker.domain.EventStatus;
import com.live.sports.event.tracker.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventJobTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private KafkaTemplate<String, String> kafkaTemplate;

    @Mock
    private JobExecutionContext jobExecutionContext;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EventJob eventJob;

    private Event event1;
    private Event event2;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(eventJob, "restTemplate", restTemplate);

        event1 = new Event();
        event1.setEventId("event-123");
        event1.setStatus(EventStatus.LIVE);

        event2 = new Event();
        event2.setEventId("event-456");
        event2.setStatus(EventStatus.LIVE);
    }

    @Test
    void execute_shouldProcessLiveEventsSuccessfully() throws JobExecutionException {
        // Given
        List<Event> liveEvents = Arrays.asList(event1, event2);
        String mockResponse1 = "{'eventId':'event-123','score':'2-1'}";
        String mockResponse2 = "{'eventId':'event-456','score':'0-0'}";

        when(eventRepository.findAllByStatus(EventStatus.LIVE)).thenReturn(liveEvents);
        when(restTemplate.getForObject("http://localhost:8080/mockScore/event-123", String.class))
                .thenReturn(mockResponse1);
        when(restTemplate.getForObject("http://localhost:8080/mockScore/event-456", String.class))
                .thenReturn(mockResponse2);

        // When
        eventJob.execute(jobExecutionContext);

        // Then
        verify(eventRepository).findAllByStatus(EventStatus.LIVE);
        verify(restTemplate).getForObject("http://localhost:8080/mockScore/event-123", String.class);
        verify(restTemplate).getForObject("http://localhost:8080/mockScore/event-456", String.class);
        verify(kafkaTemplate).send("score-topic", mockResponse1);
        verify(kafkaTemplate).send("score-topic", mockResponse2);
    }

    @Test
    void execute_shouldRetryFailedEventAndSucceedOnSecondAttempt() throws JobExecutionException {
        // Given
        List<Event> events = Collections.singletonList(event1);
        String mockResponse = "{'eventId':'event-123','score':'1-0'}";

        when(eventRepository.findAllByStatus(EventStatus.LIVE)).thenReturn(events);
        when(restTemplate.getForObject("http://localhost:8080/mockScore/event-123", String.class))
                .thenThrow(new RestClientException("Connection timeout"))
                .thenReturn(mockResponse);

        // When
        eventJob.execute(jobExecutionContext);

        // Then
        verify(eventRepository).findAllByStatus(EventStatus.LIVE);
        verify(restTemplate, times(2)).getForObject("http://localhost:8080/mockScore/event-123", String.class);
        verify(kafkaTemplate).send("score-topic", mockResponse);
    }

    @Test
    void execute_shouldRetryFailedEventAndSucceedOnThirdAttempt() throws JobExecutionException {
        // Given
        List<Event> events = Collections.singletonList(event1);
        String mockResponse = "{'eventId':'event-123','score':'2-1'}";

        when(eventRepository.findAllByStatus(EventStatus.LIVE)).thenReturn(events);
        when(restTemplate.getForObject("http://localhost:8080/mockScore/event-123", String.class))
                .thenThrow(new RestClientException("Connection timeout"))
                .thenThrow(new RestClientException("Server error"))
                .thenReturn(mockResponse);

        // When
        eventJob.execute(jobExecutionContext);

        // Then
        verify(eventRepository).findAllByStatus(EventStatus.LIVE);
        verify(restTemplate, times(3)).getForObject("http://localhost:8080/mockScore/event-123", String.class);
        verify(kafkaTemplate).send("score-topic", mockResponse);
    }


    @Test
    void execute_shouldThrowJobExecutionExceptionWhenAllEventsFail() {
        // Given
        List<Event> events = Arrays.asList(event1, event2);

        when(eventRepository.findAllByStatus(EventStatus.LIVE)).thenReturn(events);
        when(restTemplate.getForObject(anyString(), eq(String.class)))
                .thenThrow(new RestClientException("Persistent error"));

        // When & Then
        JobExecutionException exception = assertThrows(JobExecutionException.class,
                () -> eventJob.execute(jobExecutionContext));

        assertEquals("All 2 events failed to process after retries", exception.getMessage());

        verify(eventRepository).findAllByStatus(EventStatus.LIVE);
        // Each event should be retried 3 times
        verify(restTemplate, times(6)).getForObject(anyString(), eq(String.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    void execute_shouldHandlePartialSuccessWithRetries() throws JobExecutionException {
        // Given
        List<Event> events = Arrays.asList(event1, event2);
        String mockResponse1 = "{'eventId':'event-123','score':'1-0'}";
        String mockResponse2 = "{'eventId':'event-456','score':'2-1'}";

        when(eventRepository.findAllByStatus(EventStatus.LIVE)).thenReturn(events);

        // First event fails once then succeeds
        when(restTemplate.getForObject("http://localhost:8080/mockScore/event-123", String.class))
                .thenThrow(new RestClientException("Temporary error"))
                .thenReturn(mockResponse1);

        // Second event succeeds immediately
        when(restTemplate.getForObject("http://localhost:8080/mockScore/event-456", String.class))
                .thenReturn(mockResponse2);

        // When
        eventJob.execute(jobExecutionContext);

        // Then
        verify(eventRepository).findAllByStatus(EventStatus.LIVE);
        verify(restTemplate, times(2)).getForObject("http://localhost:8080/mockScore/event-123", String.class);
        verify(restTemplate, times(1)).getForObject("http://localhost:8080/mockScore/event-456", String.class);
        verify(kafkaTemplate).send("score-topic", mockResponse1);
        verify(kafkaTemplate).send("score-topic", mockResponse2);
    }

    @Test
    void execute_shouldHandleEmptyEventList() throws JobExecutionException {
        // Given
        when(eventRepository.findAllByStatus(EventStatus.LIVE)).thenReturn(Collections.emptyList());

        // When
        eventJob.execute(jobExecutionContext);

        // Then
        verify(eventRepository).findAllByStatus(EventStatus.LIVE);
        verify(restTemplate, never()).getForObject(anyString(), eq(String.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    void execute_shouldHandleRepositoryFailure() {
        // Given
        when(eventRepository.findAllByStatus(EventStatus.LIVE))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        JobExecutionException exception = assertThrows(JobExecutionException.class,
                () -> eventJob.execute(jobExecutionContext));

        assertEquals("Unexpected error during job execution", exception.getMessage());
        assertEquals("Database connection failed", exception.getCause().getMessage());

        verify(eventRepository).findAllByStatus(EventStatus.LIVE);
        verify(restTemplate, never()).getForObject(anyString(), eq(String.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

}