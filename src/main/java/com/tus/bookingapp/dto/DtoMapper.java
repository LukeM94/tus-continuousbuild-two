package com.tus.bookingapp.dto;

import com.tus.bookingapp.domain.Event;
import com.tus.bookingapp.domain.Ticket;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class DtoMapper {

    public EventDTO toEventDTO(Event event) {
        if (event == null) return null;

        List<TicketDTO> ticketDTOs = event.getTickets().stream()
                .map(this::toTicketDTO)
                .collect(Collectors.toList());

        return new EventDTO(
                event.getUuid(),
                event.getArtist(),
                event.getVenueId(),
                event.getVenueType(),
                event.getShowTime(),
                ticketDTOs
        );
    }

    public TicketDTO toTicketDTO(Ticket ticket) {
        if (ticket == null) return null;

        return new TicketDTO(
                ticket.getUuid(),
                ticket.getPrice(),
                ticket.getSeatNumber(),
                ticket.isSold()
        );
    }

    public Ticket toTicketEntity(TicketDTO dto) {
        if (dto == null) return null;
        
        Ticket ticket = new Ticket(
                UUID.randomUUID(),
                dto.getPrice(),
                dto.getSeatNumber()
        );
        ticket.setSold(dto.getIsSold() != null ? dto.getIsSold() : false);
        return ticket;
    }

    public Event toEventEntity(EventDTO dto) {
        if (dto == null) return null;

        Event event = new Event(
                dto.getId() != null ? dto.getId() : UUID.randomUUID(),
                dto.getArtist(),
                dto.getVenueId(),
                dto.getVenueType(),
                dto.getShowTime(),
                new ArrayList<>()
        );

        if (dto.getTickets() != null) {
            List<Ticket> tickets = dto.getTickets().stream()
                    .map(tDto -> {
                        Ticket t = new Ticket(
                                UUID.randomUUID(),
                                tDto.getPrice(),
                                tDto.getSeatNumber()
                        );
                        t.setEvent(event);
                        t.setSold(tDto.getIsSold() != null ? tDto.getIsSold() : false);
                        return t;
                    })
                    .collect(Collectors.toList());
            event.setTickets(tickets);
        }

        return event;
    }
}