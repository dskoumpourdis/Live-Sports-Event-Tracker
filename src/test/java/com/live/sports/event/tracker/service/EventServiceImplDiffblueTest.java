package com.live.sports.event.tracker.service;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.diffblue.cover.annotations.MethodsUnderTest;
import com.live.sports.event.tracker.base.BaseMapper;
import com.live.sports.event.tracker.domain.Event;
import com.live.sports.event.tracker.domain.EventStatus;
import com.live.sports.event.tracker.mapper.EventMapper;
import com.live.sports.event.tracker.repository.EventRepository;
import com.live.sports.event.tracker.transfer.resource.EventResource;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {EventServiceImpl.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class EventServiceImplDiffblueTest {
    @MockitoBean
    private EventMapper eventMapper;

    @MockitoBean
    private EventRepository eventRepository;

    @Autowired
    private EventServiceImpl eventServiceImpl;

    /**
     * Test {@link EventServiceImpl#createOrUpdateEvent(EventResource)}.
     * <ul>
     *   <li>Given {@link EventRepository} {@link EventRepository#findByEventId(String)} return of {@link Event#Event()}.</li>
     * </ul>
     * <p>
     * Method under test: {@link EventServiceImpl#createOrUpdateEvent(EventResource)}
     */
    @Test
    @DisplayName("Test createOrUpdateEvent(EventResource); given EventRepository findByEventId(String) return of Event()")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"void EventServiceImpl.createOrUpdateEvent(EventResource)"})
    void testCreateOrUpdateEvent_givenEventRepositoryFindByEventIdReturnOfEvent() {
        // Arrange
        Event event = new Event();
        event.setCurrentScore("Current Score");
        event.setEventId("42");
        event.setId(1L);
        event.setStatus(EventStatus.LIVE);

        Event event2 = new Event();
        event2.setCurrentScore("Current Score");
        event2.setEventId("42");
        event2.setId(1L);
        event2.setStatus(EventStatus.LIVE);
        Optional<Event> ofResult = Optional.of(event2);
        when(eventRepository.saveAndFlush(Mockito.<Event>any())).thenReturn(event);
        when(eventRepository.findByEventId(Mockito.<String>any())).thenReturn(ofResult);

        EventResource eventResource = new EventResource();
        eventResource.setCurrentScore("Current Score");
        eventResource.setEventId("42");
        eventResource.setId(1L);
        eventResource.setStatus(EventStatus.LIVE);

        // Act
        eventServiceImpl.createOrUpdateEvent(eventResource);

        // Assert
        verify(eventRepository).findByEventId(eq("42"));
        verify(eventRepository).saveAndFlush(isA(Event.class));
    }

    /**
     * Test {@link EventServiceImpl#createOrUpdateEvent(EventResource)}.
     * <ul>
     *   <li>Then calls {@link BaseMapper#toDomain(Object)}.</li>
     * </ul>
     * <p>
     * Method under test: {@link EventServiceImpl#createOrUpdateEvent(EventResource)}
     */
    @Test
    @DisplayName("Test createOrUpdateEvent(EventResource); then calls toDomain(Object)")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"void EventServiceImpl.createOrUpdateEvent(EventResource)"})
    void testCreateOrUpdateEvent_thenCallsToDomain() {
        // Arrange
        Event event = new Event();
        event.setCurrentScore("Current Score");
        event.setEventId("42");
        event.setId(1L);
        event.setStatus(EventStatus.LIVE);
        when(eventRepository.saveAndFlush(Mockito.<Event>any())).thenReturn(event);
        Optional<Event> emptyResult = Optional.empty();
        when(eventRepository.findByEventId(Mockito.<String>any())).thenReturn(emptyResult);

        Event event2 = new Event();
        event2.setCurrentScore("Current Score");
        event2.setEventId("42");
        event2.setId(1L);
        event2.setStatus(EventStatus.LIVE);
        when(eventMapper.toDomain(Mockito.<EventResource>any())).thenReturn(event2);

        EventResource eventResource = new EventResource();
        eventResource.setCurrentScore("Current Score");
        eventResource.setEventId("42");
        eventResource.setId(1L);
        eventResource.setStatus(EventStatus.LIVE);

        // Act
        eventServiceImpl.createOrUpdateEvent(eventResource);

        // Assert
        verify(eventMapper).toDomain(isA(EventResource.class));
        verify(eventRepository).findByEventId(eq("42"));
        verify(eventRepository).saveAndFlush(isA(Event.class));
    }
}
