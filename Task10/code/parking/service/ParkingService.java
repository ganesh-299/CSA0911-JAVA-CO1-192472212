package parking.service;

import parking.model.Vehicle;
import java.time.LocalDate;

/**
 * Task10: ParkingService demonstrating package boundaries and validation.
 */
public class ParkingService {
    private final int totalFloors;
    private final int slotsPerFloor;

    public ParkingService(int floors, int slotsPerFloor) {
        if (floors <= 0 || slotsPerFloor <= 0) {
            throw new IllegalArgumentException("Floors and slots must be positive integers.");
        }
        this.totalFloors = floors;
        this.slotsPerFloor = slotsPerFloor;
    }

    public void validateSlotCoordinates(int floor, int slot) {
        if (floor < 0 || floor >= totalFloors) {
            throw new IndexOutOfBoundsException("Invalid floor: " + floor + " (Allowed: 0 to " + (totalFloors - 1) + ")");
        }
        if (slot < 0 || slot >= slotsPerFloor) {
            throw new IndexOutOfBoundsException("Invalid slot: " + slot + " (Allowed: 0 to " + (slotsPerFloor - 1) + ")");
        }
    }

    public int calculateBilledAmount(Vehicle vehicle, int durationMinutes) {
        if (durationMinutes < 0) {
            throw new IllegalArgumentException("Parking duration cannot be negative: " + durationMinutes);
        }
        if (durationMinutes == 0) {
            return 0;
        }
        int billableHours = (durationMinutes + 59) / 60;
        return billableHours * vehicle.ratePerHour();
    }
}

