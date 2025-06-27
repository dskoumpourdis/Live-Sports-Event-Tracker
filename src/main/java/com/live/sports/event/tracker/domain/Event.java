package com.live.sports.event.tracker.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import javax.validation.constraints.NotNull;


/**
 * Domain class that represents an account.
 */
@Entity
@Setter
@Getter
@Builder
@ToString(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class Event extends BaseEntity {
    @NotNull
    private String eventId;

    @Enumerated(EnumType.STRING)
    private EventStatus status;

    private String currentScore;
}
