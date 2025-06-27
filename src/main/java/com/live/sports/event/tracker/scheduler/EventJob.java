package com.live.sports.event.tracker.scheduler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.live.sports.event.tracker.domain.Event;
import com.live.sports.event.tracker.domain.EventStatus;
import com.live.sports.event.tracker.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EventJob implements Job {

    private final EventRepository eventRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    ObjectMapper objectMapper = new ObjectMapper();
    private final String URL = "http://localhost:8080/mockScore";

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        List<Event> events = eventRepository.findAllByStatus(EventStatus.LIVE);

        for(Event event: events) {
            String response = restTemplate.getForObject(URL +"/"+event.getEventId() , String.class);
            System.out.println("executing job for"+ event.getEventId());
            try {
                System.out.println(response);
                Event parsedEvent = objectMapper.readValue(response, Event.class);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
