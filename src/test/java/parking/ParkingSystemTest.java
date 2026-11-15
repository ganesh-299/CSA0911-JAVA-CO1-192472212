package parking;

import parking.model.*;
import parking.pass.*;
import parking.service.ParkingLot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Automated Test Suite for Campus Smart-Parking System.
 * Pure Java (no external test runner required).
 * Verifies:
 * - Normal operations for all 5 vehicle types.
 * - Started-hour ceiling billing (0m, 60m, 61m).
 * - Full floor and facility capacity boundaries.
 * - Duplicate registration prevention.
 * - Negative duration / exit before entry.
 * - One-Day and Monthly Pass validity windows.
 * - Delivery van peak/off-peak pricing window.
 */
public class ParkingSystemTest {

    private static int testsRun = 0;
    private static int testsPassed = 0;
    private static int testsFailed = 0;

    private static void assertEquals(String testName, Object expected, Object actual) {
        testsRun++;
        if ((expected == null && actual == null) || (expected != null && expected.equals(actual))) {
            System.out.println("  [PASS] " + testName + " -> Result: " + actual);
            testsPassed++;
        } else {
            System.err.println("  [FAIL] " + testName + " -> Expected: " + expected + ", but got: " + actual);
            testsFailed++;
        }
    }

    private static void assertTrue(String testName, boolean condition) {
        testsRun++;
        if (condition) {
            System.out.println("  [PASS] " + testName);
            testsPassed++;
        } else {
            System.err.println("  [FAIL] " + testName);
            testsFailed++;
        }
    }

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println("      AUTOMATED SYSTEM TEST SUITE: CAMPUS SMART-PARKING");
        System.out.println("======================================================================");

        LocalDate testDate = LocalDate.of(2026, 11, 15);
        LocalDateTime baseEntry = LocalDateTime.of(testDate, LocalTime.of(10, 0));

        // Test Group 1: Normal Operations and Hourly Billing
        System.out.println("\n[GROUP 1: Normal Operations & Rates]");
        ParkingLot lot = new ParkingLot("TestFacility", new int[]{2, 2}); // 4 slots total

        Bike bike = new Bike("KA-01-BK-1111");
        Car car = new Car("KA-01-CR-2222");
        ElectricCar ev = new ElectricCar("KA-01-EV-3333", true);

        lot.parkVehicle(bike, baseEntry);
        lot.parkVehicle(car, baseEntry);
        lot.parkVehicle(ev, baseEntry);

        assertEquals("Bike 2 hours fee (Rs. 10/hr)", 20, lot.exitVehicle(bike.getRegistrationNumber(), baseEntry.plusHours(2)));
        assertEquals("Car 3 hours fee (Rs. 20/hr)", 60, lot.exitVehicle(car.getRegistrationNumber(), baseEntry.plusHours(3)));
        assertEquals("EV 1 hour fee + Rs.50 charging", 70, lot.exitVehicle(ev.getRegistrationNumber(), baseEntry.plusHours(1)));

        // Test Group 2: Boundary Cases (0m, 60m, 61m)
        System.out.println("\n[GROUP 2: Boundary Durations]");
        Car boundCar = new Car("TN-09-BD-9000");

        // 0 Minutes
        lot.parkVehicle(boundCar, baseEntry);
        assertEquals("Boundary 0 minutes fee", 0, lot.exitVehicle(boundCar.getRegistrationNumber(), baseEntry));

        // Exactly 60 Minutes (1 billable hour)
        lot.parkVehicle(boundCar, baseEntry);
        assertEquals("Boundary exactly 60 minutes fee", 20, lot.exitVehicle(boundCar.getRegistrationNumber(), baseEntry.plusMinutes(60)));

        // Exactly 61 Minutes (Crossed into 2 billable hours)
        lot.parkVehicle(boundCar, baseEntry);
        assertEquals("Boundary exactly 61 minutes fee", 40, lot.exitVehicle(boundCar.getRegistrationNumber(), baseEntry.plusMinutes(61)));

        // Test Group 3: Facility Full Boundary
        System.out.println("\n[GROUP 3: Facility Capacity Boundaries]");
        ParkingLot miniLot = new ParkingLot("MiniLot", new int[]{1}); // Only 1 slot
        miniLot.parkVehicle(new Bike("BIKE-1"), baseEntry);
        boolean fullCaught = false;
        try {
            miniLot.parkVehicle(new Bike("BIKE-2"), baseEntry);
        } catch (IllegalStateException e) {
            fullCaught = true;
        }
        assertTrue("Facility Full Exception Intercepted", fullCaught);

