package com.bookify.api_gateway.config;

import com.bookify.api_gateway.filter.AuthenticationFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final AuthenticationFilter filter;

    public GatewayConfig(AuthenticationFilter filter) {
        this.filter = filter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("auth-service", r -> r.path("/api/auth/**")
                        .uri("lb://AUTH-SERVICE"))

                .route("user-service", r -> r.path("/api/users/**", "/ui/users/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://USER-SERVICE"))

                .route("book-service", r -> r.path("/api/books/**", "/ui/books/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://BOOK-SERVICE"))

                .route("rental-service", r -> r.path("/api/rentals/**", "/ui/rentals/**")
                        .filters(f -> f.filter(filter))
                        .uri("lb://RENTAL-SERVICE"))
                .build();
    }
}