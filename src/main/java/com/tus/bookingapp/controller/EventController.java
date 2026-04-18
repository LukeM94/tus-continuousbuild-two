package com.tus.bookingapp.controller;

import com.tus.bookingapp.domain.*;
import com.tus.bookingapp.dto.*;
import com.tus.bookingapp.service.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.*;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final BookingService service;
    private final DtoMapper mapper;
    private final PagedResourcesAssembler<Event> pagedResourcesAssembler;

    public EventController(BookingService service, DtoMapper mapper, PagedResourcesAssembler<Event> pagedResourcesAssembler) {
        this.service = service;
        this.mapper = mapper;
        this.pagedResourcesAssembler = pagedResourcesAssembler;
    }

    @GetMapping("/search")
    public PagedModel<EntityModel<EventDTO>> search(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to,
            Pageable pageable
    ) {
        Page<Event> events = service.searchEvents(from, to, pageable);

        return pagedResourcesAssembler.toModel(events, e -> EntityModel.of(
                mapper.toEventDTO(e),
                linkTo(methodOn(EventController.class).one(e.getUuid())).withSelfRel(),
                linkTo(methodOn(EventController.class).tickets(e.getUuid())).withRel("tickets")
        ));
    }

    @GetMapping
    public PagedModel<EntityModel<EventDTO>> all(Pageable pageable) {
        Page<Event> events = service.getAllEvents(pageable);

        return pagedResourcesAssembler.toModel(events, e -> EntityModel.of(
                mapper.toEventDTO(e),
                linkTo(methodOn(EventController.class).one(e.getUuid())).withSelfRel(),
                linkTo(methodOn(EventController.class).tickets(e.getUuid())).withRel("tickets")
        ));
    }

    @GetMapping("/{id}")
    public EntityModel<EventDTO> one(@PathVariable UUID id) {
        Event e = service.getEvent(id);
        return EntityModel.of(
                mapper.toEventDTO(e),
                linkTo(methodOn(EventController.class)
                        .one(id)).withSelfRel(),
                linkTo(methodOn(EventController.class)
                        .tickets(id)).withRel("tickets")
        );
    }

    @GetMapping("/{id}/tickets")
    public CollectionModel<EntityModel<TicketDTO>> tickets(@PathVariable UUID id) {
        return CollectionModel.of(
                service.getAllTickets(id).stream()
                        .map(t -> {
                            EntityModel<TicketDTO> model = EntityModel.of(mapper.toTicketDTO(t));
                            model.add(linkTo(methodOn(EventController.class).one(id)).withRel("event"));
                            if (!t.isSold()) {
                                model.add(linkTo(methodOn(EventController.class).book(id, t.getUuid())).withRel("book"));
                            }
                            return model;
                        })
                        .collect(Collectors.toList())
        );
    }

    @PutMapping("/{id}")
    public org.springframework.http.ResponseEntity<EntityModel<EventDTO>> updateEvent(
            @PathVariable UUID id,
            @Valid @RequestBody EventDTO eventDTO
    ) {
        Event updatedEvent = service.updateEvent(id, mapper.toEventEntity(eventDTO));
        return org.springframework.http.ResponseEntity.ok(
                EntityModel.of(
                        mapper.toEventDTO(updatedEvent),
                        linkTo(methodOn(EventController.class).one(id)).withSelfRel()
                )
        );
    }

    @PutMapping("/{eventId}/tickets/{ticketId}")
    public org.springframework.http.ResponseEntity<EntityModel<TicketDTO>> updateTicket(
            @PathVariable UUID eventId,
            @PathVariable UUID ticketId,
            @Valid @RequestBody TicketDTO ticketDTO
    ) {
        Ticket updatedTicket = service.updateTicket(eventId, ticketId, mapper.toTicketEntity(ticketDTO));
        return org.springframework.http.ResponseEntity.ok(
                EntityModel.of(
                        mapper.toTicketDTO(updatedTicket),
                        linkTo(methodOn(EventController.class).one(eventId)).withRel("event")
                )
        );
    }

    @PostMapping("/{eventId}/tickets")
    public org.springframework.http.ResponseEntity<EntityModel<TicketDTO>> createTicket(
            @PathVariable UUID eventId,
            @Valid @RequestBody TicketDTO ticketDTO
    ) {
        Ticket ticket = mapper.toTicketEntity(ticketDTO);
        Ticket createdTicket = service.addTicket(eventId, ticket);

        return org.springframework.http.ResponseEntity.ok(
                EntityModel.of(
                        mapper.toTicketDTO(createdTicket),
                        linkTo(methodOn(EventController.class).one(eventId)).withRel("event")
                )
        );
    }

    @PostMapping("/{eventId}/tickets/{ticketId}/book")
    public EntityModel<TicketDTO> book(
            @PathVariable UUID eventId,
            @PathVariable UUID ticketId
    ) {
        Ticket t = service.bookTicket(eventId, ticketId);
        return EntityModel.of(
                mapper.toTicketDTO(t),
                linkTo(methodOn(EventController.class)
                        .one(eventId)).withRel("event")
        );
    }

    @DeleteMapping("/{id}")
    public org.springframework.http.ResponseEntity<?> deleteEvent(@PathVariable UUID id) {
        service.deleteEvent(id);
        return org.springframework.http.ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{eventId}/tickets/{ticketId}")
    public org.springframework.http.ResponseEntity<?> deleteTicket(
            @PathVariable UUID eventId,
            @PathVariable UUID ticketId
    ) {
        service.deleteTicket(eventId, ticketId);
        return org.springframework.http.ResponseEntity.noContent().build();
    }

    @PostMapping
    public org.springframework.http.ResponseEntity<EntityModel<EventDTO>> createEvent(@Valid @RequestBody EventDTO eventDTO) {
        Event event = mapper.toEventEntity(eventDTO);
        Event createdEvent = service.createEvent(event);
        
        EntityModel<EventDTO> model = EntityModel.of(
                mapper.toEventDTO(createdEvent),
                linkTo(methodOn(EventController.class).one(createdEvent.getUuid())).withSelfRel()
        );

        return org.springframework.http.ResponseEntity
                .created(model.getRequiredLink(IanaLinkRelations.SELF).toUri())
                .body(model);
    }
}