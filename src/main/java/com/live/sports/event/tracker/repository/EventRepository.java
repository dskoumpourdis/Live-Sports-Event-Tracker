package com.live.sports.event.tracker.repository;

import com.live.sports.event.tracker.domain.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository class for <code>Event</code> domain objects
 * */
@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByEventId(String eventId);
    List<Event> findAllByStatus();
}
