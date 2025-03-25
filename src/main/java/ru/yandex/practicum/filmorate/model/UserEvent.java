package ru.yandex.practicum.filmorate.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

/**
 * Represents an event related to a user in the system.
 * This class encapsulates the details of the event, including the timestamp,
 * user ID, event type, operation performed, event ID, and the associated entity ID.
 */
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserEvent {
    /**
     * Timestamp of the event in milliseconds since epoch.
     * This field indicates when the event occurred.
     */
    Long timestamp;

    /**
     * Unique identifier of the user associated with the event.
     * This field is used to track which user performed the action.
     */
    Long userId;

    /**
     * Type of the event (e.g., "LOGIN", "LOGOUT", "PURCHASE").
     * This field categorizes the event for easier processing and analysis.
     */
    String eventType;

    /**
     * Operation performed during the event (e.g., "CREATE", "UPDATE", "DELETE").
     * This field provides additional context about the nature of the event.
     */
    String operation;

    /**
     * Unique identifier of the event.
     * This field is used to distinguish between different events in the system.
     */
    long eventId;

    /**
     * Unique identifier of the entity associated with the event.
     * This field can refer to various entities, such as products, orders, etc.
     */
    Long entityId;
}