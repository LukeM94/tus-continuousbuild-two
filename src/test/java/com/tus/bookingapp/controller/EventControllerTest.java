package com.tus.bookingapp.controller;

import tools.jackson.databind.json.JsonMapper;
import com.tus.bookingapp.dto.DtoMapper;
import com.tus.bookingapp.dto.EventDTO;
import com.tus.bookingapp.exception.EventNotFoundException;
import com.tus.bookingapp.exception.TicketAlreadySoldException;
import com.tus.bookingapp.service.BookingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JsonMapper jsonMapper;

    @MockitoBean
    private BookingService bookingService;

    @MockitoBean
    private DtoMapper dtoMapper;

    @MockitoBean
    private PagedResourcesAssembler<?> pagedResourcesAssembler;

    @Test
    void createEvent_WithBlankArtist_Returns400BadRequest() throws Exception {
        // Arrange: Create a DTO with a blank artist
        EventDTO invalidEvent = new EventDTO();
        invalidEvent.setArtist("");
        invalidEvent.setVenueId(1L);
        invalidEvent.setVenueType("Stadium");
        invalidEvent.setShowTime(java.time.LocalDateTime.now().plusMonths(1));

        // Act & Assert
        mockMvc.perform(post("/api/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonMapper.writeValueAsString(invalidEvent)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.detail").value("Validation failed for one or more fields."))
                .andExpect(jsonPath("$.errors.artist").exists());
    }

    @Test
    void getEvent_WithNonExistentId_Returns404NotFound() throws Exception {
        // Arrange
        UUID randomId = UUID.randomUUID();
        when(bookingService.getEvent(randomId))
                .thenThrow(new EventNotFoundException(randomId));

        mockMvc.perform(get("/api/events/{id}", randomId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.detail").value("Event not found with id: " + randomId));
    }

    @Test
    void bookTicket_AlreadySold_Returns409Conflict() throws Exception {
        // Arrange
        UUID eventId = UUID.randomUUID();
        UUID ticketId = UUID.randomUUID();
        when(bookingService.bookTicket(eventId, ticketId))
                .thenThrow(new TicketAlreadySoldException(ticketId));

        // Act & Assert
        mockMvc.perform(post("/api/events/{eventId}/tickets/{ticketId}/book", eventId, ticketId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.detail").value("Ticket already sold: " + ticketId));
    }
}