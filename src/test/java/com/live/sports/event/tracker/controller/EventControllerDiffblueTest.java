package com.live.sports.event.tracker.controller;

import static org.mockito.Mockito.doNothing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.live.sports.event.tracker.domain.EventStatus;
import com.live.sports.event.tracker.mapper.EventMapper;
import com.live.sports.event.tracker.service.EventService;
import com.live.sports.event.tracker.transfer.resource.EventResource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.RepositoryRestExceptionHandler;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {EventController.class, RepositoryRestExceptionHandler.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class EventControllerDiffblueTest {
    @Autowired
    private EventController eventController;

    @MockitoBean
    private EventMapper eventMapper;

    @MockitoBean
    private EventService eventService;

    @Autowired
    private RepositoryRestExceptionHandler repositoryRestExceptionHandler;

    /**
     * Test {@link EventController#createOrUpdateEvent(EventResource)}.
     * <p>
     * Method under test: {@link EventController#createOrUpdateEvent(EventResource)}
     */
    @Test
    @DisplayName("Test createOrUpdateEvent(EventResource)")
    @Tag("MaintainedByDiffblue")
    void testCreateOrUpdateEvent() throws Exception {
        // Arrange
        doNothing().when(eventService).createOrUpdateEvent(Mockito.<EventResource>any());

        EventResource eventResource = new EventResource();
        eventResource.setCurrentScore("Current Score");
        eventResource.setEventId("42");
        eventResource.setId(1L);
        eventResource.setStatus(EventStatus.LIVE);
        String content = new ObjectMapper().writeValueAsString(eventResource);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post("/events/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(eventController)
                .setControllerAdvice(repositoryRestExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("text/plain;charset=ISO-8859-1"))
                .andExpect(MockMvcResultMatchers.content().string("Transaction successful"));
    }
}
