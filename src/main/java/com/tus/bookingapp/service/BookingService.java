package com.tus.bookingapp.service;

import com.tus.bookingapp.domain.*;
import com.tus.bookingapp.exception.*;
import com.tus.bookingapp.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
public class BookingService {

    private final EventRepository eventRepository;
    private final TicketRepository ticketRepository;
    private final ClubRepository clubRepository;
    private final StadiumRepository stadiumRepository;

    @Autowired
    public BookingService(EventRepository eventRepository,
                          TicketRepository ticketRepository,
                          ClubRepository clubRepository,
                          StadiumRepository stadiumRepository) {
        this.eventRepository = eventRepository;
        this.ticketRepository = ticketRepository;
        this.clubRepository = clubRepository;
        this.stadiumRepository = stadiumRepository;
    }

    @Transactional
    public void seedData() {
        if (eventRepository.count() == 0) {
            Stadium crokePark = stadiumRepository.findByName("Croke Park")
                    .orElseGet(() -> {
                        Stadium newStadium = new Stadium("Croke Park", 80000, false);
                        return stadiumRepository.save(newStadium);
                    });

            UUID eventUuid = UUID.randomUUID();
            Event event = new Event(
                    eventUuid,
                    "Oasis Reunion",
                    crokePark.getId(),
                    "Stadium",
                    LocalDateTime.now().plusMonths(2),
                    new ArrayList<>()
            );
            eventRepository.save(event);

            List<Ticket> tickets = new ArrayList<>();
            for (int i = 1; i <= 20; i++) {
                Ticket t = new Ticket(
                        UUID.randomUUID(),
                        new BigDecimal("50.00"),
                        "A" + i
                );
                t.setEvent(event);
                tickets.add(t);
            }
            ticketRepository.saveAll(tickets);

            event.getTickets().addAll(tickets);
            eventRepository.save(event);
        }
    }

    public Page<Event> getAllEvents(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    public Page<Event> searchEvents(LocalDateTime from, LocalDateTime to, Pageable pageable) {
        return eventRepository.findByShowTimeBetween(from, to, pageable);
    }

    public Event getEvent(UUID uuid) {
        return eventRepository.findByUuid(uuid)
                .orElseThrow(() -> new EventNotFoundException(uuid));
    }

    public List<Ticket> getAllTickets(UUID eventUuid) {
        Event event = getEvent(eventUuid);
        return event.getTickets();
    }

    @Transactional
    public Ticket updateTicket(UUID eventUuid, UUID ticketUuid, Ticket updatedTicketData) {
        Event event = getEvent(eventUuid);
        Ticket ticket = ticketRepository.findByUuid(ticketUuid)
                .orElseThrow(() -> new TicketNotFoundException(ticketUuid));

        if (!ticket.getEvent().getUuid().equals(eventUuid)) {
            throw new TicketNotFoundException(ticketUuid);
        }

        ticket.setPrice(updatedTicketData.getPrice());
        ticket.setSeatNumber(updatedTicketData.getSeatNumber());
        ticket.setSold(updatedTicketData.isSold());
        
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Event updateEvent(UUID uuid, Event updatedEventData) {
        Event event = getEvent(uuid);
        event.setArtist(updatedEventData.getArtist());
        event.setVenueId(updatedEventData.getVenueId());
        event.setVenueType(updatedEventData.getVenueType());
        event.setShowTime(updatedEventData.getShowTime());
        return eventRepository.save(event);
    }

    @Transactional
    public Ticket bookTicket(UUID eventUuid, UUID ticketUuid) {
        Event event = getEvent(eventUuid);

        Ticket ticket = ticketRepository.findByUuid(ticketUuid)
                .orElseThrow(() -> new TicketNotFoundException(ticketUuid));

        if (!ticket.getEvent().getUuid().equals(eventUuid)) {
            throw new TicketNotFoundException(ticketUuid);
        }

        if (ticket.isSold()) {
            throw new TicketAlreadySoldException(ticketUuid);
        }

        ticket.setSold(true);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public Ticket addTicket(UUID eventUuid, Ticket ticket) {
        Event event = getEvent(eventUuid);
        event.addTicket(ticket);
        return ticketRepository.save(ticket);
    }

    @Transactional
    public void deleteEvent(UUID uuid) {
        Event event = getEvent(uuid);
        eventRepository.delete(event);
    }

    @Transactional
    public void deleteTicket(UUID eventUuid, UUID ticketUuid) {
        Event event = getEvent(eventUuid);
        
        Ticket ticket = ticketRepository.findByUuid(ticketUuid)
                .orElseThrow(() -> new TicketNotFoundException(ticketUuid));
                
        if (!ticket.getEvent().getUuid().equals(eventUuid)) {
            throw new TicketNotFoundException(ticketUuid);
        }
        
        event.removeTicket(ticket);
        ticketRepository.delete(ticket);
    }

    @Transactional
    public Event createEvent(Event event) {
        return eventRepository.save(event);
    }
}