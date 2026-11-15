package task08;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Task08: Apply Polymorphism
 * Demonstrates:
 * 1. Compile-Time Polymorphism (Method Overloading).
 * 2. Runtime Polymorphism (Dynamic Method Dispatch with upcasting).
 * 3. Execution trace: Declared Type vs. Runtime Object Type vs. Executed Method.
 * 4. Eliminating long if-else / switch cascades.
 * 5. FeeDemo-style fee calculation and registration equality comparison.
 * 6. Alternative: instanceof checks with casting and complexity comparison.
 */
public class PolymorphismDemo {

    // Base Class
    public static abstract class Vehicle {
        protected final String registrationNumber;

        public Vehicle(String registrationNumber) {
            this.registrationNumber = registrationNumber.trim().toUpperCase();
        }

        public String getRegistrationNumber() {
            return registrationNumber;
        }

        public abstract int ratePerHour();

        // 1. Compile-Time Polymorphism (Method Overloading)
        // Overload A: Fee based on direct minutes
        public int calculateFee(int minutes) {
            return calculateFee(minutes, false);
        }

        // Overload B: Fee based on minutes and pass exemption flag
        public int calculateFee(int minutes, boolean hasPass) {
            if (hasPass || minutes <= 0) return 0;
            int billableHours = (minutes + 59) / 60; // started-hour ceiling
            return billableHours * ratePerHour();
        }

        // Overload C: Fee based on LocalTime in and out timestamps
        public int calculateFee(LocalTime entryTime, LocalTime exitTime) {
            long minutes = Duration.between(entryTime, exitTime).toMinutes();
            if (minutes < 0) throw new IllegalArgumentException("Exit time cannot be earlier than entry time.");
            return calculateFee((int) minutes, false);
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || !(obj instanceof Vehicle other)) return false;
            return Objects.equals(this.registrationNumber, other.registrationNumber);
        }

        @Override
        public int hashCode() {
            return Objects.hash(registrationNumber);
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " [" + registrationNumber + "]";
        }
    }

    public static class Bike extends Vehicle {
        public Bike(String registrationNumber) {
            super(registrationNumber);
        }

        @Override
        public int ratePerHour() {
            return 10;
        }
    }

    public static class Car extends Vehicle {
        public Car(String registrationNumber) {
            super(registrationNumber);
        }

        @Override
        public int ratePerHour() {
            return 20;
        }
    }

    public static class ElectricCar extends Car {
        private final boolean chargingRequired;

        public ElectricCar(String registrationNumber, boolean chargingRequired) {
            super(registrationNumber);
            this.chargingRequired = chargingRequired;
        }

        @Override
        public int ratePerHour() {
            return chargingRequired ? 25 : 20;
        }

        public boolean isChargingRequired() {
            return chargingRequired;
        }
    }

    // Alternative: Fragile instanceof-based fee calculation
    public static int calculateFeeViaInstanceof(Vehicle v, int minutes) {
        int billableHours = (minutes + 59) / 60;
        if (v instanceof ElectricCar ev) {
            return ev.isChargingRequired() ? billableHours * 25 : billableHours * 20;
        } else if (v instanceof Car) {
            return billableHours * 20;
        } else if (v instanceof Bike) {
            return billableHours * 10;
        } else {
            return billableHours * 15;
        }
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  Task 08: Polymorphism Demonstration");
        System.out.println("==================================================");

        // A. Demonstrating Compile-Time Polymorphism (Method Overloading)
        Vehicle bike = new Bike("KA-01-BK-1111");
        System.out.println("1. Compile-Time Overloading on Bike:");
        System.out.println("   Overload (int minutes = 90):        Rs. " + bike.calculateFee(90));
        System.out.println("   Overload (minutes = 90, pass=true): Rs. " + bike.calculateFee(90, true));
        System.out.println("   Overload (LocalTime 10:00 to 11:35):Rs. " + 
                           bike.calculateFee(LocalTime.of(10, 0), LocalTime.of(11, 35)));
        System.out.println();

        // B. Demonstrating Runtime Polymorphism (Dynamic Method Dispatch)
        // Upcasting: Storing different subclass instances in a common Vehicle[] array
        Vehicle[] fleet = new Vehicle[] {
            new Bike("TN-09-BK-9001"),
            new Car("TN-09-CR-9002"),
            new ElectricCar("TN-09-EV-9003", true)
        };

        System.out.println("2. Runtime Polymorphism & Dynamic Dispatch Trace (Duration = 90 mins):");
        int totalCollection = 0;
        for (Vehicle v : fleet) {
            // Tracing declared type vs actual runtime object type vs executed method
            String declaredType = "Vehicle";
            String runtimeType = v.getClass().getSimpleName();
            int fee = v.calculateFee(90); // Dynamic dispatch happens here!
            totalCollection += fee;

            System.out.println("   -> Declared: " + declaredType + 
                               " | Actual: " + runtimeType + 
                               " | Rate: Rs." + v.ratePerHour() + "/hr" + 
                               " | Fee Billed: Rs." + fee);
        }
        System.out.println("   Total Fleet Fee Collected: Rs. " + totalCollection);
        System.out.println();

        // C. Equality Verification for Same Registration Numbers
        System.out.println("3. Equality Comparison of Normalized Registration Numbers:");
        Vehicle car1 = new Car("MH-12-DE-5555");
        Vehicle car2 = new Car("mh-12-de-5555"); // case variation
        Vehicle car3 = new Car("MH-12-DE-9999"); // different reg

        System.out.println("   car1 equals car2 (same reg, different case): " + car1.equals(car2));
        System.out.println("   car1 equals car3 (different reg):            " + car1.equals(car3));
        System.out.println();

        // D. Demonstrating and comparing instanceof approach
        System.out.println("4. Procedural Instanceof Alternative Verification:");
        for (Vehicle v : fleet) {
            int feeOOP = v.calculateFee(90);
            int feeInstanceof = calculateFeeViaInstanceof(v, 90);
            System.out.println("   " + v.getRegistrationNumber() + " -> OOP Fee: Rs." + feeOOP + 
                               " == Instanceof Fee: Rs." + feeInstanceof + " (" + (feeOOP == feeInstanceof) + ")");
        }

        System.out.println("==================================================");
        System.out.println("Task 08 Demonstration completed successfully.");
    }
}

