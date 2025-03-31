package system;

// --------------------- ONLY THE BOOKING CLASS MODIFIED HERE ---------------------
public class Booking {
    private int bookingId;
    private String SpaceId;
    private int clientId;
    private double clientRate;   // Client rate calculated on the client side
	private String licensePlate; // Although not output in CSV per your header, we still store it.
    private String location;     // Represents the ParkingSpace location
    private int startTime;       // in seconds from midnight
    private int endTime;         // finishing time
    private String Date;         // finishing time
    private double cost;
    private int elapsedTime;
    private boolean deductable;
    private ClientInfoImplementor clientInfo;

    // Constructor with clientRate.
    public Booking(int bookingId, String SpaceId,int clientId, double clientRate, 
                   String licensePlate, String location, int startTime, int endTime, String Date) {
        this.bookingId = bookingId;
        this.SpaceId = SpaceId;
        this.clientId = clientId;
        this.clientRate = clientRate;
        this.licensePlate = licensePlate;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.Date = Date;
        this.elapsedTime = endTime - startTime;
        this.deductable = false;
        this.cost = calculateCost();
    }

    // Overloaded constructor with Bridge Pattern client info.
    public Booking(int bookingId, String SpaceId, int clientId, double clientRate,
                   String licensePlate, String location, int startTime, int endTime, String Date,
                   ClientInfoImplementor clientInfo) {
        this(bookingId, SpaceId, clientId, clientRate,  licensePlate, location, startTime, endTime, Date);
        this.clientInfo = clientInfo;
    }

    /**
     * Calculates the cost based on the duration (in hours) multiplied by the clientRate.
     * (MODIFICATION: Removed "licenceRate" from the cost calculation.)
     */
    private double calculateCost() {
        double durationInHours = (endTime - startTime) / 3600.0;
        // *** CHANGE BELOW: Only clientRate multiplied by durationInHours. ***
        return durationInHours * clientRate;
    }

    public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getBookingId() {
        return bookingId;
    }

    public int getClientId() {
        return clientId;
    }

    public double getClientRate() {
        return clientRate;
    }

    public String getLicensePlate() {
        return licensePlate;
    }

    public String getLocation() {
        return location;
    }

    public double getCost() {
        return cost;
    }

    public int getStartTime() {
        return startTime;
    }

    public int getEndTime() {
        return endTime;
    }

    public int getElapsedTime() {
        return elapsedTime;
    }

    public boolean isDeductable() {
        return deductable;
    }


    public ClientInfoImplementor getClientInfo() {
        return clientInfo;
    }

    // --- BRIDGE PATTERN ---
    public static interface ClientInfoImplementor {
        String getClientName();
        int getClientUniqueId();
    }

    public static class ConcreteClient implements ClientInfoImplementor {
        private String clientName;
        private int uniqueId;

        public ConcreteClient(String clientName, int uniqueId) {
            this.clientName = clientName;
            this.uniqueId = uniqueId;
        }

        @Override
        public String getClientName() {
            return clientName;
        }

        @Override
        public int getClientUniqueId() {
            return uniqueId;
        }
    }
    public String getDate() {
		return Date;
	}

	public String getSpaceId() {
		return SpaceId;
	}

	public void setSpaceId(String spaceId) {
		SpaceId = spaceId;
	}

	public void setDate(String date) {
		Date = date;
	}
    // --- ADAPTER PATTERN ---
    public static interface BookingAdapter {
        boolean bookParkingSpace(Parkinglot lot, int spaceId, Booking booking);
    }

    public static class BookingAdapterImpl implements BookingAdapter {
        @Override
        public boolean bookParkingSpace(Parkinglot lot, int spaceId, Booking booking) {
            for (Parkingspace space : lot.getSpaces()) {
                if (space.getId() == spaceId) {
                    return lot.book(space, booking);
                }
            }
            System.out.println("ERROR: No parking space with ID " + spaceId + " found.");
            return false;
        }
    }
}
