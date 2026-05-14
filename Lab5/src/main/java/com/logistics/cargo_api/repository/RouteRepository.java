package com.logistics.cargo_api.repository;

import com.logistics.cargo_api.entity.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    List<Route> findByOriginIgnoreCase(String origin);
    List<Route> findByDestinationIgnoreCase(String destination);
}