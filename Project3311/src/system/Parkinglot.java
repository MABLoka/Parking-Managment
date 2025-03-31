package system;

import java.util.ArrayList;

public class Parkinglot {
    private int id;
    private String name;
    private String location;
    
    private ArrayList<Parkingspace> spaces;
    private ParkingLotState currentState;
    private boolean state;

    public boolean isState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public Parkinglot(int id, String name, String location, boolean state,int numSpaces) {
        this.id = id;
        this.name = name;
        this.location = location;
        this.spaces = new ArrayList<>();
        this.state = state;  // enabled by default
        this.currentState = new AvailableState();  // default state

        
    }

    public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	

	/**
     * Attempts to book the given space with the specified booking.
     * Fails if the lot is disabled or is in OccupiedState.
     */
    public boolean book(Parkingspace space, Booking booking) {
        if (!state) {
            System.out.println("Invalid Selection: Parkinglot is DISABLED.");
            return false;
        }
        if (currentState instanceof OccupiedState) {
            System.out.println("Invalid Selection: Parkinglot is FULLY OCCUPIED.");
            return false;
        }

        if (space.isAvailable()) {
            space.setBooking(booking);
            System.out.println("SUCCESS: Space " + space.getId() + " booked for license plate: " + booking.getLicensePlate());
            // If no available spaces remain, change the state.
            if (getAvailableSpaces().isEmpty()) {
                setState(new OccupiedState());
            }
            return true;
        } else {
            System.out.println("ERROR: Space " + space.getId() + " is NOT available.");
            return false;
        }
    }

    public void cancelBooking(String licensePlate) {
        for (Parkingspace space : spaces) {
            if (space.getBooking() != null && space.getBooking().getLicensePlate().equals(licensePlate)) {
                space.setBooking(null);
                System.out.println("SUCCESS: Booking canceled for plate: " + licensePlate);
                // Reset state when a space becomes available.
                setState(new AvailableState());
                return;
            }
        }
        System.out.println("ERROR: No booking found for plate: " + licensePlate);
    }

    public ArrayList<Parkingspace> getAvailableSpaces() {
        ArrayList<Parkingspace> available = new ArrayList<>();
        for (Parkingspace space : spaces) {
            if (space.isAvailable()) {
                available.add(space);
            }
        }
        return available;
    }
    

    public void checkAvailability() {
        currentState.checkAvailability(this);
    }

    public void setState(ParkingLotState state) {
        this.currentState = state;
    }

    public int getId() {
        return id;
    }

    public ArrayList<Parkingspace> getSpaces() {
        return spaces;
    }
    
    public void addSpace(Parkingspace space) {
        this.spaces.add(space);
    }

    public void enableLot() {
        state = true;
        setState(new AvailableState());
        System.out.println("Parkinglot " + id + " has been ENABLED.");
    }

    public void disableLot() {
        state = false;
        setState(new OccupiedState());
        System.out.println("Parkinglot " + id + " has been DISABLED.");
    }

    // --- STATE PATTERN (Nested Classes) ---
    private interface ParkingLotState {
        void checkAvailability(Parkinglot parkinglot);
    }

    private class AvailableState implements ParkingLotState {
        @Override
        public void checkAvailability(Parkinglot parkinglot) {
            System.out.println("Parkinglot " + parkinglot.getId() + " is AVAILABLE.");
        }
    }

    private class OccupiedState implements ParkingLotState {
        @Override
        public void checkAvailability(Parkinglot parkinglot) {
            System.out.println("Invalid Selection: Parkinglot " + parkinglot.getId() + " is fully occupied or disabled.");
        }
    }

    // --- PROXY PATTERN (Nested Static Class) ---
    public static class ParkinglotProxy {
        private Parkinglot realParkinglot;

        public ParkinglotProxy(Parkinglot realParkinglot) {
            this.realParkinglot = realParkinglot;
        }

        public void displayInfo() {
            System.out.println("Parkinglot ID: " + realParkinglot.getId());
            System.out.println("Enabled: " + realParkinglot.state);
            System.out.println("Available Spaces: " + realParkinglot.getAvailableSpaces().size());
        }

        public boolean book(Parkingspace space, Booking booking) {
            System.out.println("Proxy: Attempting to book space " + space.getId());
            return realParkinglot.book(space, booking);
        }

        public void cancel(String licensePlate) {
            System.out.println("Proxy: Attempting to cancel booking for plate " + licensePlate);
            realParkinglot.cancelBooking(licensePlate);
        }
    }
}
