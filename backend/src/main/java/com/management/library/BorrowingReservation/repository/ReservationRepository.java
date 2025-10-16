package com.management.library.BorrowingReservation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.management.library.BorrowingReservation.entity.Reservation;

public interface ReservationRepository extends MongoRepository<Reservation, String> {
    java.util.List<Reservation> findByMemberId(String memberId);
    java.util.List<Reservation> findByMemberIdAndBookIdAndStatus(String memberId, String bookId, String status);
}



