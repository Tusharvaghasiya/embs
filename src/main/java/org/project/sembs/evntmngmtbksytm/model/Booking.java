package org.project.sembs.evntmngmtbksytm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "bookings",
        indexes = {
                @Index(name = "idx_booking_date_time", columnList = "booking_date_time")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"event_id", "attendee_id"})
        }
)
@Getter
@Setter
public class Booking {

    @Id
    @GeneratedValue(generator = "uuid2")
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendee_id", nullable = false)
    private User attendee;

    @Column(nullable = false, updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime bookingDateTime;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 1", updatable = false)
    private Integer numberOfTickets;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @CreationTimestamp
    @Column(updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime updatedAt;

    public Booking() {
        this.status = BookingStatus.CONFIRMED;
    }

    public Booking(Event event, User attendee, Integer numberOfTickets) {
        this(); // Calls the default constructor to set default status
        this.event = event;
        this.attendee = attendee;
        this.numberOfTickets = numberOfTickets;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        // If the id is null, then objects are equal if they are the same instance
        // If ids are non-null, compare by id
        if (id == null) {
            return super.equals(o);
        }
        return id.equals(booking.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "Booking{" +
                "id=" + id +
                ", eventId=" + (event != null ? event.getId() : null) + // Avoid full event serialization
                ", attendeeId=" + (attendee != null ? attendee.getId() : null) + // Avoid full user serialization
                ", bookingDateTime=" + bookingDateTime +
                ", numberOfTickets=" + numberOfTickets +
                ", status=" + status +
                '}';
    }
}
