package task06;

/**
 * Task06: Design Inheritance Hierarchy
 * Demonstrates:
 * - Base class (Vehicle) and Subclasses (Bike, Car, ElectricCar).
 * - Multilevel inheritance: Vehicle -> Car -> ElectricCar.
 * - Hierarchical inheritance: Vehicle -> Bike, Vehicle -> Car.
 * - super(...), super.method(), and inherited vs subclass-specific attributes.
 * - Constructor chaining and execution tracing.
 */
public class InheritanceDemo {

    // Base Class in the hierarchy
    public static class Vehicle {
        protected final String registrationNumber;
        protected final String typeName;

        public Vehicle(String registrationNumber, String typeName) {
            System.out.println("    [Constructor Trace 1/3] Vehicle initialized: " + registrationNumber);
            this.registrationNumber = registrationNumber;
            this.typeName = typeName;
        }

        public String getRegistrationNumber() {
            return registrationNumber;
        }

        public String getTypeName() {
            return typeName;
        }

        public int ratePerHour() {
            return 15; // default base rate
        }

        public String getDetails() {
            return "Type=" + typeName + ", RegNo=" + registrationNumber + ", Rate=Rs." + ratePerHour() + "/hr";
        }
    }

    // Subclass 1: Hierarchical inheritance (Vehicle -> Bike)
    public static class Bike extends Vehicle {
        private final boolean hasHelmetStorage;

        public Bike(String registrationNumber, boolean hasHelmetStorage) {
            super(registrationNumber, "Bike"); // Invoking superclass constructor
            this.hasHelmetStorage = hasHelmetStorage;
        }

        public boolean isHasHelmetStorage() {
            return hasHelmetStorage;
        }

        @Override
        public int ratePerHour() {
            return 10;
        }

        @Override
        public String getDetails() {
            return super.getDetails() + ", HelmetStorage=" + hasHelmetStorage;
        }
    }

    // Subclass 2: Hierarchical inheritance (Vehicle -> Car)
    public static class Car extends Vehicle {
        protected final int seatingCapacity;

        public Car(String registrationNumber, int seatingCapacity) {
            super(registrationNumber, "Car");
            System.out.println("    [Constructor Trace 2/3] Car initialized (Seats: " + seatingCapacity + ")");
            this.seatingCapacity = seatingCapacity;
        }

        public int getSeatingCapacity() {
            return seatingCapacity;
        }

        @Override
        public int ratePerHour() {
            return 20;
        }

        @Override
        public String getDetails() {
            return super.getDetails() + ", Seats=" + seatingCapacity;
        }
    }

    // Subclass 3: Multilevel inheritance (Vehicle -> Car -> ElectricCar)
    public static class ElectricCar extends Car {
        private final int batteryCapacityKWh;
        private final boolean chargingRequired;

        public ElectricCar(String registrationNumber, int seatingCapacity, 
                           int batteryCapacityKWh, boolean chargingRequired) {
            // Chains up to Car constructor, which in turn chains up to Vehicle constructor
            super(registrationNumber, seatingCapacity);
            System.out.println("    [Constructor Trace 3/3] ElectricCar initialized (Battery: " + 
                               batteryCapacityKWh + " kWh, Charging: " + chargingRequired + ")");
            this.batteryCapacityKWh = batteryCapacityKWh;
            this.chargingRequired = chargingRequired;
        }

        public int getBatteryCapacityKWh() {
            return batteryCapacityKWh;
        }

        public boolean isChargingRequired() {
            return chargingRequired;
        }

        @Override
        public int ratePerHour() {
            // Base car rate + charging surcharge if charging is enabled
            int baseRate = super.ratePerHour(); // Reusing parent Car rate
            return chargingRequired ? (baseRate + 5) : baseRate;
        }

        @Override
        public String getDetails() {
            // Demonstrating super.getDetails() to reuse parent string representation
            return super.getDetails() + ", Battery=" + batteryCapacityKWh + "kWh, Charging=" + chargingRequired;
        }
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  Task 06: Inheritance Hierarchy Demonstration");
        System.out.println("==================================================");

        System.out.println("A. Tracing Constructor Execution for ElectricCar:");
        // Instantiating ElectricCar triggers Vehicle -> Car -> ElectricCar constructors in sequence
        ElectricCar ev = new ElectricCar("TN-09-EV-2026", 5, 60, true);
        System.out.println();

        System.out.println("B. Hierarchical Subclass Details (Bike):");
        Bike bike = new Bike("TN-09-BK-1111", true);
        System.out.println("  " + bike.getDetails());
        System.out.println();

        System.out.println("C. Multilevel Subclass Details (ElectricCar):");
        System.out.println("  " + ev.getDetails());
        System.out.println("  ElectricCar Hourly Rate (includes EV charging): Rs. " + ev.ratePerHour());
        System.out.println();

        System.out.println("D. Standard Car Details:");
        Car car = new Car("TN-09-CR-5555", 4);
        System.out.println("  " + car.getDetails());

        System.out.println("==================================================");
        System.out.println("Task 06 Demonstration completed successfully.");
    }
}

