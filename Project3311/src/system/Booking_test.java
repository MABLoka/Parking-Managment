package system;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class Booking_test {

    private Booking bookingBasic;
    private Booking bookingWithBridge;

    @BeforeEach
    void setup() {
        // Basic booking (no Bridge pattern client info)
        bookingBasic = new Booking(
                1,            // bookingId
                "Space-A",    // SpaceId
                1001,         // clientId
                2.0,          // clientRate
                "ABC-123",    // licensePlate
                "Lot-A",      // location
                3600,         // startTime = 1 hour in seconds
                7200,         // endTime = 2 hours in seconds
                "2025-05-01"  // date
        );

        // Booking with the Bridge pattern implementation
        Booking.ClientInfoImplementor clientInfo =
                new Booking.ConcreteClient("John Doe", 9999);

        bookingWithBridge = new Booking(
                2,
                "Space-B",
                1002,
                3.5,
                "XYZ-987",
                "Lot-B",
                0,       // start at midnight
                3600,    // 1 hour later
                "2025-05-02",
                clientInfo
        );
    }

    @Test
    void testConstructorBasicFields() {
        assertEquals(1, bookingBasic.getBookingId(), "Booking ID should be 1.");
        assertEquals("Space-A", bookingBasic.getSpaceId(), "SpaceId should be 'Space-A'.");
        assertEquals(1001, bookingBasic.getClientId(), "Client ID should be 1001.");
        assertEquals(2.0, bookingBasic.getClientRate(), "Client rate should be 2.0.");
        assertEquals("ABC-123", bookingBasic.getLicensePlate(), "License plate should be 'ABC-123'.");
        assertEquals("Lot-A", bookingBasic.getLocation(), "Location should be 'Lot-A'.");
        assertEquals("2025-05-01", bookingBasic.getDate(), "Date should be '2025-05-01'.");

        // Elapsed time = 7200 - 3600 = 3600 seconds
        assertEquals(3600, bookingBasic.getElapsedTime(), "Elapsed time should be 3600.");

        // Duration in hours = 3600 / 3600 = 1 hour; cost = 1 * 2.0 = 2.0
        assertEquals(2.0, bookingBasic.getCost(), 0.0001,
                "Cost should be durationInHours * clientRate = 2.0.");

        assertFalse(bookingBasic.isDeductable(),
                "Deductable should default to false.");
    }

    @Test
    void testConstructorBridgePatternFields() {
        assertNotNull(bookingWithBridge.getClientInfo(),
                "ClientInfoImplementor should not be null.");
        Booking.ClientInfoImplementor info = bookingWithBridge.getClientInfo();

        assertEquals("John Doe", info.getClientName(),
                "Client name in bridging pattern should be 'John Doe'.");
        assertEquals(9999, info.getClientUniqueId(),
                "Client unique ID in bridging pattern should be 9999.");

        // startTime=0, endTime=3600 => 1 hour => cost=1*3.5=3.5
        assertEquals(3.5, bookingWithBridge.getCost(), 0.0001,
                "Cost should be 3.5 for one hour at clientRate=3.5.");
    }

    @Test
    void testSetSpaceIdAndDate() {
        // Confirm initial
        assertEquals("Space-A", bookingBasic.getSpaceId());
        assertEquals("2025-05-01", bookingBasic.getDate());

        // Update
        bookingBasic.setSpaceId("Space-C");
        bookingBasic.setDate("2025-06-01");

        assertEquals("Space-C", bookingBasic.getSpaceId(),
                "SpaceId should be updated to 'Space-C'.");
        assertEquals("2025-06-01", bookingBasic.getDate(),
                "Date should be updated to '2025-06-01'.");
    }

    @Test
    void testSetStartTimeDoesNotRecalculateCost() {
        // Original cost for 1 hour is 2.0
        assertEquals(2.0, bookingBasic.getCost(), 0.0001);

        // Update the startTime
        bookingBasic.setStartTime(7200); // effectively 2h -> 2h, no real "duration" if we re-calc
        // But no recalculation logic is triggered in the code after the constructor.

        // So cost remains the same
        assertEquals(2.0, bookingBasic.getCost(),
                "Cost remains unchanged since no recalculation after construction.");
    }

    // ---- ADAPTER PATTERN TESTS ----
    @Test
    void testBookingAdapterSuccess() {
        // Create a Parkinglot with 1 space
        Parkinglot lot = new Parkinglot(500, "AdapterLot", "Center", true, 2);
        Parkingspace space = new Parkingspace(100, true);
        lot.addSpace(space);

        // Create adapter
        Booking.BookingAdapter adapter = new Booking.BookingAdapterImpl();

        // Attempt booking
        boolean result = adapter.bookParkingSpace(lot, 100, bookingBasic);
        assertTrue(result, "Booking should succeed if space is free and lot is enabled.");

        assertNotNull(space.getBooking(), "Space #100 should have the booking now.");
        assertEquals("ABC-123", space.getBooking().getLicensePlate(),
                "License plate should be 'ABC-123'.");
    }

    @Test
    void testBookingAdapterNoSuchSpace() {
        // Empty lot, no spaces
        Parkinglot lot = new Parkinglot(501, "NoSpaceLot", "East", true, 0);

        Booking.BookingAdapter adapter = new Booking.BookingAdapterImpl();
        boolean result = adapter.bookParkingSpace(lot, 999, bookingBasic);
        assertFalse(result, "Booking should fail if the space ID doesn't exist.");
    }
}
