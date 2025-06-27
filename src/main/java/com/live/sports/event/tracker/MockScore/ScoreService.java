package com.live.sports.event.tracker.MockScore;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class ScoreService {

    private int home = 0;
    private int away = 0;
    private final Random random = new Random();

    public ScoreResponse getCurrentScore(String eventId) {
        if(random.nextBoolean())
            home++;
        else
            away++;

        return new ScoreResponse(eventId, home+":"+away);
    }
}
