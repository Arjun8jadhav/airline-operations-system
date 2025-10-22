package com.example.airline_platform.controller;

import com.example.airline_platform.Entity.*;
import com.example.airline_platform.repository.BookingRepository;
import com.example.airline_platform.repository.UserRepository;
import com.example.airline_platform.repository.FlightRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FlightRepository flightRepository;

    @PostMapping("/create")
    public ResponseEntity<Map<String, Object>> createBooking(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();

        try {

            Long userId = Long.valueOf(request.get("userId").toString());
            Long flightId = Long.valueOf(request.get("flightId").toString());
            Integer seatNo = Integer.valueOf(request.get("seatNo").toString());


            Optional<User> userOpt = userRepository.findById(userId);
            Optional<Flight> flightOpt = flightRepository.findById(flightId);

            if (userOpt.isEmpty()) {
                response.put("status", "error");
                response.put("message", "User not found!");
                return ResponseEntity.badRequest().body(response);
            }

            if (flightOpt.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Flight not found!");
                return ResponseEntity.badRequest().body(response);
            }

            User user = userOpt.get();
            Flight flight = flightOpt.get();

            // 4. Check if user already has active booking for this flight
            boolean alreadyBooked = bookingRepository.existsByUserAndFlightAndStatus(
                    user, flight, BookingStatus.BOOKED);

            if (alreadyBooked) {
                response.put("status", "error");
                response.put("message", "You already have an active booking for this flight!");
                return ResponseEntity.badRequest().body(response);
            }

            boolean seatTaken = bookingRepository.existsByFlightAndSeatNoAndStatus(
                    flight, seatNo, BookingStatus.BOOKED);

            if (seatTaken) {
                response.put("status", "error");
                response.put("message", "Seat " + seatNo + " is already booked!");
                return ResponseEntity.badRequest().body(response);
            }

            Booking booking = new Booking(user, flight, seatNo);
            Booking savedBooking = bookingRepository.save(booking);


            response.put("status", "success");
            response.put("message", "Booking created successfully!");
            response.put("bookingId", savedBooking.getId());
            response.put("bookingDetails", Map.of(
                    "user", user.getName(),
                    "flight", flight.getFlightNumber(),
                    "route", flight.getOrigin() + " → " + flight.getDestination(),
                    "seatNo", seatNo,
                    "departureTime", flight.getDepartureTime()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            // Handle any errors (invalid data types, etc.)
            response.put("status", "error");
            response.put("message", "Invalid request data: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PutMapping("/user/{userId}/cancel/{bookingId}")
    public ResponseEntity<Map<String, Object>> cancelUserBooking(
            @PathVariable Long userId,
            @PathVariable Long bookingId) {

        Map<String, Object> response = new HashMap<>();

        try {
            // 1. Validate user exists
            if (!userRepository.existsById(userId)) {
                response.put("status", "error");
                response.put("message", "User not found!");
                return ResponseEntity.status(404).body(response);
            }

            // 2. Find the booking
            Optional<Booking> bookingOpt = bookingRepository.findById(bookingId);

            if (bookingOpt.isEmpty()) {
                response.put("status", "error");
                response.put("message", "Booking not found!");
                return ResponseEntity.status(404).body(response);
            }

            Booking booking = bookingOpt.get();

            // 3. ✅ CRITICAL SECURITY: Verify booking belongs to user
            if (!booking.getUser().getId().equals(userId)) {
                response.put("status", "error");
                response.put("message", "Access denied! You can only cancel your own bookings.");
                return ResponseEntity.status(403).body(response); // 403 Forbidden
            }

            // 4. Check if already cancelled
            if (booking.getStatus() == BookingStatus.CANCELLED) {
                response.put("status", "error");
                response.put("message", "Booking is already cancelled!");
                return ResponseEntity.badRequest().body(response);
            }

            // 5. Cancel booking
            booking.setStatus(BookingStatus.CANCELLED);
            Booking updatedBooking = bookingRepository.save(booking);

            response.put("status", "success");
            response.put("message", "Booking cancelled successfully!");
            response.put("cancelledBooking", Map.of(
                    "bookingId", updatedBooking.getId(),
                    "flight", updatedBooking.getFlight().getFlightNumber(),
                    "route", updatedBooking.getFlight().getOrigin() + " → " + updatedBooking.getFlight().getDestination(),
                    "seatNo", updatedBooking.getSeatNo(),
                    "status", updatedBooking.getStatus()
            ));

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error cancelling booking: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserBookings(@PathVariable Long userId) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 1. Validate user exists
            Optional<User> userOpt = userRepository.findById(userId);
            if (userOpt.isEmpty()) {
                response.put("status", "error");
                response.put("message", "User not found!");
                return ResponseEntity.status(404).body(response);
            }

            User user = userOpt.get();

            // 2. Retrieve bookings
            var bookings = bookingRepository.findByUser(user);

            response.put("status", "success");
            response.put("bookings", bookings.stream().map(booking -> Map.of(
                    "bookingId", booking.getId(),
                    "flight", booking.getFlight().getFlightNumber(),
                    "route", booking.getFlight().getOrigin() + " → " + booking.getFlight().getDestination(),
                    "seatNo", booking.getSeatNo(),
                    "status", booking.getStatus(),
                    "bookingDate", booking.getBookingDate()
            )).toList());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error retrieving bookings: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }


}