package system;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import javax.swing.JOptionPane;

import com.opencsv.CSVReader;
import com.opencsv.CSVWriter;
import com.opencsv.exceptions.CsvValidationException;

public class Sensor {
    private Parkingspace parkingSpace;
    private Parkinglot lot;
    private boolean activity;
    private String state;
	private ArrayList<Parkinglot> lots;

    public Sensor(Parkingspace parkingSpace, int lotId) {
    	
    	try {
			this.lots = loadCSVData();
		} catch (CsvValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	this.lot = lots.get(lotId);
    	
        this.parkingSpace = parkingSpace;
        this.activity = false;
        this.state = "Idle";
    }

    public void printNavigationInfo() {
        System.out.println("Navigation info for Parking Space " + parkingSpace.getId());
    }

    public void update(int time) throws CsvValidationException {
        // Simulate an update that checks occupancy at the given time.
        String occupant = checkOccupied(time);
        if (occupant != null) {
        	changeSpaceStatus(false, lot.getId(), parkingSpace.getId());
        	updateFile(parkingSpace.getId());
            System.out.println("Parking Space " + parkingSpace.getId() + " is occupied by " + occupant);
        } else {
            System.out.println("Parking Space " + parkingSpace.getId() + " is available.");
        }
        
    }

    public String checkOccupied(int time) {
        return parkingSpace.getOccupied().get(time);
    }

    public String checkParkingUse(int spaceNum) {
        return "Usage info for space " + spaceNum;
    }

    public void sendParkTime(int time, String licensePlate, String model) {
        System.out.println("Sending park time " + time + " for license " + licensePlate + " with model " + model);
    }

    public boolean isActivity() {
        return activity;
    }

    public void setActivity(boolean activity) {
        this.activity = activity;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
    
 // Load data from CSV
    private ArrayList<Parkinglot> loadCSVData() throws CsvValidationException {
        Path filePath = Paths.get("src", "system", "resources", "parkinglot.csv").toAbsolutePath();
        ArrayList<Parkinglot> lots = new ArrayList<>();
        int recordNum = 0;
        try (CSVReader reader = new CSVReader(new FileReader(filePath.toFile()))) {
            String[] nextLine;
            boolean isHeader = true;
            while ((nextLine = reader.readNext()) != null) {
                if (isHeader) {
                    isHeader = false;
                    continue;
                }
                Parkinglot lot = new Parkinglot(Integer.parseInt(nextLine[0]), nextLine[1], nextLine[2], nextLine[3].equals("Y") , 100);
                for (int i = 4; i < 104; i++) {
                    lot.addSpace(new Parkingspace(i - 3, nextLine[i].equals("Y")));
                }
                lots.add(lot);
                recordNum++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lots;
    }
    
    // Update table with lots data
    private void updateFile(int id) throws CsvValidationException {
    	Path filePath = Paths.get("src", "system", "resources", "parkinglot.csv").toAbsolutePath();
 		ArrayList<String[]> filelots = new ArrayList<>();

 	    try (CSVReader reader = new CSVReader(new FileReader(filePath.toFile()))) {
 	        String[] nextLine;

 	        while ((nextLine = reader.readNext()) != null) {
 	            
 	        	filelots.add(nextLine);

 	        }
 	    } catch (IOException e) {
 	        e.printStackTrace();
 	    }
 	    
 	   for (int i = 1; i < filelots.size(); i++) { // Skip header row (i = 1)
 	        int rowLotId = Integer.parseInt(filelots.get(i)[0]); // Get lot ID from CSV
 	        if (rowLotId == id) { // Match the correct row
 	            Parkinglot lot = lots.stream().filter(l -> l.getId() == id).findFirst().orElse(null);
 	            if (lot == null) return; // If no lot found, exit

 	            // Update lot status
 	           filelots.get(i)[3] = lot.isState() ? "Y" : "N";

 	            // Update parking spaces
 	            for (int j = 4; j < 104; j++) {
 	            	filelots.get(i)[j] = lot.getSpaces().get(j - 4).isAvailable() ? "Y" : "N";
 	            }
 	            break; // Exit loop after updating the correct row
 	        }
 	   }
 	    
 	    try (CSVWriter  writer  = new CSVWriter(new FileWriter(filePath.toFile()))) {
 	    	writer.writeAll(filelots);
 	    } catch (IOException e) {
 	        e.printStackTrace();
 	    }
    }

    // Change space status
    private void changeSpaceStatus(boolean enable, int selectedLotId, int selectedSpaceIndex) {
        if (selectedLotId == -1 || selectedSpaceIndex == -1) {
            return;
        }
        for (Parkinglot lot : lots) {
            if (lot.getId() == selectedLotId) {
            	if(enable)
            		lot.getSpaces().get(selectedSpaceIndex).enable();
            	else
            		lot.getSpaces().get(selectedSpaceIndex).disable();
                return;
            }
        }
    }
}
