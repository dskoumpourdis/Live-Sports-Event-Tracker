package com.live.sports.event.tracker.service;

import com.live.sports.event.tracker.transfer.resource.EventResource;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
public interface EventService {
    void createOrUpdateEvent(EventResource eventResource);
}
