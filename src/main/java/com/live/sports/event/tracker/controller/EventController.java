package com.live.sports.event.tracker.controller;

import com.live.sports.event.tracker.mapper.EventMapper;
import com.live.sports.event.tracker.service.EventService;
import com.live.sports.event.tracker.transfer.resource.EventResource;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

/**
 * Controller class that handles the requests under the /events/status endpoint
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/events")
public class EventController {

    private final EventService eventService;
    private final EventMapper eventMapper;


    @PostMapping("/status")
    public ResponseEntity<String> createOrUpdateEvent(@Valid @RequestBody final EventResource eventResource) {
        eventService.createOrUpdateEvent(eventResource);
        return new ResponseEntity<>("Transaction successful", HttpStatus.OK);
    }

}
