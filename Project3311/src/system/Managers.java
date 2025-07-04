package system;

import java.nio.file.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JOptionPane;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

public abstract class Managers {
    int id;
    String name;
    String email;
    static String CSV_FILE = "src/system/resources/managers.csv";
}

// Singleton implementation for supermanager
class SuperManager extends Managers {
    private static SuperManager instance;

    // Private constructor to prevent instantiation
    private SuperManager() {
    	this.id = 0;
    	this.email = "admin@test.com";
    	this.name = "admin";
    	
    }

    // Public method to provide access to the single instance
    public static SuperManager getInstance() {
        if (instance == null) {
            instance = new SuperManager();
        }
        return instance;
    }
    
    // Public method to provide access to the single instance
    static String[] generateManager() {
    	
    	String id = generateUserId();
    	String name = "manager" + id;
    	String email = "manager" + id + "@test.com";
    	String password = generatePassword();
    	// Write to CSV file
        try {
        	Path filePath = Paths.get(CSV_FILE).toAbsolutePath();

            // Create the file if it does not exist
            boolean fileExists = Files.exists(filePath);
            try (BufferedWriter writer = new BufferedWriter(new FileWriter((CSV_FILE), true))) {
                
                // Write user data including id if available
                writer.write(name + "," + id + "," + email + "," +  password + "," + "N"+ "\n");

            }
        } catch (IOException e) {
        	
        
            e.printStackTrace();
        }
        String[] info = {name, password};
        return  info;
    }
    
    private static String generateUserId() {
	    int lastId = 0;
	    
	    try {
	        Path filePath = Paths.get(CSV_FILE).toAbsolutePath(); // Use users.csv for IDs
	        
	        // Read the existing file
	        if (Files.exists(filePath)) {
	            try (BufferedReader reader = new BufferedReader(new FileReader(CSV_FILE))) {
	                String line;
	                boolean isHeader = true;  // Flag to skip the header row
	                while ((line = reader.readLine()) != null) {
	                    // Skip the header line
	                    if (isHeader) {
	                        isHeader = false;
	                        continue;
	                    }
	                    
	                    line = line.trim();  // Remove leading/trailing spaces

					    if (line.isEmpty()) {
					        break; // Skip completely empty lines
					    }
	                    String[] columns = line.split(",");
	                    try {
	                        int id = Integer.parseInt(columns[1]); // Now the ID is in the second column
	                        lastId = Math.max(lastId, id); // Get the maximum ID
	                    } catch (NumberFormatException e) {
	                        // Skip any rows where the ID is not a valid integer
	                        continue;
	                    }
	                }
	            }
	        }
	        else {
	        	try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath.toFile(), true))) {
	        		writer.write("name,id,email,password,Super\n");
	        		writer.write("admin,0,admin@test.com,admin,Y\n");
	            }
					
				
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	    
	    // Return the next ID as a string
	    return String.valueOf(lastId + 1);
	}
    
    // Function to generate a random 6-letter password
    public static String generatePassword() {
    	String uppercase = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String lowercase = "abcdefghijklmnopqrstuvwxyz";
        String numbers = "0123456789";
        String special = "!@#$%^&*()-_+=<>?/{}[]";
        Random random = new Random();
        StringBuilder password = new StringBuilder(12);  // Increased password length to 12

        // Ensure password contains at least one character from each set
        password.append(uppercase.charAt(random.nextInt(uppercase.length())));
        password.append(lowercase.charAt(random.nextInt(lowercase.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        // Fill the rest of the password with random characters from all sets
        String allCharacters = uppercase + lowercase + numbers + special;
        for (int i = 4; i < 12; i++) {
            int index = random.nextInt(allCharacters.length());
            password.append(allCharacters.charAt(index));
        }

        // Shuffle the password to avoid predictable patterns
        StringBuilder shuffledPassword = new StringBuilder();
        while (password.length() > 0) {
            int index = random.nextInt(password.length());
            shuffledPassword.append(password.charAt(index));
            password.deleteCharAt(index);
        }

        return shuffledPassword.toString();
    }
    
}

class Manager extends Managers {
    // Private static instance of the Manager class
    private static Manager instance;

    // Constructor is private to prevent direct instantiation
    private Manager(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    // Public method to provide access to the single instance of the Manager class
    public static Manager getInstance(int id, String name, String email) {
        if (instance == null) {
            instance = new Manager(id, name, email);
        }
        return instance;
    }

    // Getter methods for Manager's fields if needed
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}




