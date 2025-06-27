package com.live.sports.event.tracker.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 *  Simple domain class with an id property. Used as a base class for objects
 *  that need this property.
 */
@MappedSuperclass
@Setter
@Getter
@ToString
public abstract class BaseEntity implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;
}
