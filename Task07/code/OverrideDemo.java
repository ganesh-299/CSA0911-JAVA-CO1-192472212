package task07;

import java.util.Objects;

/**
 * Task07: Override and Specialise Behaviour
 * Demonstrates:
 * - Overriding ratePerHour() in each subclass with @Override.
 * - Rules of method overriding.
 * - 'final' variables, 'final' methods, and 'final' classes.
 * - Consistent overriding of toString(), equals(), and hashCode() based on normalized registration number.
 * - Comparison: Object-oriented dynamic dispatch vs. Procedural switch statement.
 */
public class OverrideDemo {

    // Final Class: Cannot be extended/subclassed
    public static final class SystemConfig {
        public static final int MAX_PARKING_HOURS = 72; // final variable: constant, cannot be reassigned
        private SystemConfig() {} // Prevents instantiation
    }

    // Base Class
    public static class Vehicle {
        // final instance variable: must be initialized in constructor, cannot be reassigned
        protected final String registrationNumber;

        public Vehicle(String registrationNumber) {
            if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Registration number cannot be null or empty.");
            }
            // Normalizing to upper-case and trimming for consistent identity
            this.registrationNumber = registrationNumber.trim().toUpperCase();
        }

        // final method: subclasses cannot override or tamper with access to registration
        public final String getRegistrationNumber() {
            return registrationNumber;
        }

        // Overridable method
        public int ratePerHour() {
            return 15;
        }

        // Final template method: encapsulates billing formula, preventing subclasses from breaking math
        public final int calculateFee(int hours) {
            int billable = Math.max(1, hours);
            return ratePerHour() * billable;
        }

        // 1. Overriding toString()
        @Override
        public String toString() {
            return "Vehicle[" + registrationNumber + ", Rate=Rs." + ratePerHour() + "/hr]";
        }

        // 2. Overriding equals()
        // Identity Rule: Two vehicles with the same normalized registration number are logically equal
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true; // Reference equality optimization
            if (obj == null || !(obj instanceof Vehicle other)) return false; // Type safety check
            return Objects.equals(this.registrationNumber, other.registrationNumber);
        }

        // 3. Overriding hashCode()
        // Contract: If a.equals(b) == true, then a.hashCode() MUST equal b.hashCode()
        @Override
        public int hashCode() {
            return Objects.hash(registrationNumber);
        }
    }

    // Subclass 1: Bike
    public static class Bike extends Vehicle {
        public Bike(String registrationNumber) {
            super(registrationNumber);
        }

        // Method Overriding: Same method signature, same return type, not reducing visibility
        @Override
        public int ratePerHour() {
            return 10;
        }

        @Override
        public String toString() {
            return "Bike[" + registrationNumber + ", Rate=Rs." + ratePerHour() + "/hr]";
        }
    }

    // Subclass 2: Car
    public static class Car extends Vehicle {
        public Car(String registrationNumber) {
            super(registrationNumber);
        }

        @Override
        public int ratePerHour() {
            return 20;
        }

        @Override
        public String toString() {
            return "Car[" + registrationNumber + ", Rate=Rs." + ratePerHour() + "/hr]";
        }
    }

    // Subclass 3: ElectricCar
    public static class ElectricCar extends Car {
        private final boolean chargingRequired;

        public ElectricCar(String registrationNumber, boolean chargingRequired) {
            super(registrationNumber);
            this.chargingRequired = chargingRequired;
        }

        @Override
        public int ratePerHour() {
            // Reuses superclass rate of 20 and adds charging surcharge
            return chargingRequired ? (super.ratePerHour() + 5) : super.ratePerHour();
        }

        @Override
        public String toString() {
            return "ElectricCar[" + registrationNumber + ", Rate=Rs." + ratePerHour() + 
                   "/hr, Charging=" + chargingRequired + "]";
        }
    }

    // Procedural Alternative: Switch-based rate calculation
    public static int calculateRateViaSwitch(String vehicleType) {
        return switch (vehicleType.toUpperCase()) {
            case "BIKE" -> 10;
            case "CAR" -> 20;
            case "ELECTRIC_CAR" -> 25;
            default -> 15;
        };
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  Task 07: Overriding & Specialisation Demo");
        System.out.println("==================================================");

        // A. Demonstrating Overridden ratePerHour() and toString()
        Vehicle v1 = new Bike("TN-01-BK-1111");
        Vehicle v2 = new Car("TN-01-CR-2222");
        Vehicle v3 = new ElectricCar("TN-01-EV-3333", true);

        System.out.println("Polymorphic Display (Overridden toString & rate):");
        System.out.println("  " + v1 + " -> 3 hrs Fee: Rs. " + v1.calculateFee(3));
        System.out.println("  " + v2 + " -> 3 hrs Fee: Rs. " + v2.calculateFee(3));
        System.out.println("  " + v3 + " -> 3 hrs Fee: Rs. " + v3.calculateFee(3));
        System.out.println();

        // B. Demonstrating equals() and hashCode() Contract with Normalized Identity
        Vehicle carAlpha = new Car("tn-09-ab-1234"); // lower-case input
        Vehicle carBeta = new Car("TN-09-AB-1234");  // upper-case input
        Vehicle diffCar = new Car("TN-09-XY-9999");

        System.out.println("Identity and Equality Verification:");
        System.out.println("  carAlpha: " + carAlpha);
        System.out.println("  carBeta:  " + carBeta);
        System.out.println("  Reference Equality (carAlpha == carBeta): " + (carAlpha == carBeta));
        System.out.println("  Logical Equality (carAlpha.equals(carBeta)): " + carAlpha.equals(carBeta));
        System.out.println("  carAlpha.hashCode(): " + carAlpha.hashCode());
        System.out.println("  carBeta.hashCode():  " + carBeta.hashCode());
        System.out.println("  HashCodes Match: " + (carAlpha.hashCode() == carBeta.hashCode()));
        System.out.println("  carAlpha.equals(diffCar): " + carAlpha.equals(diffCar));
        System.out.println();

        // C. Demonstrating final keyword protections
        System.out.println("Final Keyword Protections:");
        System.out.println("  Final variable SystemConfig.MAX_PARKING_HOURS: " + SystemConfig.MAX_PARKING_HOURS);
        System.out.println("  Final method getRegistrationNumber() returns immutable value: " + 
                           carAlpha.getRegistrationNumber());

        System.out.println("==================================================");
        System.out.println("Task 07 Demonstration completed successfully.");
    }
}

