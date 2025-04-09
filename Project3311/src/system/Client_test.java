package system;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class Client_test {

	@Test
    void testStudentRate() {
        Client student = new student("John Doe", 1, "john.doe@example.com", "password123", 12345, "STUDENT");
        
        assertEquals(2.0, student.getParkingRate(), "Student's parking rate should be 2.");
    }

    @Test
    void testFacultyRate() {
        Client faculty = new faculty("Dr. Smith", 2, "dr.smith@example.com", "password123", 98765, "FACULTY");
        
        assertEquals(3.0, faculty.getParkingRate(), "Faculty's parking rate should be 3.");
    }

    @Test
    void testStaffRate() {
        Client staff = new staff("Jane Doe", 3, "jane.doe@example.com", "password123", 45678, "STAFF");
        
        assertEquals(3.0, staff.getParkingRate(), "Staff's parking rate should be 3.");
    }

    @Test
    void testVisitorRate() {
        Client visitor = new visitor("Guest", 4, "guest@example.com", "password123", "VISITOR");
        
        assertEquals(4.0, visitor.getParkingRate(), "Visitor's parking rate should be 4.");
    }

    @Test
    void testGetUniqueIdForStudent() {
        student s = new student("Student1", 1, "student1@example.com", "password123", 123, "STUDENT");
        assertEquals(123, s.getUniqueId(), "Unique ID for student should be 123.");
    }

    @Test
    void testGetUniqueIdForFaculty() {
        faculty f = new faculty("Faculty1", 2, "faculty1@example.com", "password123", 456, "FACULTY");
        assertEquals(456, f.getUniqueId(), "Unique ID for faculty should be 456.");
    }

    @Test
    void testGetUniqueIdForStaff() {
        staff st = new staff("Staff1", 3, "staff1@example.com", "password123", 789, "STAFF");
        assertEquals(789, st.getUniqueId(), "Unique ID for staff should be 789.");
    }

    @Test
    void testGetUniqueIdForVisitor() {
        visitor v = new visitor("Visitor1", 4, "visitor1@example.com", "password123", "VISITOR");
        assertEquals(0, v.getUniqueId(), "Unique ID for visitor should be 0.");
    }

    @Test
    void testClientFactoryCreateStudent() {
        Client student = clientFactory.getClientType("STUDENT", "John Doe", 1, "john.doe@example.com", "password123", 12345);
        
        assertNotNull(student, "Student should not be null.");
        assertEquals("John Doe", student.getName(), "Student name should be correct.");
        assertEquals(2.0, student.getParkingRate(), "Student parking rate should be 2.");
    }

    @Test
    void testClientFactoryCreateFaculty() {
        Client faculty = clientFactory.getClientType("FACULTY", "Dr. Smith", 2, "dr.smith@example.com", "password123", 98765);
        
        assertNotNull(faculty, "Faculty should not be null.");
        assertEquals("Dr. Smith", faculty.getName(), "Faculty name should be correct.");
        assertEquals(3.0, faculty.getParkingRate(), "Faculty parking rate should be 3.");
    }

    @Test
    void testClientFactoryCreateStaff() {
        Client staff = clientFactory.getClientType("STAFF", "Jane Doe", 3, "jane.doe@example.com", "password123", 45678);
        
        assertNotNull(staff, "Staff should not be null.");
        assertEquals("Jane Doe", staff.getName(), "Staff name should be correct.");
        assertEquals(3.0, staff.getParkingRate(), "Staff parking rate should be 3.");
    }

    @Test
    void testClientFactoryCreateVisitor() {
        Client visitor = clientFactory.getClientType("VISITOR", "Guest", 4, "guest@example.com", "password123", 0);
        
        assertNotNull(visitor, "Visitor should not be null.");
        assertEquals("Guest", visitor.getName(), "Visitor name should be correct.");
        assertEquals(4.0, visitor.getParkingRate(), "Visitor parking rate should be 4.");
    }

    @Test
    void testClientFactoryWithInvalidType() {
        Client invalidClient = clientFactory.getClientType("INVALID", "Invalid", 0, "invalid@example.com", "password123", 0);
        
        assertNull(invalidClient, "Client type should return null for invalid types.");
    }
}
