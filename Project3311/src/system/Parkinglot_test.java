package system;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

class Parkinglot_test {

    @Test
    void testConstructorAndBasicGettersSetters() {
        Parkinglot lot = new Parkinglot(1, "Lot A", "Campus North", true, 10);
        assertEquals(1, lot.getId(), "ID should be 1.");
        assertEquals("Lot A", lot.getName(), "Name should be 'Lot A'.");
        assertEquals("Campus North", lot.getLocation(), "Location should be 'Campus North'.");
        assertTrue(lot.isState(), "Lot should be enabled initially.");

        lot.setName("Lot B");
        lot.setLocation("Campus South");
        assertEquals("Lot B", lot.getName());
        assertEquals("Campus South", lot.getLocation());
    }

    @Test
    void testAddSpaceAndGetSpaces() {
        Parkinglot lot = new Parkinglot(2, "Lot C", "Campus East", true, 5);
        assertTrue(lot.getSpaces().isEmpty(), "Initially no spaces.");

        Parkingspace s1 = new Parkingspace(101, true);
        Parkingspace s2 = new Parkingspace(102, true);
        lot.addSpace(s1);
        lot.addSpace(s2);

        ArrayList<Parkingspace> spaces = lot.getSpaces();
        assertEquals(2, spaces.size(), "There should be 2 spaces.");
        assertTrue(spaces.contains(s1));
        assertTrue(spaces.contains(s2));
    }

    @Test
    void testBookingSuccess() {
        Parkinglot lot = new Parkinglot(3, "Lot D", "Campus West", true, 2);
        Parkingspace s1 = new Parkingspace(201, true);
        Parkingspace s2 = new Parkingspace(202, true);
        lot.addSpace(s1);
        lot.addSpace(s2);

        Booking booking1 = new Booking(
                10, "Space-201", 999, 2.5, "ABC-123", "Lot-D", 0, 3600, "2025-05-01");
        assertTrue(lot.book(s1, booking1), "Should book successfully.");
        assertEquals("ABC-123", s1.getBooking().getLicensePlate());

        Booking booking2 = new Booking(
                11, "Space-202", 1000, 3.0, "XYZ-999", "Lot-D", 3600, 7200, "2025-05-02");
        assertTrue(lot.book(s2, booking2));
        assertEquals("XYZ-999", s2.getBooking().getLicensePlate());
    }

    @Test
    void testBookingWhenLotIsDisabled() {
        Parkinglot lot = new Parkinglot(4, "Lot E", "Campus Center", false, 2);
        Parkingspace s1 = new Parkingspace(301, true);
        lot.addSpace(s1);

        Booking booking = new Booking(20, "Space-301", 1001, 2.0, "NOPE-111", "Lot-E", 0, 1800, "2025-05-01");
        assertFalse(lot.book(s1, booking), "Booking should fail when lot is disabled.");
        assertNull(s1.getBooking(), "Space remains unbooked.");
    }

    @Test
    void testBookingWhenAllSpacesOccupied() {
        Parkinglot lot = new Parkinglot(5, "Lot F", "Campus North", true, 2);
        Parkingspace s1 = new Parkingspace(401, true);
        lot.addSpace(s1);

        // Book the only space
        Booking booking1 = new Booking(30, "Space-401", 1101, 1.5, "CAR-456", "Lot-F", 0, 3600, "2025-05-02");
        assertTrue(lot.book(s1, booking1));

        // Now there's only one space, it's taken => lot state likely changes to OccupiedState
        // Try to book again
        Booking booking2 = new Booking(31, "Space-401", 1102, 1.5, "CAR-789", "Lot-F", 3600, 7200, "2025-05-02");
        assertFalse(lot.book(s1, booking2),
                "Booking should fail when the single space is already occupied.");
        assertEquals("CAR-456", s1.getBooking().getLicensePlate());
    }

    @Test
    void testCancelBookingSuccess() {
        Parkinglot lot = new Parkinglot(6, "Lot G", "Campus South", true, 2);
        Parkingspace s1 = new Parkingspace(501, true);
        lot.addSpace(s1);

        Booking booking = new Booking(40, "Space-501", 1201, 2.0, "DEL-999", "Lot-G", 0, 1800, "2025-05-01");
        lot.book(s1, booking);
        assertNotNull(s1.getBooking());

        lot.cancelBooking("DEL-999");
        assertNull(s1.getBooking(), "Space booking should be canceled.");
    }

    @Test
    void testGetAvailableSpaces() {
        Parkinglot lot = new Parkinglot(7, "Lot H", "Campus East", true, 3);
        Parkingspace s1 = new Parkingspace(601, true);
        Parkingspace s2 = new Parkingspace(602, true);
        Parkingspace s3 = new Parkingspace(603, true);
        lot.addSpace(s1);
        lot.addSpace(s2);
        lot.addSpace(s3);

        // Book s1
        lot.book(s1, new Booking(50, "Space-601", 1301, 2.0, "TAKEN-111", "Lot-H", 0, 3600, "2025-05-03"));
        ArrayList<Parkingspace> available = lot.getAvailableSpaces();
        assertEquals(2, available.size());
        assertTrue(available.contains(s2));
        assertTrue(available.contains(s3));
    }

    @Test
    void testEnableDisableLot() {
        Parkinglot lot = new Parkinglot(8, "Lot I", "Campus West", true, 1);
        Parkingspace s1 = new Parkingspace(701, true);
        lot.addSpace(s1);

        assertTrue(lot.isState(), "Lot is enabled initially.");
        lot.disableLot();
        assertFalse(lot.isState(), "Lot is disabled now.");

        // Booking fails
        assertFalse(lot.book(s1, new Booking(60, "Space-701", 1401, 2.0, "ZZZ-999", "Lot-I", 0, 3600, "2025-05-04")));

        // Re-enable
        lot.enableLot();
        assertTrue(lot.isState());
        // Booking now succeeds
        assertTrue(lot.book(s1, new Booking(61, "Space-701", 1402, 3.0, "HERO-123", "Lot-I", 3600, 7200, "2025-05-04")));
    }

    @Test
    void testCheckAvailability() {
        Parkinglot lot = new Parkinglot(9, "Lot J", "Campus North", true, 1);
        Parkingspace s1 = new Parkingspace(801, true);
        lot.addSpace(s1);

        // Typically prints "is AVAILABLE."
        lot.checkAvailability();

        // Book the only space => Occupied
        lot.book(s1, new Booking(70, "Space-801", 1501, 2.0, "CAP-111", "Lot-J", 0, 3600, "2025-05-04"));

        // Typically prints "is fully occupied or disabled."
        lot.checkAvailability();
    }

    @Test
    void testParkinglotProxy() {
        Parkinglot realLot = new Parkinglot(10, "Lot ProxyTest", "Campus Proxy", true, 2);
        Parkingspace s1 = new Parkingspace(901, true);
        realLot.addSpace(s1);

        Parkinglot.ParkinglotProxy proxy = new Parkinglot.ParkinglotProxy(realLot);

        proxy.displayInfo(); // prints info
        boolean success = proxy.book(s1, new Booking(80, "Space-901", 1601, 2.5, "PROXY-123", "Lot-ProxyTest", 0, 3600, "2025-05-05"));
        assertTrue(success, "Proxy booking should succeed.");
        assertNotNull(s1.getBooking());

        proxy.cancel("PROXY-123");
        assertNull(s1.getBooking(), "Booking should be canceled via proxy.");
    }
}
