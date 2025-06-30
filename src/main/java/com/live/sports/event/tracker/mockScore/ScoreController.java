package com.live.sports.event.tracker.mockScore;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/mockScore")
@RequiredArgsConstructor
public class ScoreController {

    private final ScoreService scoreService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/{eventId}")
    public String getScore(@Valid @PathVariable("eventId") String eventId) throws JsonProcessingException {
        return objectMapper.writeValueAsString(scoreService.getCurrentScore(eventId));
    }
}
