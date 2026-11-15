package parking.model;

/**
 * Encapsulates a single floor of parking slots.
 */
public class ParkingFloor {
    private final int floorNumber;
    private final ParkingSlot[] slots;

    public ParkingFloor(int floorNumber, int slotCapacity) {
        if (floorNumber < 0) {
            throw new IllegalArgumentException("Floor number cannot be negative: " + floorNumber);
        }
        if (slotCapacity <= 0) {
            throw new IllegalArgumentException("Slot capacity must be positive: " + slotCapacity);
        }
        this.floorNumber = floorNumber;
        this.slots = new ParkingSlot[slotCapacity];
        for (int i = 0; i < slotCapacity; i++) {
            this.slots[i] = new ParkingSlot(floorNumber, i);
        }
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public int getCapacity() {
        return slots.length;
    }

    public ParkingSlot getSlot(int slotIndex) {
        if (slotIndex < 0 || slotIndex >= slots.length) {
            throw new IndexOutOfBoundsException("Slot index out of bounds: " + slotIndex);
        }
        return slots[slotIndex];
    }

    public int getOccupiedCount() {
        int count = 0;
        for (ParkingSlot s : slots) {
            if (s.isOccupied()) count++;
        }
        return count;
    }

    public int getAvailableCount() {
        return slots.length - getOccupiedCount();
    }
}

