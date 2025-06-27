package com.live.sports.event.tracker.dataload;

import com.live.sports.event.tracker.base.BaseComponent;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class JpaRunner extends BaseComponent implements CommandLineRunner {
    @Override
    public void run(final String... args) {

    }
}
