package ru.practicum.shareit.booking.model;

public enum BookingTimeState {
    ALL,
    CURRENT,
    FUTURE,
    PAST;

    static BookingTimeState from(String state) {
        for (BookingTimeState bookingTimeState : BookingTimeState.values()) {
            if (bookingTimeState.name().equalsIgnoreCase(state)) {
                return bookingTimeState;
            }
        }
        return null;
    }
}
