package com.live.sports.event.tracker.MockScore;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class ScoreResponse {
    private String eventId;
    private String currentScore;
}
