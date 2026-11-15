package parking.app;

import parking.model.Car;
import parking.model.Vehicle;
import parking.service.ParkingService;

/**
 * Task10: EncapsulationDemo
 * Demonstrates:
 * 1. Access modifiers across packages (private, protected, public, package-private).
 * 2. Input validation in constructors and setters (rejecting blanks, negatives, out-of-bounds).
 * 3. Wrapper classes, Autoboxing/Unboxing, and the Integer == Integer reference pitfall.
 */
public class EncapsulationDemo {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  Task 10: Encapsulation & Packages Demonstration");
        System.out.println("==================================================");

        // 1. Package-Enforced Encapsulation & Validation
        System.out.println("1. Validating Object Creation and Boundary Protection:");
        Car car = new Car("TN-09-XX-1234", "TAG-999", 4);
        System.out.println("   Successfully created: " + car.getRegistrationNumber() + 
                           " with " + car.getOccupantCount() + " occupants.");

        // Testing Setter Validation:
        try {
            System.out.println("   Attempting to set invalid occupant count (10):");
            car.setOccupantCount(10); // Violates max limit of 8
        } catch (IllegalArgumentException e) {
            System.out.println("   [Intercepted Expected Error]: " + e.getMessage());
        }

        // Testing Blank Registration Validation:
        try {
            System.out.println("   Attempting to create car with blank registration:");
            new Car("   ", "TAG-100", 2);
        } catch (IllegalArgumentException e) {
            System.out.println("   [Intercepted Expected Error]: " + e.getMessage());
        }
        System.out.println();

        // 2. Service-level Coordinate and Duration Validation
        System.out.println("2. Service-Level Defensive Validation:");
        ParkingService service = new ParkingService(3, 5); // 3 floors, 5 slots each
        try {
            System.out.println("   Attempting access to invalid Floor 5:");
            service.validateSlotCoordinates(5, 2);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("   [Intercepted Expected Error]: " + e.getMessage());
        }

        try {
            System.out.println("   Attempting billing with negative duration (-45 mins):");
            service.calculateBilledAmount(car, -45);
        } catch (IllegalArgumentException e) {
            System.out.println("   [Intercepted Expected Error]: " + e.getMessage());
        }

        int validFee = service.calculateBilledAmount(car, 90);
        System.out.println("   Valid Fee for 90 mins: Rs. " + validFee);
        System.out.println();

        // 3. Wrapper Classes, Autoboxing/Unboxing, and the Integer == Pitfall
        System.out.println("3. Wrapper Classes, Autoboxing & Reference Equality Pitfall:");
        // Autoboxing: primitive int automatically converted to wrapper Integer object
        Integer feeWrapper1 = validFee; 
        int unboxedFee = feeWrapper1;   // Unboxing: Integer automatically converted to primitive int
        System.out.println("   Autoboxed Integer: " + feeWrapper1 + ", Unboxed int: " + unboxedFee);

        // Pitfall Demonstration:
        // Java caches Integers between -128 and 127. Values outside this cache allocate distinct heap objects!
        Integer smallA = 100;
        Integer smallB = 100;
        System.out.println("   Comparing cached Integers (smallA = 100, smallB = 100):");
        System.out.println("     smallA == smallB (Cached object reference): " + (smallA == smallB));
        System.out.println("     smallA.equals(smallB) (Value comparison):   " + smallA.equals(smallB));

        Integer largeA = 1000;
        Integer largeB = 1000;
        System.out.println("   Comparing non-cached Integers (largeA = 1000, largeB = 1000):");
        System.out.println("     largeA == largeB (DANGER! Compares stack references): " + (largeA == largeB));
        System.out.println("     largeA.equals(largeB) (CORRECT: Compares numeric value): " + largeA.equals(largeB));

        System.out.println("==================================================");
        System.out.println("Task 10 Demonstration completed successfully.");
    }
}

