package task03;

import java.util.Arrays;

/**
 * Task03: Design the Control Logic
 * Implements the standard ParkingFacility with a jagged array for floor slots.
 * Demonstrates:
 * - Scanning and allocating the first available slot.
 * - Releasing an allocated slot.
 * - Querying occupancy and lot full status.
 * - Producing the standard expected output.
 */
public class ParkingFacility {

    // Jagged array: boolean[floor][slot] where true = occupied, false = free
    private final boolean[][] slots;

    /**
     * Constructor initializing the parking facility with custom floor capacities.
     * @param floorCapacities array where each element specifies slot count for that floor.
     */
    public ParkingFacility(int[] floorCapacities) {
        this.slots = new boolean[floorCapacities.length][];
        for (int i = 0; i < floorCapacities.length; i++) {
            this.slots[i] = new boolean[floorCapacities[i]];
        }
    }

    /**
     * Allocates the first free slot scanning floors and slots in order.
     * @return int array [floor, slot] if allocated, or null if full.
     */
    public int[] allocateSlot() {
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (!slots[f][s]) {
                    slots[f][s] = true; // Mark as occupied
                    return new int[]{f, s}; // Immediate return upon finding slot
                }
            }
        }
        return null; // Entire facility is full
    }

    /**
     * Releases a previously occupied slot.
     * @param floor floor index
     * @param slot slot index
     * @return true if successfully released, false if invalid or already vacant
     */
    public boolean releaseSlot(int floor, int slot) {
        if (floor >= 0 && floor < slots.length && slot >= 0 && slot < slots[floor].length) {
            if (slots[floor][slot]) {
                slots[floor][slot] = false;
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a specific slot is occupied.
     */
    public boolean isOccupied(int floor, int slot) {
        if (floor >= 0 && floor < slots.length && slot >= 0 && slot < slots[floor].length) {
            return slots[floor][slot];
        }
        return false;
    }

    /**
     * Checks if all slots in the entire facility are occupied.
     */
    public boolean isFull() {
        for (int f = 0; f < slots.length; f++) {
            for (int s = 0; s < slots[f].length; s++) {
                if (!slots[f][s]) {
                    return false; // Found at least one vacant slot
                }
            }
        }
        return true;
    }

    /**
     * Calculates parking fee using vehicle-specific rates, started-hour billing,
     * pass exemption, and daily maximum caps.
     */
    public static int calculateFee(String vehicleType, int minutesParked, boolean hasValidPass) {
        // Business Rule 1: Valid pass gives free parking
        if (hasValidPass) {
            return 0;
        }

        // Business Rule 2: 0 minutes parked incurs 0 fee
        if (minutesParked <= 0) {
            return 0;
        }

        // Started-hour billing rule: round up to next full hour
        int billableHours = (minutesParked + 59) / 60;

        int baseRatePerHour;
        int dailyMaxCap;

        switch (vehicleType.toUpperCase()) {
            case "BIKE":
                baseRatePerHour = 10;
                dailyMaxCap = 100;
                break;
            case "CAR":
                baseRatePerHour = 20;
                dailyMaxCap = 250;
                break;
            case "ELECTRIC_CAR":
            case "EV":
                baseRatePerHour = 20; // 20/hr base + charging surcharge handled separately
                dailyMaxCap = 250;
                break;
            default:
                throw new IllegalArgumentException("Unknown vehicle type: " + vehicleType);
        }

        int calculatedFee = billableHours * baseRatePerHour;
        // Apply daily maximum cap if fee exceeds cap
        return Math.min(calculatedFee, dailyMaxCap);
    }

    /**
     * Standard main() method demonstrating required execution behavior.
     */
    public static void main(String[] args) {
        // Facility configuration: Floor 0 has 2 slots, Floor 1 has 1 slot (Total = 3 slots)
        ParkingFacility facility = new ParkingFacility(new int[]{2, 1});

        // 1. Allocate 1st slot -> [0, 0]
        int[] s1 = facility.allocateSlot();
        System.out.println(Arrays.toString(s1));

        // 2. Allocate 2nd slot -> [0, 1]
        int[] s2 = facility.allocateSlot();
        System.out.println(Arrays.toString(s2));

        // 3. Allocate 3rd slot -> [1, 0]
        int[] s3 = facility.allocateSlot();
        System.out.println(Arrays.toString(s3));

        // 4. Facility is now full -> returns null
        int[] s4 = facility.allocateSlot();
        System.out.println(s4 == null ? "null" : Arrays.toString(s4));

        // Release slot [0, 1]
        facility.releaseSlot(0, 1);

        // 5. Query status: slot[0][0] occupied? (true), slot[0][1] occupied? (false), facility isFull? (false)
        System.out.println(facility.isOccupied(0, 0) + " " + 
                           facility.isOccupied(0, 1) + " " + 
                           facility.isFull());

        // 6. Allocate again: scans and finds the freed slot [0, 1]
        int[] s5 = facility.allocateSlot();
        System.out.println(Arrays.toString(s5));
    }
}

