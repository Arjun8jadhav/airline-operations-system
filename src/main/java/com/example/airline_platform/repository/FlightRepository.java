package com.example.airline_platform.repository;

import com.example.airline_platform.Entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface FlightRepository extends JpaRepository<Flight, Long> {

    // Find single flight by flight number
    Optional<Flight> findByFlightNumber(String flightNumber);

    // Find multiple flights by route
    List<Flight> findByOriginAndDestination(String origin, String destination);

    // Find flights by origin
    List<Flight> findByOrigin(String origin);

    // Find flights by destination
    List<Flight> findByDestination(String destination);

    // Find flights by route with date range
    List<Flight> findByOriginAndDestinationAndDepartureTimeBetween(
            String origin,
            String destination,
            LocalDateTime start,
            LocalDateTime end
    );

    // Check if flight number exists
    boolean existsByFlightNumber(String flightNumber);

    // Flexible search with optional parameters
    @Query("SELECT f FROM Flight f WHERE " +
            "(:origin IS NULL OR f.origin = :origin) AND " +
            "(:destination IS NULL OR f.destination = :destination) AND " +
            "(:departureTime IS NULL OR DATE(f.departureTime) = DATE(:departureTime))")
    List<Flight> searchFlights(@Param("origin") String origin,
                               @Param("destination") String destination,
                               @Param("departureTime") LocalDateTime departureTime);
}