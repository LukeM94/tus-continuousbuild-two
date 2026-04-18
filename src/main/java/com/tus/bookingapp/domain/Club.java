package com.tus.bookingapp.domain;

import jakarta.persistence.*;

@Entity
public class Club {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;
    private int capacity;
    private boolean hasBar;

    public Club() {
    }

    public Club(String name, int capacity, boolean hasBar) {
        this.name = name;
        this.capacity = capacity;
        this.hasBar = hasBar;
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

    public boolean isHasBar() {
        return hasBar;
    }

    public void setHasBar(boolean hasBar) {
        this.hasBar = hasBar;
    }
}