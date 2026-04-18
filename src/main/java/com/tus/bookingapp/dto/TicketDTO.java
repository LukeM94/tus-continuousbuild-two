package com.tus.bookingapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public class TicketDTO {

    private UUID id;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be positive")
    private BigDecimal price;

    @NotBlank(message = "Seat number is required")
    private String seatNumber;

    private Boolean isSold;

    public TicketDTO() {}

    public TicketDTO(UUID id, BigDecimal price, String seatNumber, Boolean isSold) {
        this.id = id;
        this.price = price;
        this.seatNumber = seatNumber;
        this.isSold = isSold;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public String getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
    }

    public Boolean getIsSold() {
        return isSold;
    }

    public void setIsSold(Boolean sold) {
        isSold = sold;
    }
}