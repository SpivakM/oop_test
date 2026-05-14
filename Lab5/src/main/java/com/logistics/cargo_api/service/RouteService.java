package com.logistics.cargo_api.service;

import com.logistics.cargo_api.entity.Route;
import com.logistics.cargo_api.exception.EntityNotFoundException;
import com.logistics.cargo_api.repository.RouteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class RouteService {

    private final RouteRepository routeRepository;

    @Transactional
    public Route addRoute(String origin, String destination, double distanceKm) {
        if (origin == null || origin.isBlank() || destination == null || destination.isBlank()) {
            throw new IllegalArgumentException("Пункт відправлення та призначення не можуть бути порожніми");
        }
        if (distanceKm <= 0) {
            throw new IllegalArgumentException("Відстань повинна бути більше 0 км");
        }

        Route route = Route.builder()
                .origin(origin)
                .destination(destination)
                .distanceKm(distanceKm)
                .build();

        return routeRepository.save(route);
    }

    @Transactional(readOnly = true)
    public Route findById(Long id) {
        return routeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Маршрут", id));
    }

    @Transactional(readOnly = true)
    public List<Route> findAll() {
        return routeRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Route> findByOrigin(String origin) {
        return routeRepository.findByOriginIgnoreCase(origin);
    }
}