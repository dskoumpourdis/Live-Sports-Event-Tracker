package com.live.sports.event.tracker.mockScore;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {ScoreService.class})
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
@ExtendWith(SpringExtension.class)
class ScoreServiceDiffblueTest {
    @Autowired
    private ScoreService scoreService;

    /**
     * Test {@link ScoreService#getCurrentScore(String)}.
     * <ul>
     *   <li>Given {@link ScoreService}.</li>
     *   <li>When {@code 42Event Id}.</li>
     *   <li>Then return EventId is {@code 42Event Id}.</li>
     * </ul>
     * <p>
     * Method under test: {@link ScoreService#getCurrentScore(String)}
     */
    @Test
    @DisplayName("Test getCurrentScore(String); given ScoreService; when '42Event Id'; then return EventId is '42Event Id'")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"com.live.sports.event.tracker.MockScore.ScoreResponse ScoreService.getCurrentScore(String)"})
    void testGetCurrentScore_givenScoreService_when42EventId_thenReturnEventIdIs42EventId() {
        // Arrange, Act and Assert
        assertEquals("42Event Id", scoreService.getCurrentScore("42Event Id").getEventId());
    }

    /**
     * Test {@link ScoreService#getCurrentScore(String)}.
     * <ul>
     *   <li>Given {@link ScoreService}.</li>
     *   <li>When {@code 42}.</li>
     *   <li>Then return EventId is {@code 42}.</li>
     * </ul>
     * <p>
     * Method under test: {@link ScoreService#getCurrentScore(String)}
     */
    @Test
    @DisplayName("Test getCurrentScore(String); given ScoreService; when '42'; then return EventId is '42'")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"com.live.sports.event.tracker.MockScore.ScoreResponse ScoreService.getCurrentScore(String)"})
    void testGetCurrentScore_givenScoreService_when42_thenReturnEventIdIs42() {
        // Arrange, Act and Assert
        assertEquals("42", scoreService.getCurrentScore("42").getEventId());
    }

    /**
     * Test {@link ScoreService#getCurrentScore(String)}.
     * <ul>
     *   <li>Given {@link ScoreService} (default constructor).</li>
     *   <li>When {@code 42}.</li>
     *   <li>Then return EventId is {@code 42}.</li>
     * </ul>
     * <p>
     * Method under test: {@link ScoreService#getCurrentScore(String)}
     */
    @Test
    @DisplayName("Test getCurrentScore(String); given ScoreService (default constructor); when '42'; then return EventId is '42'")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"com.live.sports.event.tracker.MockScore.ScoreResponse ScoreService.getCurrentScore(String)"})
    void testGetCurrentScore_givenScoreService_when42_thenReturnEventIdIs422() {
        // Arrange, Act and Assert
        assertEquals("42", new ScoreService().getCurrentScore("42").getEventId());
    }
}
