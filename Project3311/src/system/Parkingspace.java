package system;

import java.util.HashMap;

public class Parkingspace {
	private int id;
	private Parkinglot lot;
	private Sensor sensor;
	private Booking booking;
	private boolean isEnabled;
	private HashMap<Integer, String> occupied;
	
	public boolean isEnabled() {
		return isEnabled;
	}
	public void setEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}
	
	public Parkingspace(int id, boolean isEnabled) {
		this.id = id;
		this.lot = lot;

		this.isEnabled = isEnabled;
		// Initialize the occupied map (key: time, value: licensePlate).
		this.occupied = new HashMap<>();
	}
	public boolean isAvailable() {
		return isEnabled && booking == null;
	}

	public void setBooking(Booking booking) {
		this.booking = booking;
	}

	public Booking getBooking() {
		return booking;
	}

	public void enable() {
		isEnabled = true;
	}

	public void disable() {
		isEnabled = false;
	}

	public int getId() {
		return id;
	}

	public HashMap<Integer, String> getOccupied() {
		return occupied;
	}
	
}
