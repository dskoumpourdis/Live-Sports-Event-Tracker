package com.live.sports.event.tracker.service;

import com.live.sports.event.tracker.domain.Event;
import com.live.sports.event.tracker.mapper.EventMapper;
import com.live.sports.event.tracker.repository.EventRepository;
import com.live.sports.event.tracker.transfer.resource.EventResource;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;


    @Transactional
    @Override
    public void createOrUpdateEvent(EventResource eventResource) {
        Optional<Event> eventOptional = eventRepository.findByEventId(eventResource.getEventId());
        Event event = null;

        if (eventOptional.isPresent()) {
            eventOptional.get().setStatus(eventResource.getStatus());
            event = eventOptional.get();
        } else {
            event = eventMapper.toDomain(eventResource);
        }

        eventRepository.saveAndFlush(event);
    }

}
