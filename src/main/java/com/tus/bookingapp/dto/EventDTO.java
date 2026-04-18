package com.tus.bookingapp.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class EventDTO {

    private UUID id;

    @NotBlank(message = "Artist name cannot be empty")
    private String artist;

    @NotNull(message = "Venue ID is required")
    private Long venueId;

    @NotBlank(message = "Venue type is required")
    private String venueType;

    @NotNull(message = "Show time is required")
    @Future(message = "Show time must be in the future")
    private LocalDateTime showTime;

    @Valid
    private List<TicketDTO> tickets;

    public EventDTO() {}

    public EventDTO(UUID id, String artist, Long venueId, String venueType, LocalDateTime showTime, List<TicketDTO> tickets) {
        this.id = id;
        this.artist = artist;
        this.venueId = venueId;
        this.venueType = venueType;
        this.showTime = showTime;
        this.tickets = tickets;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public Long getVenueId() {
        return venueId;
    }

    public void setVenueId(Long venueId) {
        this.venueId = venueId;
    }

    public String getVenueType() {
        return venueType;
    }

    public void setVenueType(String venueType) {
        this.venueType = venueType;
    }

    public LocalDateTime getShowTime() {
        return showTime;
    }

    public void setShowTime(LocalDateTime showTime) {
        this.showTime = showTime;
    }

    public List<TicketDTO> getTickets() {
        return tickets;
    }

    public void setTickets(List<TicketDTO> tickets) {
        this.tickets = tickets;
    }
}