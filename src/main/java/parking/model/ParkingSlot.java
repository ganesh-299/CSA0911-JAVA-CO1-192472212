package parking.model;

/**
 * Represents an individual physical parking space.
 */
public class ParkingSlot {
    private final int floorNumber;
    private final int slotNumber;
    private boolean occupied;
    private Vehicle currentVehicle;

    public ParkingSlot(int floorNumber, int slotNumber) {
        if (floorNumber < 0 || slotNumber < 0) {
            throw new IllegalArgumentException("Floor and slot numbers must be non-negative.");
        }
        this.floorNumber = floorNumber;
        this.slotNumber = slotNumber;
        this.occupied = false;
        this.currentVehicle = null;
    }

    public int getFloorNumber() { return floorNumber; }
    public int getSlotNumber() { return slotNumber; }
    public boolean isOccupied() { return occupied; }
    public Vehicle getCurrentVehicle() { return currentVehicle; }

    public synchronized void occupy(Vehicle vehicle) {
        if (this.occupied) {
            throw new IllegalStateException("Slot F" + floorNumber + "-S" + slotNumber + " is already occupied.");
        }
        this.currentVehicle = vehicle;
        this.occupied = true;
    }

    public synchronized void vacate() {
        this.currentVehicle = null;
        this.occupied = false;
    }

    @Override
    public String toString() {
        return "Slot[F" + floorNumber + "-S" + slotNumber + ", " + 
               (occupied ? "Occupied by " + currentVehicle : "Available") + "]";
    }
}

