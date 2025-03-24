package system;

public class Sensor {
    private Parkingspace parkingSpace;
    private boolean activity;
    private String state;

    public Sensor(Parkingspace parkingSpace) {
        this.parkingSpace = parkingSpace;
        this.activity = false;
        this.state = "Idle";
    }

    public void printNavigationInfo() {
        System.out.println("Navigation info for Parking Space " + parkingSpace.getId());
    }

    public void update(int time) {
        // Simulate an update that checks occupancy at the given time.
        String occupant = checkOccupied(time);
        if (occupant != null) {
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
}