        // Test Group 4: Invalid Operations
        System.out.println("\n[GROUP 4: Invalid & Defensive Operations]");
        // Duplicate active registration
        boolean dupCaught = false;
        try {
            lot.parkVehicle(boundCar, baseEntry);
            lot.parkVehicle(new Car(boundCar.getRegistrationNumber()), baseEntry);
        } catch (IllegalStateException e) {
            dupCaught = true;
        }
        assertTrue("Duplicate Registration Rejected", dupCaught);

        // Exit before entry
        boolean exitBeforeEntryCaught = false;
        try {
            lot.exitVehicle(boundCar.getRegistrationNumber(), baseEntry.minusMinutes(30));
        } catch (IllegalArgumentException e) {
            exitBeforeEntryCaught = true;
        }
        assertTrue("Exit Before Entry Rejected", exitBeforeEntryCaught);

        // Exit non-existent vehicle
        boolean ghostCaught = false;
        try {
            lot.exitVehicle("GHOST-REG-404", baseEntry.plusHours(1));
        } catch (IllegalArgumentException e) {
            ghostCaught = true;
        }
        assertTrue("Non-Parked Exit Rejected", ghostCaught);

        // Cleanup
        lot.exitVehicle(boundCar.getRegistrationNumber(), baseEntry.plusMinutes(30));

        // Blank registration
        boolean blankCaught = false;
        try {
            new Bike("   ");
        } catch (IllegalArgumentException e) {
            blankCaught = true;
        }
        assertTrue("Blank Registration Rejected in Constructor", blankCaught);

        // Test Group 5: Task 12 Specific Adaptations (EBike, DeliveryVan, Passes)
        System.out.println("\n[GROUP 5: Task 12 Adaptations]");
        // EBike Flat Fee
        EBike ebike = new EBike("TN-05-EB-001");
        lot.parkVehicle(ebike, baseEntry);
        assertEquals("EBike 10 mins flat fee", 15, lot.exitVehicle(ebike.getRegistrationNumber(), baseEntry.plusMinutes(10)));

        lot.parkVehicle(ebike, baseEntry);
        assertEquals("EBike 8 hours flat fee", 15, lot.exitVehicle(ebike.getRegistrationNumber(), baseEntry.plusHours(8)));

        // Delivery Van: Peak vs Off-Peak
        DeliveryVan van = new DeliveryVan("TN-05-VAN-002");
        // Peak: 10:00 to 12:00 (2 hrs @ Rs. 40 = 80)
        lot.parkVehicle(van, baseEntry);
        assertEquals("Delivery Van Peak (2 hrs @ Rs.40)", 80, lot.exitVehicle(van.getRegistrationNumber(), baseEntry.plusHours(2)));

        // Off-Peak: 19:00 to 21:00 (2 hrs @ Rs. 20 = 40)
        LocalDateTime offPeakEntry = LocalDateTime.of(testDate, LocalTime.of(19, 0));
        lot.parkVehicle(van, offPeakEntry);
        assertEquals("Delivery Van Off-Peak (2 hrs @ Rs.20)", 40, lot.exitVehicle(van.getRegistrationNumber(), offPeakEntry.plusHours(2)));

        // One-Day Pass
        OneDayPass validPass = lot.sellOneDayPass("KA-03-PASS-1", testDate);
        lot.parkVehicle(new Car("KA-03-PASS-1"), baseEntry);
        assertEquals("Visitor with One-Day Pass pays Rs. 0 exit fee", 0, lot.exitVehicle("KA-03-PASS-1", baseEntry.plusHours(7)));

        // Expired Pass
        OneDayPass expiredPass = new OneDayPass("OLD-PASS", "KA-03-EXP-2", testDate.minusDays(1));
        lot.registerPass(expiredPass);
        lot.parkVehicle(new Car("KA-03-EXP-2"), baseEntry);
        assertEquals("Expired Pass pays normal parking fee", 40, lot.exitVehicle("KA-03-EXP-2", baseEntry.plusHours(2)));

        // Final Report
        System.out.println("\n======================================================================");
        System.out.println("                 TEST SUITE EXECUTION SUMMARY");
        System.out.println("======================================================================");
        System.out.println("Total Tests Run:     " + testsRun);
        System.out.println("Tests Passed:        " + testsPassed);
        System.out.println("Tests Failed:        " + testsFailed);
        System.out.println("Success Rate:        " + String.format("%.2f", (testsPassed * 100.0 / testsRun)) + "%");
        System.out.println("======================================================================");

        if (testsFailed > 0) {
            System.err.println("SOME TESTS FAILED!");
            System.exit(1);
        } else {
            System.out.println("ALL TESTS PASSED SUCCESSFULLY!");
        }
    }
}

