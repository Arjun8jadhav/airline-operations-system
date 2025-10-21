package com.example.airline_platform.controller;
import com.example.airline_platform.Entity.Flight;
import com.example.airline_platform.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/flights")
public class FlightController {

    @Autowired
    private FlightRepository flightRepository;

    @PostMapping("/create")
    public ResponseEntity<Map<String,Object>> createFlight(@RequestBody Flight flight){
        Map<String,Object> response= new HashMap<>();
        if(flightRepository.existsByFlightNumber(flight.getFlightNumber())){
            response.put("status","error");
            response.put("message","Flight number already exists!");
            return ResponseEntity.badRequest().body(response);
        }
        if(flight.getOrigin().equals(flight.getDestination())){
            response.put("status","error");
            response.put("message","Origin and destination cannot be the same!");
            return ResponseEntity.badRequest().body(response);
        }
        if(flight.getArrivalTime()== null || flight.getDepartureTime()== null){
            response.put("status","error");
            response.put("message","Departure and arrival times are required!");
            return ResponseEntity.badRequest().body(response);
        }
        if(flight.getDepartureTime().isAfter(flight.getArrivalTime()) ||
           flight.getDepartureTime().isEqual(flight.getArrivalTime())){
            response.put("status","error");
            response.put("message","Departure time must be before arrival time!");
            return ResponseEntity.badRequest().body(response);
        }
        if(flight.getSeats() <= 0){
            response.put("status","error");
            response.put("message","Number of seats must be greater than zero!");
            return ResponseEntity.badRequest().body(response);
        }
        if (flight.getPrice()== null || flight.getPrice() < 0) {
            response.put("status", "error");
            response.put("message", "Price is required and must be non-negative value!");
            return ResponseEntity.badRequest().body(response);
        }
        Flight savedFlight= flightRepository.save(flight);
        response.put("status","success");
        response.put("message","Flight created successfully!");
        response.put("flightID", savedFlight.getId());
        return ResponseEntity.ok(response);

    }

    @GetMapping
    public ResponseEntity<Map<String,Object>> getAllFlights(){
        try{
            List<Flight> flights= flightRepository.findAll();
            Map<String,Object> response= new HashMap<>();
            response.put("status","success");
            response.put("flights",flights);
            return ResponseEntity.ok(response);
        }
        catch (Exception e){
            Map<String,Object> response= new HashMap<>();
            response.put("status","error");
            response.put("message","An error occurred while retrieving flights.");
            return ResponseEntity.status(500).body(response);
        }


    }


    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchFlights(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime departureDate) {

        List<Flight> flights;

        if (departureDate != null) {
            // Search with specific date
            LocalDateTime startOfDay = departureDate.toLocalDate().atStartOfDay();
            LocalDateTime endOfDay = departureDate.toLocalDate().atTime(23, 59, 59);

            flights = flightRepository.findByOriginAndDestinationAndDepartureTimeBetween(
                    origin, destination, startOfDay, endOfDay);
        } else {
            // Search without specific date
            flights = flightRepository.findByOriginAndDestination(origin, destination);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("flights", flights);
        response.put("count", flights.size());
        response.put("search", Map.of(
                "origin", origin,
                "destination", destination,
                "departureDate", departureDate != null ? departureDate.toString() : "any"
        ));

        return ResponseEntity.ok(response);
    }

    // Get flight by ID
    @GetMapping("/{flightId}")
    public ResponseEntity<Map<String, Object>> getFlightById(@PathVariable Long flightId) {
        Optional<Flight> flightOpt = flightRepository.findById(flightId);

        if (flightOpt.isEmpty()) {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Flight not found!");
            return ResponseEntity.status(404).body(response);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("flight", flightOpt.get());

        return ResponseEntity.ok(response);
    }


}
