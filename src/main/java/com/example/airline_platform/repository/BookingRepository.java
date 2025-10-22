package com.example.airline_platform.repository;


import com.example.airline_platform.Entity.Booking;
import com.example.airline_platform.Entity.BookingStatus;
import com.example.airline_platform.Entity.Flight;
import com.example.airline_platform.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByUserId(Long userId);
    List<Booking> findByUser(User user);
    List<Booking> findByFlight(Flight flight);
    List<Booking> findByStatus(BookingStatus status);
    Optional<Booking>  findByUserAndFlight(User user, Flight flight);

    boolean existsByFlightAndSeatNoAndStatus(Flight flight, Integer seatNo, BookingStatus status);

    boolean existsByUserAndFlightAndStatus(User user, Flight flight, BookingStatus status);

}
