package com.tus.bookingapp.domain;

import jakarta.persistence.*;

@Entity
public class Stadium {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
    private int capacity;
    private boolean hasRoof;

    public Stadium() {
    }

    public Stadium(String name, int capacity, boolean hasRoof) {
        this.name = name;
        this.capacity = capacity;
        this.hasRoof = hasRoof;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isHasRoof() {
        return hasRoof;
    }

    public void setHasRoof(boolean hasRoof) {
        this.hasRoof = hasRoof;
    }
}