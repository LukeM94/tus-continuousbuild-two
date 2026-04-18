package com.tus.bookingapp.dto;

import com.tus.bookingapp.domain.Event;
import com.tus.bookingapp.domain.Ticket;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DtoMapperTest {

    private final DtoMapper mapper = new DtoMapper();

    @Test
    @DisplayName("Should generate new UUID and set ticket back-references when DTO ID is null")
    void toEventEntity_WithNullId_GeneratesUuidAndSetsBackReferences() {
        TicketDTO ticketDto1 = new TicketDTO(null, new BigDecimal("50.00"), "A1", false);
        TicketDTO ticketDto2 = new TicketDTO(null, new BigDecimal("75.00"), "B2", true);

        EventDTO eventDto = new EventDTO(
                null,
                "The Killers",
                1L,
                "Stadium",
                LocalDateTime.now().plusMonths(1),
                List.of(ticketDto1, ticketDto2)
        );

        Event result = mapper.toEventEntity(eventDto);

        assertNotNull(result);
        assertNotNull(result.getUuid());
        assertNotNull(result.getTickets());
        assertEquals(2, result.getTickets().size());

        for (Ticket ticket : result.getTickets()) {
            assertNotNull(ticket.getUuid());
            assertEquals(result, ticket.getEvent());
        }
    }
}