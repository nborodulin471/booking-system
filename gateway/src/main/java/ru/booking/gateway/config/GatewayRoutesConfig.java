package ru.booking.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRoutesConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("booking_service_route", r -> r
                        .path("/api/bookings/**")
                        .filters(f -> f.rewritePath("/api/bookings/(?<segment>.*)", "/${segment}"))
                        .uri("lb://RESERVER"))
                .route("hotel_service_route", r -> r
                        .path("/api/hotels/**")
                        .filters(f -> f.rewritePath("/api/hotels", "/hotels"))
                        .uri("lb://MANAGEMENT"))
                .route("hotel_service_route", r -> r
                        .path("/api/rooms", "/api/rooms/recommend")
                        .filters(f -> f.rewritePath("/api/rooms", "/rooms"))
                        .uri("lb://MANAGEMENT"))
                .build();
    }
}
