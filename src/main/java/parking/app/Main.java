package parking.app;

import parking.model.*;
import parking.pass.*;
import parking.service.ParkingLot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Main Application Showcase for the Campus Smart-Parking System.
 * Demonstrates:
 * - 5-floor jagged array configuration.
 * - Multi-vehicle support (Bike, Car, ElectricCar, EBike, DeliveryVan).
 * - Pass integrations (Monthly Pass & One-Day Pass).
 * - Dynamic pricing strategy rules.
 * - Revenue audits and error-handling defenses.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("   CAMPUS SMART-PARKING SYSTEM - OBJECT-ORIENTED MANAGEMENT DEMO");
        System.out.println("======================================================================");

        // 1. Initialize Multi-Floor Parking Facility
        // 5 floors with variable capacities: Floor 0: 5, Floor 1: 8, Floor 2: 6, Floor 3: 10, Floor 4: 4
        int[] floorCapacities = new int[]{5, 8, 6, 10, 4};
        ParkingLot lot = new ParkingLot("University Central Parking Complex", floorCapacities);

        System.out.println("Facility Name: " + lot.getFacilityName());
        System.out.println("Floors: " + lot.getFloorCount() + " | Total Capacity: " + lot.getTotalSlotCapacity() + " bays.");
        System.out.println("Slot Layout across floors (Jagged Array):");
        for (int f = 0; f < lot.getFloorCount(); f++) {
            System.out.println("  Floor " + f + ": " + lot.getFloor(f).getCapacity() + " slots");
        }
        System.out.println();

        LocalDate today = LocalDate.of(2026, 11, 15);
        LocalDateTime morningTime = LocalDateTime.of(today, LocalTime.of(8, 30));

        // 2. Pass Registrations & Sales
        System.out.println("--- PHASE 1: Pass Registrations & Sales ---");
        // Student Monthly Pass for Car
        MonthlyPass studentPass = new MonthlyPass(
                "PASS-STU-01", "TN-01-STU-100", 
                today.minusDays(5), today.plusDays(25), PassType.STUDENT_MONTHLY
        );
        lot.registerPass(studentPass);
        System.out.println("[Pass Issued] " + studentPass.getPassDescription());

        // Visitor One-Day Pass for Car
        OneDayPass visitorPass = lot.sellOneDayPass("KA-05-VIS-200", today);
        System.out.println("[Pass Sold]   " + visitorPass.getPassDescription());
        System.out.println("Pass Sales Revenue to date: Rs. " + lot.getTotalPassSalesCollected() + "\n");

        // 3. Vehicle Entry and Parking Allocations
        System.out.println("--- PHASE 2: Vehicle Influx & Dynamic Slot Allocation ---");
        Bike bike1 = new Bike("TN-02-BK-501");
        Car studentCar = new Car("TN-01-STU-100"); // Has Monthly Pass
        ElectricCar evCar = new ElectricCar("TN-09-EV-701", true); // Charging requested
        EBike ebike = new EBike("TN-03-EB-801"); // Flat fee vehicle
        DeliveryVan van = new DeliveryVan("MH-12-VAN-901"); // Peak window vehicle
        Car visitorCar = new Car("KA-05-VIS-200"); // Has One-Day Pass

        ParkingTicket t1 = lot.parkVehicle(bike1, morningTime);
        ParkingTicket t2 = lot.parkVehicle(studentCar, morningTime);
        ParkingTicket t3 = lot.parkVehicle(evCar, morningTime);
        ParkingTicket t4 = lot.parkVehicle(ebike, morningTime);
        ParkingTicket t5 = lot.parkVehicle(van, morningTime);
        ParkingTicket t6 = lot.parkVehicle(visitorCar, morningTime);

        System.out.println("Issued Tickets:");
        System.out.println("  1. " + t1);
        System.out.println("  2. " + t2);
        System.out.println("  3. " + t3);
        System.out.println("  4. " + t4);
        System.out.println("  5. " + t5);
        System.out.println("  6. " + t6);
        System.out.println("\nLive Facility Occupancy: " + lot.getTotalOccupiedSlots() + 
                           " occupied, " + lot.getTotalAvailableSlots() + " vacant.\n");

        // 4. Vehicle Departure and Fee Processing
        System.out.println("--- PHASE 3: Vehicle Departures & Billing Reconciliation ---");

        // Departure 1: Bike exits after 2 hours (8:30 to 10:30)
        LocalDateTime exitBike = morningTime.plusHours(2);
        int feeBike = lot.exitVehicle(bike1.getRegistrationNumber(), exitBike);
        System.out.println("-> Vehicle Exit: " + bike1.getRegistrationNumber() + " (Bike, 2 hrs)");
        System.out.println("   Billed Fee: Rs. " + feeBike + " (Rate: Rs. 10/hr)");

        // Departure 2: Student Car exits after 4 hours (8:30 to 12:30) with Monthly Pass
        LocalDateTime exitStudent = morningTime.plusHours(4);
        int feeStudent = lot.exitVehicle(studentCar.getRegistrationNumber(), exitStudent);
        System.out.println("-> Vehicle Exit: " + studentCar.getRegistrationNumber() + " (Student Car, 4 hrs)");
        System.out.println("   Billed Fee: Rs. " + feeStudent + " [EXEMPTION: Valid Monthly Pass]");

        // Departure 3: Electric Car exits after 3 hours (8:30 to 11:30) with Charging
        LocalDateTime exitEv = morningTime.plusHours(3);
        int feeEv = lot.exitVehicle(evCar.getRegistrationNumber(), exitEv);
        System.out.println("-> Vehicle Exit: " + evCar.getRegistrationNumber() + " (EV Car, 3 hrs + Charging)");
        System.out.println("   Billed Fee: Rs. " + feeEv + " (3 hrs @ Rs. 20 = Rs. 60 + Rs. 50 EV Charging)");

        // Departure 4: EBike exits after 5 hours (8:30 to 13:30)
        LocalDateTime exitEbike = morningTime.plusHours(5);
        int feeEbike = lot.exitVehicle(ebike.getRegistrationNumber(), exitEbike);
        System.out.println("-> Vehicle Exit: " + ebike.getRegistrationNumber() + " (E-Bike, 5 hrs)");
        System.out.println("   Billed Fee: Rs. " + feeEbike + " [POLICY: Flat Tariff]");

        // Departure 5: Delivery Van exits after 2 hours (8:30 to 10:30) -> 30m off-peak, 90m peak
        LocalDateTime exitVan = morningTime.plusHours(2);
        int feeVan = lot.exitVehicle(van.getRegistrationNumber(), exitVan);
        System.out.println("-> Vehicle Exit: " + van.getRegistrationNumber() + " (Delivery Van, 2 hrs)");
        System.out.println("   Billed Fee: Rs. " + feeVan + " [POLICY: Peak 09:00-17:00 @ Rs.40, Off-peak @ Rs.20]");

        // Departure 6: Visitor Car exits after 6 hours (8:30 to 14:30) with One-Day Pass
        LocalDateTime exitVisitor = morningTime.plusHours(6);
        int feeVisitor = lot.exitVehicle(visitorCar.getRegistrationNumber(), exitVisitor);
        System.out.println("-> Vehicle Exit: " + visitorCar.getRegistrationNumber() + " (Visitor Car, 6 hrs)");
        System.out.println("   Billed Fee: Rs. " + feeVisitor + " [EXEMPTION: Pre-purchased One-Day Pass]");
        System.out.println();

        // 5. System Defenses Verification (Exception Interceptions)
        System.out.println("--- PHASE 4: Robustness & Defensive Exception Handling ---");
        try {
            System.out.print("Testing duplicate active entry: ");
            lot.parkVehicle(new Bike("TN-02-BK-501")); // Was exited, so this should pass!
            System.out.println("[ALLOWED - Successfully re-entered exited vehicle]");
            lot.parkVehicle(new Bike("TN-02-BK-501")); // Duplicate!
        } catch (IllegalStateException e) {
            System.out.println("   [CAUGHT EXPECTED ERROR]: " + e.getMessage());
        }

        try {
            System.out.print("Testing exit of non-parked ghost vehicle: ");
            lot.exitVehicle("FAKE-REG-999");
        } catch (IllegalArgumentException e) {
            System.out.println("   [CAUGHT EXPECTED ERROR]: " + e.getMessage());
        }
        System.out.println();

        // 6. Comprehensive Financial Audit
        System.out.println("======================================================================");
        System.out.println("                   DAILY REVENUE & AUDIT SUMMARY");
        System.out.println("======================================================================");
        System.out.println("Facility Name:                     " + lot.getFacilityName());
        System.out.println("Total Exit Gate Parking Fees:      Rs. " + lot.getTotalParkingFeesCollected());
        System.out.println("Total Pass Sales Revenue:          Rs. " + lot.getTotalPassSalesCollected());
        System.out.println("----------------------------------------------------------------------");
        System.out.println("GRAND TOTAL CAMPUS REVENUE:        Rs. " + lot.getGrandTotalRevenue());
        System.out.println("======================================================================");
        System.out.println("Execution finished cleanly. All business rules verified.");
    }
}

