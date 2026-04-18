package com.tus.bookingapp.exception;

import java.util.UUID;

public class TicketAlreadySoldException extends RuntimeException {
    public TicketAlreadySoldException(UUID id) {
        super("Ticket already sold: " + id);
    }
}