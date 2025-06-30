package com.live.sports.event.tracker.mockScore;

import lombok.RequiredArgsConstructor;
import lombok.Value;

@Value
@RequiredArgsConstructor
public class ScoreResponse {
    String eventId;
    String currentScore;
}
