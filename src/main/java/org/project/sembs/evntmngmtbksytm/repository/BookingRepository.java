package org.project.sembs.evntmngmtbksytm.repository;

import org.project.sembs.evntmngmtbksytm.model.Booking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface BookingRepository extends JpaRepository<Booking, UUID> {
}
