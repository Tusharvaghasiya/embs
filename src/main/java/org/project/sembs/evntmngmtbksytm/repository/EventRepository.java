package org.project.sembs.evntmngmtbksytm.repository;

import org.project.sembs.evntmngmtbksytm.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EventRepository extends JpaRepository<Event, UUID> {

}
