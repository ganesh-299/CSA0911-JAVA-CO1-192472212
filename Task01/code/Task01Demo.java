package task01;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Task01Demo - Small runnable model demonstrating initial candidate classes
 * for the Campus Smart-Parking System.
 */
public class Task01Demo {

    // Simple enum for vehicle types
    public enum VehicleType {
        BIKE, CAR, ELECTRIC_CAR
    }

    // Candidate Class 1: Vehicle
    public static class Vehicle {
        private final String registrationNumber;
        private final VehicleType type;

        public Vehicle(String registrationNumber, VehicleType type) {
            this.registrationNumber = registrationNumber;
            this.type = type;
        }

        public String getRegistrationNumber() {
            return registrationNumber;
        }

        public VehicleType getType() {
            return type;
        }

        @Override
        public String toString() {
            return type + " [" + registrationNumber + "]";
        }
    }

    // Candidate Class 2: ParkingSlot
    public static class ParkingSlot {
        private final int floorNumber;
        private final int slotNumber;
        private boolean occupied;
        private Vehicle currentVehicle;

        public ParkingSlot(int floorNumber, int slotNumber) {
            this.floorNumber = floorNumber;
            this.slotNumber = slotNumber;
            this.occupied = false;
            this.currentVehicle = null;
        }

        public boolean isOccupied() {
            return occupied;
        }

        public void assignVehicle(Vehicle vehicle) {
            this.currentVehicle = vehicle;
            this.occupied = true;
        }

        public void release() {
            this.currentVehicle = null;
            this.occupied = false;
        }

        public int getFloorNumber() {
            return floorNumber;
        }

        public int getSlotNumber() {
            return slotNumber;
        }

        @Override
        public String toString() {
            return "Floor " + floorNumber + ", Slot " + slotNumber + 
                   (occupied ? " (Occupied by " + currentVehicle + ")" : " (Available)");
        }
    }

    // Candidate Class 3: ParkingTicket
    public static class ParkingTicket {
        private static int counter = 1000;
        private final String ticketId;
        private final Vehicle vehicle;
        private final ParkingSlot slot;
        private final LocalDateTime entryTime;

        public ParkingTicket(Vehicle vehicle, ParkingSlot slot) {
            this.ticketId = "TKT-" + (++counter);
            this.vehicle = vehicle;
            this.slot = slot;
            this.entryTime = LocalDateTime.now();
        }

        public String getTicketId() {
            return ticketId;
        }

        public Vehicle getVehicle() {
            return vehicle;
        }

        public ParkingSlot getSlot() {
            return slot;
        }

        public LocalDateTime getEntryTime() {
            return entryTime;
        }

        @Override
        public String toString() {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return "Ticket{" +
                    "id='" + ticketId + '\'' +
                    ", vehicle=" + vehicle +
                    ", location=F" + slot.getFloorNumber() + "-S" + slot.getSlotNumber() +
                    ", entry=" + entryTime.format(fmt) +
                    '}';
        }
    }

    // Candidate Class 4: FeeCalculator
    public static class FeeCalculator {
        public static int calculateFee(VehicleType type, long hours, boolean hasPass) {
            if (hasPass) {
                return 0; // Monthly pass holders park free
            }
            int ratePerHour = switch (type) {
                case BIKE -> 10;
                case CAR -> 20;
                case ELECTRIC_CAR -> 25; // 20 base + 5 EV charging
            };
            return (int) (ratePerHour * Math.max(1, hours));
        }
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  Task 01: Candidate Classes Demonstration");
        System.out.println("==================================================");

        // 1. Create parking slot
        ParkingSlot slot = new ParkingSlot(0, 1);
        System.out.println("Initial slot status: " + slot);

        // 2. Create vehicle
        Vehicle car = new Vehicle("TN-01-AB-1234", VehicleType.CAR);
        System.out.println("Created vehicle: " + car);

        // 3. Park vehicle into slot and issue ticket
        slot.assignVehicle(car);
        ParkingTicket ticket = new ParkingTicket(car, slot);
        System.out.println("Slot status after parking: " + slot);
        System.out.println("Issued ticket: " + ticket);

        // 4. Calculate sample fee
        int feeNoPass = FeeCalculator.calculateFee(car.getType(), 3, false);
        int feeWithPass = FeeCalculator.calculateFee(car.getType(), 3, true);
        System.out.println("Calculated fee for 3 hours (Visitor): Rs. " + feeNoPass);
        System.out.println("Calculated fee for 3 hours (Pass Holder): Rs. " + feeWithPass);

        // 5. Release slot on exit
        slot.release();
        System.out.println("Slot status after vehicle exit: " + slot);
        System.out.println("==================================================");
        System.out.println("Task 01 Demonstration completed successfully.");
    }
}

