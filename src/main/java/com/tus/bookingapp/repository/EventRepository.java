package com.tus.bookingapp.repository;

import com.tus.bookingapp.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    Optional<Event> findByUuid(UUID uuid);

    Page<Event> findByShowTimeBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);
}
