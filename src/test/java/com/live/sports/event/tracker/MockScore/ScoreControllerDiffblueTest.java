package com.live.sports.event.tracker.MockScore;

import static org.mockito.Mockito.when;

import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestExceptionHandler;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {ScoreController.class, RepositoryRestExceptionHandler.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class ScoreControllerDiffblueTest {
    @Autowired
    private RepositoryRestExceptionHandler repositoryRestExceptionHandler;

    @Autowired
    private ScoreController scoreController;

    @MockitoBean
    private ScoreService scoreService;

    /**
     * Test {@link ScoreController#getScore(String)}.
     * <ul>
     *   <li>Given {@link ScoreService} {@link ScoreService#getCurrentScore(String)} return {@code null}.</li>
     *   <li>Then content string {@code null}.</li>
     * </ul>
     * <p>
     * Method under test: {@link ScoreController#getScore(String)}
     */
    @Test
    @DisplayName("Test getScore(String); given ScoreService getCurrentScore(String) return 'null'; then content string 'null'")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"String ScoreController.getScore(String)"})
    void testGetScore_givenScoreServiceGetCurrentScoreReturnNull_thenContentStringNull() throws Exception {
        // Arrange
        when(scoreService.getCurrentScore(Mockito.<String>any())).thenReturn(null);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/mockScore/{eventId}", "42");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(scoreController)
                .setControllerAdvice(repositoryRestExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("null"));
    }

    /**
     * Test {@link ScoreController#getScore(String)}.
     * <ul>
     *   <li>Then content string {@code {"eventId":"42","currentScore":"Current Score"}}.</li>
     * </ul>
     * <p>
     * Method under test: {@link ScoreController#getScore(String)}
     */
    @Test
    @DisplayName("Test getScore(String); then content string '{\"eventId\":\"42\",\"currentScore\":\"Current Score\"}'")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"String ScoreController.getScore(String)"})
    void testGetScore_thenContentStringEventId42CurrentScoreCurrentScore() throws Exception {
        // Arrange
        when(scoreService.getCurrentScore(Mockito.<String>any())).thenReturn(new ScoreResponse("42", "Current Score"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/mockScore/{eventId}", "42");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(scoreController)
                .setControllerAdvice(repositoryRestExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("{\"eventId\":\"42\",\"currentScore\":\"Current Score\"}"));
    }

    /**
     * Test {@link ScoreController#getScore(String)}.
     * <ul>
     *   <li>Then content string {@code {"eventId":"Event Id","currentScore":"Current Score"}}.</li>
     * </ul>
     * <p>
     * Method under test: {@link ScoreController#getScore(String)}
     */
    @Test
    @DisplayName("Test getScore(String); then content string '{\"eventId\":\"Event Id\",\"currentScore\":\"Current Score\"}'")
    @Tag("MaintainedByDiffblue")
    @MethodsUnderTest({"String ScoreController.getScore(String)"})
    void testGetScore_thenContentStringEventIdEventIdCurrentScoreCurrentScore() throws Exception {
        // Arrange
        when(scoreService.getCurrentScore(Mockito.<String>any()))
                .thenReturn(new ScoreResponse("Event Id", "Current Score"));
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/mockScore/{eventId}", "42");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(scoreController)
                .setControllerAdvice(repositoryRestExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(
                        MockMvcResultMatchers.content().string("{\"eventId\":\"Event Id\",\"currentScore\":\"Current Score\"}"));
    }
}
