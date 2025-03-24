package system;

// --------------------- ONLY THE BOOKING CLASS MODIFIED HERE ---------------------
public class Booking {
    private int bookingId;
    private int clientId;
    private double clientRate;   // Client rate calculated on the client side
    private double licenceRate;  // Licence rate remains as before
    private String licensePlate; // Although not output in CSV per your header, we still store it.
    private String location;     // Represents the ParkingSpace location
    private int startTime;       // in seconds from midnight
    private int endTime;         // finishing time
    private double cost;
    private int elapsedTime;
    private boolean deductable;
    private Payment bill;
    private ClientInfoImplementor clientInfo;

    // Constructor with clientRate.
    public Booking(int bookingId, int clientId, double clientRate, double licenceRate,
                   String licensePlate, String location, int startTime, int endTime) {
        this.bookingId = bookingId;
        this.clientId = clientId;
        this.clientRate = clientRate;
        this.licenceRate = licenceRate;
        this.licensePlate = licensePlate;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.elapsedTime = endTime - startTime;
        this.deductable = false;
        this.bill = null;
        this.cost = calculateCost();
    }

    // Overloaded constructor with Bridge Pattern client info.
    public Booking(int bookingId, int clientId, double clientRate, double licenceRate,
                   String licensePlate, String location, int startTime, int endTime,
                   ClientInfoImplementor clientInfo) {
        this(bookingId, clientId, clientRate, licenceRate, licensePlate, location, startTime, endTime);
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

    public int getBookingId() {
        return bookingId;
    }

    public int getClientId() {
        return clientId;
    }

    public double getClientRate() {
        return clientRate;
    }

    public double getLicenceRate() {
        return licenceRate;
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

    public Payment getBill() {
        return bill;
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
