package com.live.sports.event.tracker.mapper;


import com.live.sports.event.tracker.base.BaseMapper;
import com.live.sports.event.tracker.domain.Event;
import com.live.sports.event.tracker.transfer.resource.EventResource;
import org.mapstruct.Mapper;

/**
 * Mapper class that allows MapStruct to create an implementation of it.
 */
@Mapper(componentModel = "spring")
public interface EventMapper extends BaseMapper<Event, EventResource> {
}
