package org.project.sembs.evntmngmtbksytm.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "events", indexes = {
        @Index(name = "idx_title", columnList = "title"),
        @Index(name = "idx_status", columnList = "status"),
        @Index(name = "idx_city", columnList = "city"),
        @Index(name = "idx_country", columnList = "country")
})
@Getter
@Setter
public class Event {

    @Id
    @GeneratedValue(generator = "uuid2")
    @Column(columnDefinition = "uuid", updatable = false, nullable = false)
    private UUID id;

    @NotBlank(message = "Title cannot be blank")
    @Column(nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String description;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime startDateTime;

    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime endDateTime;

    @NotBlank(message = "Venue name cannot be blank")
    @Column(nullable = false)
    private String venueName;

    @NotBlank(message = "Address line cannot be blank")
    @Column(nullable = false)
    private String addressLine;

    @NotBlank(message = "City cannot be blank")
    @Column(nullable = false, length = 100)
    private String city;

    @NotBlank(message = "Country cannot be blank")
    @Column(nullable = false, length = 100)
    private String country;

    @NotBlank(message = "Postal code cannot be blank")
    @Column(length = 20, nullable = false)
    private String postalCode;

    @Column(precision = 9, scale = 6)
    private BigDecimal latitude;

    @Column(precision = 9, scale = 6)
    private BigDecimal longitude;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    private Integer capacity;

    @Column(nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer currentAttendeeCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @CreationTimestamp
    @Column(updatable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false, columnDefinition = "TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP")
    private OffsetDateTime updatedAt;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE}, fetch = FetchType.LAZY)
    @JoinTable(
            name = "event_categories",
            joinColumns = @JoinColumn(name = "event_id"), // FK column for Event in the join table (event_categories)
            inverseJoinColumns = @JoinColumn(name = "category_id") // FK column for Category in join table (event_categories)
    )
    @BatchSize(size = 10)
    private Set<Category> categories = new HashSet<>();

    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    Set<Booking> bookings = new HashSet<>();

    public Event() {
        this.status = Status.DRAFT;
        this.currentAttendeeCount = 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        if (id == null) { // For transient entities
            return super.equals(o);
        }
        return id.equals(event.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : System.identityHashCode(this);
    }

    @Override
    public String toString() {
        return "Event{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", startDateTime=" + startDateTime +
                ", city='" + city + '\'' +
                ", status=" + status +
                '}';
    }

    public void addCategory(Category category) {
        this.categories.add(category);
        category.getEvents().add(this);
    }

    public void removeCategory(Category category) {
        this.categories.remove(category);
        category.getEvents().remove(this);
    }

    public void addBooking(Booking booking) {
        this.bookings.add(booking);
        booking.setEvent(this); // Crucial for mappedBy
    }

    public void removeBooking(Booking booking) {
        this.bookings.remove(booking);
        booking.setEvent(null); // Crucial for mappedBy and orphanRemoval
    }

    @AssertTrue(message = "End date must be greater than or equal to start date")
    public boolean isDateRangeValid() {
        if (startDateTime == null || endDateTime == null) {
            return true; // Let @NotNull handle null validation
        }
        return !endDateTime.isBefore(startDateTime);
    }

    @AssertTrue(message = "Current attendee count cannot exceed capacity")
    @Transient
    public boolean isAttendeeCountValid() {
        if (currentAttendeeCount == null || capacity == null) {
            return true; // Let @NotNull handle null validation
        }
        return currentAttendeeCount <= capacity;
    }
}
