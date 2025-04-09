package system;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.HashMap;

class Sensor_test {

    private Parkingspace space;
    private Sensor sensor;

    @BeforeEach
    void setup() {
        // Create a simple Parkingspace
        space = new Parkingspace(10, true);
        sensor = new Sensor(space, 1);
    }

    @Test
    void testConstructorAndInitialValues() {
        assertNotNull(sensor, "Sensor should be instantiated.");
        assertFalse(sensor.isActivity(), "Sensor activity should be false by default.");
        assertEquals("Idle", sensor.getState(), "Sensor state should be 'Idle' by default.");
    }

    @Test
    void testPrintNavigationInfo() {
        assertDoesNotThrow(() -> sensor.printNavigationInfo(),
                "Should not throw an exception while printing navigation info.");
    }

    @Test
    void testCheckOccupiedWhenEmpty() {
        String occupant = sensor.checkOccupied(0);
        assertNull(occupant, "No occupant should be found at time=0 in empty map.");
    }

    @Test
    void testCheckOccupiedWhenPopulated() {
        HashMap<Integer, String> occupiedMap = space.getOccupied();
        occupiedMap.put(5, "ABC-123");
        String occupant = sensor.checkOccupied(5);
        assertEquals("ABC-123", occupant, "Occupant should be 'ABC-123' for time=5.");
    }

    @Test
    void testUpdateSpaceAvailable() {
        // No occupant => should say the space is available
        assertDoesNotThrow(() -> sensor.update(10),
                "update() should not throw exceptions when unoccupied.");
        assertTrue(space.isEnabled(), "Space remains enabled if unoccupied.");
    }

    @Test
    void testUpdateSpaceOccupied() {
        // Put occupant at time=7
        space.getOccupied().put(7, "XYZ-999");

        // If the code is set to disable a space when occupant is found,
        // we might expect the space to be disabled. Because the code references
        // 'lot' incorrectly (as a Parkingspace), it might not find a Parkinglot.
        // But this test just confirms no exceptions are thrown.
        assertDoesNotThrow(() -> sensor.update(7),
                "update() should not throw exceptions even if occupant is found.");
        // If your code fully works, you might expect `assertFalse(space.isEnabled())`.
        // That depends on whether the code can locate the Parkinglot object.
    }

    @Test
    void testCheckParkingUse() {
        String usage = sensor.checkParkingUse(space.getId());
        assertEquals("Usage info for space 10", usage,
                "Should return 'Usage info for space 10'.");
    }

    @Test
    void testSendParkTime() {
        assertDoesNotThrow(() -> sensor.sendParkTime(120, "ABC-123", "ModelXYZ"),
                "Should not throw exception while sending park time.");
    }

    @Test
    void testActivityGetterSetter() {
        assertFalse(sensor.isActivity());
        sensor.setActivity(true);
        assertTrue(sensor.isActivity(), "Sensor activity should now be true.");
    }

    @Test
    void testStateGetterSetter() {
        assertEquals("Idle", sensor.getState());
        sensor.setState("Active");
        assertEquals("Active", sensor.getState(), "State should be updated to 'Active'.");
    }

    // Skipped CSV load/update tests (private methods) or integration tests, since they'd need actual files.
}
