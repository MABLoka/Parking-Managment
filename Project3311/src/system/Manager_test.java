package system;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class Manager_test {

    @Test
    void testSuperManagerSingleton() {
        // Test that SuperManager is a singleton and always returns the same instance
        SuperManager superManager1 = SuperManager.getInstance();
        SuperManager superManager2 = SuperManager.getInstance();
        
        // Assert that both instances are the same
        assertSame(superManager1, superManager2, "SuperManager should be a singleton.");
    }

    @Test
    void testGenerateManager() {
        // Test that SuperManager's generateManager correctly generates a manager and writes to CSV
        String[] managerInfo = SuperManager.generateManager();
        
        assertNotNull(managerInfo, "Manager info should not be null.");
        assertEquals(2, managerInfo.length, "Manager info should contain name and password.");
        
        // Check if a CSV file is created and contains the manager data
        Path path = Paths.get(SuperManager.CSV_FILE);
        assertTrue(Files.exists(path), "CSV file should exist.");
        
        try (BufferedReader reader = new BufferedReader(new FileReader(SuperManager.CSV_FILE))) {
            String line = reader.readLine();
            line = reader.readLine();
            line = reader.readLine();
            assertNotNull(line, "CSV file should not be empty.");
            assertTrue(line.contains(managerInfo[0]), "CSV file should contain the manager's name.");
        } catch (IOException e) {
            fail("IOException while reading CSV file: " + e.getMessage());
        }
    }

    @Test
    void testManagerSingleton() {
        // Test that Manager's getInstance() returns the same instance
        Manager manager1 = Manager.getInstance(1, "Manager1", "manager1@test.com");
        Manager manager2 = Manager.getInstance(1, "Manager1", "manager1@test.com");
        
        // Assert that both instances are the same
        assertSame(manager1, manager2, "Manager should be a singleton.");
    }

    @Test
    void testManagerGetters() {
        // Test Manager getter methods
        Manager manager = Manager.getInstance(1, "Manager1", "manager1@test.com");
        
        assertEquals(1, manager.getId(), "Manager ID should be correct.");
        assertEquals("Manager1", manager.getName(), "Manager name should be correct.");
        assertEquals("manager1@test.com", manager.getEmail(), "Manager email should be correct.");
    }

    @Test
    void testGeneratePassword() {
        // Test that the generated password is 12 characters long and contains valid characters
        String password = SuperManager.generatePassword();
        
        assertEquals(12, password.length(), "Password length should be 12 characters.");
        assertTrue(password.matches(".*[A-Z].*"), "Password should contain at least one uppercase letter.");
        assertTrue(password.matches(".*[a-z].*"), "Password should contain at least one lowercase letter.");
        assertTrue(password.matches(".*[0-9].*"), "Password should contain at least one number.");
        assertTrue(password.matches(".*[!@#$%^&*()-_+=<>?/{}\\[\\]].*"), "Password should contain at least one special character.");
    }

    @AfterEach
    void cleanup() {
        // Clean up after each test, for example, delete the CSV file created during tests
        try {
            Path path = Paths.get(SuperManager.CSV_FILE);
            if (Files.exists(path)) {
                Files.delete(path);
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(path.toFile(), true))) {
	        		writer.write("name,id,email,password,Super\n");
	        		writer.write("admin,0,admin@test.com,admin,Y\n");
	            }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
