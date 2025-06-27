package com.live.sports.event.tracker.kafka;

import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {KafkaConsumer.class})
@ExtendWith(SpringExtension.class)
class KafkaConsumerDiffblueTest {
    @Autowired
    private KafkaConsumer kafkaConsumer;

    /**
     * Test {@link KafkaConsumer#listen(String)}.
     * <p>
     * Method under test: {@link KafkaConsumer#listen(String)}
     */
    @Test
    @DisplayName("Test listen(String)")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"void KafkaConsumer.listen(String)"})
    void testListen() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Diffblue AI was unable to find a test

        // Arrange and Act
        kafkaConsumer.listen("Not all who wander are lost");
    }
}
