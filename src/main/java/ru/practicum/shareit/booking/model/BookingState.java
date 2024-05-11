package ru.practicum.shareit.booking.model;

public enum BookingState {

    WAITING,
    APPROVED,
    REJECTED,
    CANCELED;

    static BookingState from(String state) {
        for (BookingState bookingState : BookingState.values()) {
            if (bookingState.name().equalsIgnoreCase(state)) {
                return bookingState;
            }
        }
        return null;
    }

}