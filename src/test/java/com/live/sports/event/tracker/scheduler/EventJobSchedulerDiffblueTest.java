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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

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
        // Inject the mocked RestTemplate into the EventJob
        ReflectionTestUtils.setField(eventJob, "restTemplate", restTemplate);

        // Create test events
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
    void execute_shouldHandleSingleEvent() throws JobExecutionException {
        // Given
        List<Event> singleEvent = Collections.singletonList(event1);
        String mockResponse = "{'eventId':'event-123','score':'1-0'}";

        when(eventRepository.findAllByStatus(EventStatus.LIVE)).thenReturn(singleEvent);
        when(restTemplate.getForObject("http://localhost:8080/mockScore/event-123", String.class))
                .thenReturn(mockResponse);

        // When
        eventJob.execute(jobExecutionContext);

        // Then
        verify(eventRepository).findAllByStatus(EventStatus.LIVE);
        verify(restTemplate).getForObject("http://localhost:8080/mockScore/event-123", String.class);
        verify(kafkaTemplate).send("score-topic", mockResponse);
    }

    @Test
    void execute_shouldStopProcessingWhenRestTemplateThrowsException() {
        // Given
        List<Event> events = Arrays.asList(event1, event2);

        when(eventRepository.findAllByStatus(EventStatus.LIVE)).thenReturn(events);
        when(restTemplate.getForObject("http://localhost:8080/mockScore/event-123", String.class))
                .thenThrow(new RestClientException("Connection timeout"));

        // When & Then
        assertThrows(RestClientException.class, () -> eventJob.execute(jobExecutionContext));

        verify(eventRepository).findAllByStatus(EventStatus.LIVE);
        verify(restTemplate).getForObject("http://localhost:8080/mockScore/event-123", String.class);

        // Second event should not be processed due to exception in first event
        verify(restTemplate, never()).getForObject("http://localhost:8080/mockScore/event-456", String.class);
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    void execute_shouldHandleNullResponseFromRestTemplate() throws JobExecutionException {
        // Given
        List<Event> events = Collections.singletonList(event1);

        when(eventRepository.findAllByStatus(EventStatus.LIVE)).thenReturn(events);
        when(restTemplate.getForObject("http://localhost:8080/mockScore/event-123", String.class))
                .thenReturn(null);

        // When
        eventJob.execute(jobExecutionContext);

        // Then
        verify(eventRepository).findAllByStatus(EventStatus.LIVE);
        verify(restTemplate).getForObject("http://localhost:8080/mockScore/event-123", String.class);
        verify(kafkaTemplate).send("score-topic", null);
    }

    @Test
    void execute_shouldStopProcessingWhenKafkaTemplateThrowsException() {
        // Given
        List<Event> events = Arrays.asList(event1, event2);
        String mockResponse1 = "{'eventId':'event-123','score':'1-1'}";

        when(eventRepository.findAllByStatus(EventStatus.LIVE)).thenReturn(events);
        when(restTemplate.getForObject("http://localhost:8080/mockScore/event-123", String.class))
                .thenReturn(mockResponse1);

        doThrow(new RuntimeException("Kafka send failed")).when(kafkaTemplate)
                .send("score-topic", mockResponse1);

        // When & Then
        assertThrows(RuntimeException.class, () -> eventJob.execute(jobExecutionContext));

        verify(eventRepository).findAllByStatus(EventStatus.LIVE);
        verify(restTemplate).getForObject("http://localhost:8080/mockScore/event-123", String.class);
        verify(kafkaTemplate).send("score-topic", mockResponse1);

        // Second event should not be processed due to exception in first event
        verify(restTemplate, never()).getForObject("http://localhost:8080/mockScore/event-456", String.class);
    }

    @Test
    void execute_shouldThrowJobExecutionExceptionWhenRepositoryFails() {
        // Given
        when(eventRepository.findAllByStatus(EventStatus.LIVE))
                .thenThrow(new RuntimeException("Database connection failed"));

        // When & Then
        assertThrows(RuntimeException.class, () -> eventJob.execute(jobExecutionContext));

        verify(eventRepository).findAllByStatus(EventStatus.LIVE);
        verify(restTemplate, never()).getForObject(anyString(), eq(String.class));
        verify(kafkaTemplate, never()).send(anyString(), anyString());
    }

    @Test
    void execute_shouldHandleEventWithNullEventId() throws JobExecutionException {
        // Given
        Event eventWithNullId = new Event();
        eventWithNullId.setEventId(null);
        eventWithNullId.setStatus(EventStatus.LIVE);

        List<Event> events = Collections.singletonList(eventWithNullId);

        when(eventRepository.findAllByStatus(EventStatus.LIVE)).thenReturn(events);
        when(restTemplate.getForObject("http://localhost:8080/mockScore/null", String.class))
                .thenReturn("null-response");

        // When
        eventJob.execute(jobExecutionContext);

        // Then
        verify(eventRepository).findAllByStatus(EventStatus.LIVE);
        verify(restTemplate).getForObject("http://localhost:8080/mockScore/null", String.class);
        verify(kafkaTemplate).send("score-topic", "null-response");
    }

    @Test
    void execute_shouldHandleEventWithEmptyEventId() throws JobExecutionException {
        // Given
        Event eventWithEmptyId = new Event();
        eventWithEmptyId.setEventId("");
        eventWithEmptyId.setStatus(EventStatus.LIVE);

        List<Event> events = Collections.singletonList(eventWithEmptyId);

        when(eventRepository.findAllByStatus(EventStatus.LIVE)).thenReturn(events);
        when(restTemplate.getForObject("http://localhost:8080/mockScore/", String.class))
                .thenReturn("empty-id-response");

        // When
        eventJob.execute(jobExecutionContext);

        // Then
        verify(eventRepository).findAllByStatus(EventStatus.LIVE);
        verify(restTemplate).getForObject("http://localhost:8080/mockScore/", String.class);
        verify(kafkaTemplate).send("score-topic", "empty-id-response");
    }

    @Test
    void execute_shouldVerifyCorrectTopicIsUsed() throws JobExecutionException {
        // Given
        List<Event> events = Collections.singletonList(event1);
        String mockResponse = "test-response";

        when(eventRepository.findAllByStatus(EventStatus.LIVE)).thenReturn(events);
        when(restTemplate.getForObject("http://localhost:8080/mockScore/event-123", String.class))
                .thenReturn(mockResponse);

        // When
        eventJob.execute(jobExecutionContext);

        // Then
        verify(kafkaTemplate).send("score-topic", mockResponse);
        verify(kafkaTemplate, never()).send(eq("wrong-topic"), anyString());
    }
}