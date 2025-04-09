package system;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.HashMap;

class Parkingspace_test {

    @Test
    void testConstructorAndInitialValues() {
        Parkingspace space = new Parkingspace(101, true);

        assertEquals(101, space.getId(), "ID should be 101.");
        assertTrue(space.isEnabled(), "Should be enabled initially.");
        assertNull(space.getBooking(), "Booking should be null initially.");

        HashMap<Integer, String> occupiedMap = space.getOccupied();
        assertNotNull(occupiedMap, "Occupied map should be created.");
        assertTrue(occupiedMap.isEmpty(), "Occupied map should be empty initially.");
    }

    @Test
    void testIsAvailableWhenEnabledAndNoBooking() {
        Parkingspace space = new Parkingspace(102, true);
        assertTrue(space.isAvailable(),
                "Should be available if enabled and no booking assigned.");
    }

    @Test
    void testIsAvailableWhenDisabled() {
        Parkingspace space = new Parkingspace(103, false);
        assertFalse(space.isAvailable(), "Disabled space is not available.");
    }

    @Test
    void testIsAvailableWhenBooked() {
        Parkingspace space = new Parkingspace(104, true);
        Booking booking = new Booking(10, "Space-104", 900, 2.0, "ABC-104", "Lot-1", 0, 3600, "2025-05-01");
        space.setBooking(booking);

        assertFalse(space.isAvailable(),
                "Space is not available if it already has a booking.");
    }

    @Test
    void testSetBookingAndGetBooking() {
        Parkingspace space = new Parkingspace(105, true);
        assertNull(space.getBooking());
        Booking booking = new Booking(11, "Space-105", 901, 3.0, "XYZ-105", "Lot-2", 0, 3600, "2025-05-02");
        space.setBooking(booking);

        assertEquals(booking, space.getBooking(), "Should return the same Booking instance.");
    }

    @Test
    void testEnableDisable() {
        Parkingspace space = new Parkingspace(106, true);
        assertTrue(space.isEnabled());
        space.disable();
        assertFalse(space.isEnabled(), "Should be disabled after calling disable().");
        space.enable();
        assertTrue(space.isEnabled(), "Should be enabled after calling enable().");
    }

    @Test
    void testOccupiedMapUsage() {
        Parkingspace space = new Parkingspace(107, true);
        HashMap<Integer, String> occupiedMap = space.getOccupied();
        occupiedMap.put(5, "ABC-123");
        occupiedMap.put(6, "XYZ-999");

        assertEquals(2, occupiedMap.size(), "Should have 2 entries.");
        assertEquals("ABC-123", occupiedMap.get(5));
        assertEquals("XYZ-999", occupiedMap.get(6));
    }
}
