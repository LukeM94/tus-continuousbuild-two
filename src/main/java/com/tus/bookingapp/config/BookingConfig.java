package com.tus.bookingapp.config;

import com.tus.bookingapp.service.BookingService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookingConfig {

    @Bean
    public CommandLineRunner run(BookingService bookingService) {
        return args -> {
            bookingService.seedData();
        };
    }
}