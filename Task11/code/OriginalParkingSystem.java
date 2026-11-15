package task11;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Objects;

/**
 * Task11: Original Array-Based Parking System Implementation & Comprehensive Tests.
 * Uses an array for active tickets (ParkingTicket[]) to evaluate linear search,
 * array-based removal, boundary limits, and error handling.
 */
public class OriginalParkingSystem {

    // --- Model Classes ---
    public enum VehicleType { BIKE, CAR, ELECTRIC_CAR, UNKNOWN }

    public static abstract class Vehicle {
        private final String registrationNumber;
        private final VehicleType type;

        public Vehicle(String registrationNumber, VehicleType type) {
            if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Registration number cannot be blank.");
            }
            if (type == null || type == VehicleType.UNKNOWN) {
                throw new IllegalArgumentException("Unknown or invalid vehicle type.");
            }
            this.registrationNumber = registrationNumber.trim().toUpperCase();
            this.type = type;
        }

        public String getRegistrationNumber() { return registrationNumber; }
        public VehicleType getType() { return type; }
        public abstract int ratePerHour();

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || !(o instanceof Vehicle v)) return false;
            return Objects.equals(registrationNumber, v.registrationNumber);
        }

        @Override
        public int hashCode() { return Objects.hash(registrationNumber); }
        @Override
        public String toString() { return type + " [" + registrationNumber + "]"; }
    }

    public static class Bike extends Vehicle {
        public Bike(String regNo) { super(regNo, VehicleType.BIKE); }
        @Override public int ratePerHour() { return 10; }
    }

    public static class Car extends Vehicle {
        public Car(String regNo) { super(regNo, VehicleType.CAR); }
        @Override public int ratePerHour() { return 20; }
    }

    public static class ElectricCar extends Car {
        private final boolean chargingRequired;
        public ElectricCar(String regNo, boolean chargingRequired) {
            super(regNo);
            this.chargingRequired = chargingRequired;
        }
        public boolean isChargingRequired() { return chargingRequired; }
        @Override public int ratePerHour() { return 20; }
        public int chargingFee() { return chargingRequired ? 50 : 0; }
    }

    public static class ParkingTicket {
        private final String ticketId;
        private final Vehicle vehicle;
        private final int floor;
        private final int slot;
        private final LocalDateTime entryTime;

        public ParkingTicket(String ticketId, Vehicle vehicle, int floor, int slot, LocalDateTime entryTime) {
            this.ticketId = ticketId;
            this.vehicle = vehicle;
            this.floor = floor;
            this.slot = slot;
            this.entryTime = entryTime;
        }

        public String getTicketId() { return ticketId; }
        public Vehicle getVehicle() { return vehicle; }
        public int getFloor() { return floor; }
        public int getSlot() { return slot; }
        public LocalDateTime getEntryTime() { return entryTime; }

        @Override
        public String toString() {
            return "Ticket[" + ticketId + ", " + vehicle.getRegistrationNumber() + 
                   ", F" + floor + "-S" + slot + "]";
        }
    }

    // --- Parking Facility Management Service (Array-Based) ---
    public static class Facility {
        private final boolean[][] slots; // Jagged slots
        private final ParkingTicket[] activeTickets; // Task 11 requirement: array-based ticket storage
        private int ticketCount;
        private int ticketSequence = 1000;
        private int totalCollections = 0;

        public Facility(int[] capacities, int maxActiveTickets) {
            this.slots = new boolean[capacities.length][];
            for (int i = 0; i < capacities.length; i++) {
                this.slots[i] = new boolean[capacities[i]];
            }
            this.activeTickets = new ParkingTicket[maxActiveTickets];
            this.ticketCount = 0;
        }

        // Park Vehicle
        public ParkingTicket parkVehicle(Vehicle vehicle, LocalDateTime entryTime) {
            if (vehicle == null) throw new IllegalArgumentException("Vehicle cannot be null.");

            // Check Duplicate Active Registration
            for (int i = 0; i < activeTickets.length; i++) {
                if (activeTickets[i] != null && 
                    activeTickets[i].getVehicle().getRegistrationNumber().equals(vehicle.getRegistrationNumber())) {
                    throw new IllegalStateException("Duplicate active registration: " + vehicle.getRegistrationNumber() + " is already parked.");
                }
            }

            // Find first available slot (linear scan)
            int allocatedFloor = -1;
            int allocatedSlot = -1;
            for (int f = 0; f < slots.length; f++) {
                for (int s = 0; s < slots[f].length; s++) {
                    if (!slots[f][s]) {
                        allocatedFloor = f;
                        allocatedSlot = s;
                        slots[f][s] = true;
                        break;
                    }
                }
                if (allocatedFloor != -1) break;
            }

            if (allocatedFloor == -1) {
                throw new IllegalStateException("Parking full: No slots available.");
            }

            // Store in active tickets array (O(n) search for null slot)
            int targetIndex = -1;
            for (int i = 0; i < activeTickets.length; i++) {
                if (activeTickets[i] == null) {
                    targetIndex = i;
                    break;
                }
            }

            if (targetIndex == -1) {
                // Rollback slot occupancy if tickets array is exhausted
                slots[allocatedFloor][allocatedSlot] = false;
                throw new IllegalStateException("Active ticket registry is full.");
            }

            String ticketId = "TKT-" + (++ticketSequence);
            ParkingTicket ticket = new ParkingTicket(ticketId, vehicle, allocatedFloor, allocatedSlot, entryTime);
            activeTickets[targetIndex] = ticket;
            ticketCount++;
            return ticket;
        }

        // Exit Vehicle
        public int exitVehicle(String regNo, LocalDateTime exitTime, boolean hasMonthlyPass) {
            if (regNo == null || regNo.trim().isEmpty()) {
                throw new IllegalArgumentException("Registration cannot be blank.");
            }
            String normalizedReg = regNo.trim().toUpperCase();

            // Linear search in array: O(n)
            int foundIndex = -1;
            ParkingTicket ticket = null;
            for (int i = 0; i < activeTickets.length; i++) {
                if (activeTickets[i] != null && 
                    activeTickets[i].getVehicle().getRegistrationNumber().equals(normalizedReg)) {
                    foundIndex = i;
                    ticket = activeTickets[i];
                    break;
                }
            }

            if (foundIndex == -1) {
                throw new IllegalArgumentException("Exit failed: Vehicle " + normalizedReg + " not found in active parking.");
            }

            if (exitTime.isBefore(ticket.getEntryTime())) {
                throw new IllegalArgumentException("Exit time cannot be earlier than entry time.");
            }

            long durationMinutes = java.time.Duration.between(ticket.getEntryTime(), exitTime).toMinutes();

            // Fee calculation
            int fee = 0;
            if (hasMonthlyPass) {
                fee = 0; // Pass holder parks free
            } else if (durationMinutes == 0) {
                fee = 0; // 0 minutes turnaround
            } else {
                long billableHours = (durationMinutes + 59) / 60;
                fee = (int) (billableHours * ticket.getVehicle().ratePerHour());
                if (ticket.getVehicle() instanceof ElectricCar ev) {
                    fee += ev.chargingFee();
                }
            }

            // Release physical slot
            slots[ticket.getFloor()][ticket.getSlot()] = false;

            // Remove from array: O(1) assignment after location
            activeTickets[foundIndex] = null;
            ticketCount--;
            totalCollections += fee;

            return fee;
        }

        public int getActiveCount() { return ticketCount; }
        public int getTotalCollections() { return totalCollections; }
        public int getAvailableSlots() {
            int count = 0;
            for (boolean[] floor : slots) {
                for (boolean occ : floor) if (!occ) count++;
            }
            return count;
        }
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  Task 11: System Implementation & Test Defense");
        System.out.println("==================================================");

        // Facility with 2 floors: Floor 0 has 2 slots, Floor 1 has 1 slot (Total 3 slots)
        Facility facility = new Facility(new int[]{2, 1}, 10);
        LocalDateTime baseTime = LocalDateTime.of(2026, 11, 15, 10, 0);

        // 1. Normal Operations: Bike, Car, EV
        System.out.println("\n--- 1. Normal Operations ---");
        Bike bike = new Bike("TN-01-BK-0001");
        Car car = new Car("TN-01-CR-0002");
        ElectricCar ev = new ElectricCar("TN-01-EV-0003", true);

        ParkingTicket t1 = facility.parkVehicle(bike, baseTime);
        ParkingTicket t2 = facility.parkVehicle(car, baseTime);
        ParkingTicket t3 = facility.parkVehicle(ev, baseTime);
        System.out.println("Parked Bike: " + t1);
        System.out.println("Parked Car:  " + t2);
        System.out.println("Parked EV:   " + t3);
        System.out.println("Available slots remaining: " + facility.getAvailableSlots());

        // 2. Boundary Cases
        System.out.println("\n--- 2. Boundary Cases ---");
        // A. Last free slot & Full Facility
        try {
            System.out.print("Testing parking when lot is full: ");
            facility.parkVehicle(new Car("TN-01-CR-FULL"), baseTime);
        } catch (IllegalStateException e) {
            System.out.println("[PASS - Intercepted]: " + e.getMessage());
        }

        // Exit bike to create a free space
        int bikeFee = facility.exitVehicle("TN-01-BK-0001", baseTime.plusMinutes(120), false); // 120 mins = 2 hrs * 10 = 20
        System.out.println("Bike Exited (120 mins). Fee Billed: Rs. " + bikeFee + " (Expected: Rs. 20)");

        // B. Zero-minute duration
        ParkingTicket tZero = facility.parkVehicle(new Car("TN-01-CR-ZERO"), baseTime);
        int feeZero = facility.exitVehicle("TN-01-CR-ZERO", baseTime, false);
        System.out.println("Zero-minute stay exit fee: Rs. " + feeZero + " (Expected: Rs. 0)");

        // C. Exactly 60 minutes (Boundary)
        ParkingTicket t60 = facility.parkVehicle(new Car("TN-01-CR-60M"), baseTime);
        int fee60 = facility.exitVehicle("TN-01-CR-60M", baseTime.plusMinutes(60), false);
        System.out.println("Exactly 60-min stay fee: Rs. " + fee60 + " (Expected: Rs. 20)");

        // D. Exactly 61 minutes (Crossed Boundary)
        ParkingTicket t61 = facility.parkVehicle(new Car("TN-01-CR-61M"), baseTime);
        int fee61 = facility.exitVehicle("TN-01-CR-61M", baseTime.plusMinutes(61), false);
        System.out.println("Exactly 61-min stay fee: Rs. " + fee61 + " (Expected: Rs. 40)");

        // 3. Invalid Cases
        System.out.println("\n--- 3. Invalid Cases ---");
        // Unknown vehicle type / blank reg
        try {
            System.out.print("Testing blank registration: ");
            new Car("   ");
        } catch (IllegalArgumentException e) {
            System.out.println("[PASS]: " + e.getMessage());
        }

        // Duplicate active registration
        try {
            System.out.print("Testing duplicate active registration: ");
            facility.parkVehicle(car, baseTime); // car is already parked (t2)
        } catch (IllegalStateException e) {
            System.out.println("[PASS]: " + e.getMessage());
        }

        // Negative duration / exit before entry
        try {
            System.out.print("Testing exit time before entry time: ");
            facility.exitVehicle("TN-01-CR-0002", baseTime.minusMinutes(30), false);
        } catch (IllegalArgumentException e) {
            System.out.println("[PASS]: " + e.getMessage());
        }

        // Exit of unknown registration
        try {
            System.out.print("Testing exit of non-parked vehicle: ");
            facility.exitVehicle("TN-99-GHOST-9999", baseTime.plusHours(1), false);
        } catch (IllegalArgumentException e) {
            System.out.println("[PASS]: " + e.getMessage());
        }

        // Clean up currently parked vehicles
        facility.exitVehicle("TN-01-CR-0002", baseTime.plusHours(1), false);
        facility.exitVehicle("TN-01-EV-0003", baseTime.plusHours(1), false);

        // 4. Daily Collection Scenario
        System.out.println("\n--- 4. Daily Collection Scenario ---");
        Facility dailyFacility = new Facility(new int[]{5, 5}, 20);

        // Vehicle A: Bike, 120 mins (2 hrs) -> 2 * Rs. 10 = Rs. 20
        dailyFacility.parkVehicle(new Bike("TN-10-A-0001"), baseTime);
        int fA = dailyFacility.exitVehicle("TN-10-A-0001", baseTime.plusMinutes(120), false);

        // Vehicle B: Car, 180 mins (3 hrs) -> 3 * Rs. 20 = Rs. 60
        dailyFacility.parkVehicle(new Car("TN-10-B-0002"), baseTime);
        int fB = dailyFacility.exitVehicle("TN-10-B-0002", baseTime.plusMinutes(180), false);

        // Vehicle C: EV (with charging), 60 mins (1 hr) -> 1 * Rs. 20 + Rs. 50 = Rs. 70
        dailyFacility.parkVehicle(new ElectricCar("TN-10-C-0003", true), baseTime);
        int fC = dailyFacility.exitVehicle("TN-10-C-0003", baseTime.plusMinutes(60), false);

        int handCalculationTotal = 20 + 60 + 70; // Rs. 150
        int actualTotal = dailyFacility.getTotalCollections();

        System.out.println("Vehicle A (Bike, 2 hrs):             Rs. " + fA + " (Hand-calc: Rs. 20)");
        System.out.println("Vehicle B (Car, 3 hrs):              Rs. " + fB + " (Hand-calc: Rs. 60)");
        System.out.println("Vehicle C (EV, 1 hr + charging):     Rs. " + fC + " (Hand-calc: Rs. 70)");
        System.out.println("--------------------------------------------------");
        System.out.println("Actual Daily Facility Collection:    Rs. " + actualTotal);
        System.out.println("Hand-Calculated Daily Total:         Rs. " + handCalculationTotal);
        System.out.println("Discrepancy:                         Rs. " + (actualTotal - handCalculationTotal) + " (MATCH: " + (actualTotal == handCalculationTotal) + ")");

        System.out.println("==================================================");
        System.out.println("Task 11 Verification completed successfully.");
    }
}

