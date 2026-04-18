package com.tus.bookingapp.service;

import com.tus.bookingapp.domain.Event;
import com.tus.bookingapp.domain.Ticket;
import com.tus.bookingapp.exception.TicketAlreadySoldException;
import com.tus.bookingapp.exception.TicketNotFoundException;
import com.tus.bookingapp.repository.EventRepository;
import com.tus.bookingapp.repository.TicketRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import com.tus.bookingapp.exception.EventNotFoundException;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private EventRepository eventRepository;

    @Mock
    private TicketRepository ticketRepository;

    @InjectMocks
    private BookingService bookingService;

    private UUID eventUuid;
    private UUID ticketUuid;
    private Event mockEvent;
    private Ticket mockTicket;

    @BeforeEach
    void setUp() {
        eventUuid = UUID.randomUUID();
        ticketUuid = UUID.randomUUID();

        mockEvent = new Event();
        mockEvent.setUuid(eventUuid);

        mockTicket = new Ticket();
        mockTicket.setUuid(ticketUuid);
        mockTicket.setEvent(mockEvent); // Link ticket to event
        mockTicket.setSold(false);
    }

    @Test
    @DisplayName("Should successfully book a ticket (Happy Path)")
    void bookTicket_Success() {
        // Arrange
        when(eventRepository.findByUuid(eventUuid)).thenReturn(Optional.of(mockEvent));
        when(ticketRepository.findByUuid(ticketUuid)).thenReturn(Optional.of(mockTicket));
        when(ticketRepository.save(any(Ticket.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Ticket bookedTicket = bookingService.bookTicket(eventUuid, ticketUuid);

        // Assert
        assertThat(bookedTicket.isSold()).isTrue();
        verify(ticketRepository).save(mockTicket);
    }

    @Test
    @DisplayName("Should throw TicketNotFoundException when ticket doesn't exist")
    void bookTicket_TicketNotFound() {
        // Arrange
        when(eventRepository.findByUuid(eventUuid)).thenReturn(Optional.of(mockEvent));
        when(ticketRepository.findByUuid(ticketUuid)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(TicketNotFoundException.class, () -> {
            bookingService.bookTicket(eventUuid, ticketUuid);
        });
        verify(ticketRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw TicketAlreadySoldException when ticket is already sold")
    void bookTicket_AlreadySold() {
        // Arrange
        mockTicket.setSold(true);
        when(eventRepository.findByUuid(eventUuid)).thenReturn(Optional.of(mockEvent));
        when(ticketRepository.findByUuid(ticketUuid)).thenReturn(Optional.of(mockTicket));

        // Act & Assert
        assertThrows(TicketAlreadySoldException.class, () -> {
            bookingService.bookTicket(eventUuid, ticketUuid);
        });
        verify(ticketRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should throw TicketNotFoundException when ticket belongs to a different event")
    void bookTicket_WrongEvent() {
        // Arrange
        UUID differentEventUuid = UUID.randomUUID();
        Event differentEvent = new Event();
        differentEvent.setUuid(differentEventUuid);

        // Ticket is linked to the "wrong" event
        mockTicket.setEvent(differentEvent);

        when(eventRepository.findByUuid(eventUuid)).thenReturn(Optional.of(mockEvent));
        when(ticketRepository.findByUuid(ticketUuid)).thenReturn(Optional.of(mockTicket));

        // Act & Assert
        assertThrows(TicketNotFoundException.class, () -> {
            bookingService.bookTicket(eventUuid, ticketUuid);
        });
        verify(ticketRepository, never()).save(any());
    }

    @Test
    void bookTicket_EventNotFound_ThrowsEventNotFoundException() {
        UUID unknownEventUuid = UUID.randomUUID();
        when(eventRepository.findByUuid(unknownEventUuid)).thenReturn(Optional.empty());

        assertThrows(EventNotFoundException.class, () ->
                bookingService.bookTicket(unknownEventUuid, ticketUuid));
    }

    //Tests authored by me
    @Test
    void createEvent_saveAndReturnEent() {
        when(eventRepository.save(mockEvent)).thenReturn(mockEvent);

        Event result = bookingService.createEvent(mockEvent);

        assertEquals(eventUuid, result.getUuid());
        verify(eventRepository).save(mockEvent);
    }

    @Test
    void deleteEvent_existingEventCallsDelete() {
        when(eventRepository.findByUuid(eventUuid)).thenReturn(Optional.of(mockEvent));

        bookingService.deleteEvent(eventUuid);

        verify(eventRepository).delete(mockEvent);
    }

    @Test
    void updateEvent_existingEventUpdatesAndReturns() {
        Event updatedEvent = new Event();
        updatedEvent.setUuid(eventUuid);
        updatedEvent.setArtist("Updated Artist");

        when(eventRepository.findByUuid(eventUuid)).thenReturn(Optional.of(mockEvent));
        when(eventRepository.save(any(Event.class))).thenReturn(updatedEvent);

        Event result = bookingService.updateEvent(eventUuid, updatedEvent);

        assertEquals("Updated Artist", result.getArtist());
        verify(eventRepository).save(any(Event.class));
    }

    @Test
    void getEvent_existingEventReturnsEvent() {
        when(eventRepository.findByUuid(eventUuid)).thenReturn(Optional.of(mockEvent));

        Event result = bookingService.getEvent(eventUuid);

        assertEquals(eventUuid, result.getUuid());
        verify(eventRepository).findByUuid(eventUuid);
    }
}