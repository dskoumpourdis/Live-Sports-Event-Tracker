package com.live.sports.event.tracker.transfer.resource;

import com.live.sports.event.tracker.domain.EventStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Simple DTO for Event objects
 */
@Getter
@Setter
@ToString(callSuper = true)
public class EventResource extends BaseResource {
    private String eventId;
    private EventStatus Status;
    private String currentScore;
}
