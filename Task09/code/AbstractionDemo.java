package task09;

/**
 * Task09: Introduce Abstraction
 * Demonstrates:
 * 1. Abstract class (Vehicle) with shared state, constructor, concrete methods, and abstract methods.
 * 2. Interfaces (Chargeable, Payable) modeling optional capabilities and contracts.
 * 3. Multiple interface implementation (ElectricCar extends Vehicle implements Chargeable, Payable).
 * 4. Default and static methods in interfaces.
 * 5. Architectural justification: Abstract Class vs. Interface.
 */
public class AbstractionDemo {

    // Interface 1: Capability for electric charging
    public interface Chargeable {
        // Abstract interface methods
        void plugInForCharging();
        void unplugCharging();
        int getChargingSurcharge();

        // Default interface method (provides default behavior without breaking implementers)
        default boolean isFastChargingSupported() {
            return false;
        }

        // Static interface method (utility function bound to the interface)
        static int standardKWhTariff() {
            return 12; // Rs. 12 per kWh
        }
    }

    // Interface 2: Contract for financial settlement
    public interface Payable {
        int computeFinalBill(int billableHours);
        void recordPayment(int amountRupees);

        default void printReceipt(int amountRupees) {
            System.out.println("      [Receipt] Payment received: Rs. " + amountRupees + " (Thank you!)");
        }
    }

    // Abstract Superclass: Common state and template mechanics
    public static abstract class Vehicle {
        protected final String registrationNumber;

        // Abstract classes CAN have constructors to initialize shared state
        public Vehicle(String registrationNumber) {
            this.registrationNumber = registrationNumber.trim().toUpperCase();
        }

        public final String getRegistrationNumber() {
            return registrationNumber;
        }

        // Abstract method: MUST be implemented by concrete subclasses
        public abstract int ratePerHour();
        public abstract String getCategory();

        // Concrete common method
        public int computeBaseParkingCharge(int hours) {
            return Math.max(1, hours) * ratePerHour();
        }

        @Override
        public String toString() {
            return getCategory() + " [" + registrationNumber + "]";
        }
    }

    // Subclass 1: Standard Bike (Vehicle + Payable, not Chargeable)
    public static class Bike extends Vehicle implements Payable {
        public Bike(String registrationNumber) {
            super(registrationNumber);
        }

        @Override
        public int ratePerHour() {
            return 10;
        }

        @Override
        public String getCategory() {
            return "Standard Bike";
        }

        @Override
        public int computeFinalBill(int billableHours) {
            return computeBaseParkingCharge(billableHours);
        }

        @Override
        public void recordPayment(int amountRupees) {
            System.out.println("      [Bike Payment] Settled Rs. " + amountRupees + " for " + registrationNumber);
        }
    }

    // Subclass 2: ElectricCar (Demonstrates Multiple Interface Implementation)
    public static class ElectricCar extends Vehicle implements Chargeable, Payable {
        private boolean chargingActive;
        private final boolean fastChargingCompatible;
        private final int flatChargingFee;

        public ElectricCar(String registrationNumber, boolean fastChargingCompatible, int flatChargingFee) {
            super(registrationNumber);
            this.chargingActive = false;
            this.fastChargingCompatible = fastChargingCompatible;
            this.flatChargingFee = flatChargingFee;
        }

        @Override
        public int ratePerHour() {
            return 20; // Base car parking rate
        }

        @Override
        public String getCategory() {
            return "Electric Car (EV)";
        }

        // Chargeable implementation
        @Override
        public void plugInForCharging() {
            this.chargingActive = true;
            System.out.println("      [EV Station] Connected to charging bay: " + registrationNumber);
        }

        @Override
        public void unplugCharging() {
            this.chargingActive = false;
            System.out.println("      [EV Station] Disconnected charging cable: " + registrationNumber);
        }

        @Override
        public int getChargingSurcharge() {
            return chargingActive ? flatChargingFee : 0;
        }

        @Override
        public boolean isFastChargingSupported() {
            return fastChargingCompatible; // Overrides interface default
        }

        // Payable implementation
        @Override
        public int computeFinalBill(int billableHours) {
            int base = computeBaseParkingCharge(billableHours);
            int charging = getChargingSurcharge();
            return base + charging;
        }

        @Override
        public void recordPayment(int amountRupees) {
            System.out.println("      [EV Payment] Settled Rs. " + amountRupees + " for " + registrationNumber + 
                               " (includes EV charging fee)");
        }
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  Task 09: Abstraction & Interface Demonstration");
        System.out.println("==================================================");

        // A. Static method in interface
        System.out.println("1. Interface Static Method Call:");
        System.out.println("   Standard kWh Tariff: Rs. " + Chargeable.standardKWhTariff() + " / kWh\n");

        // B. ElectricCar implementing Multiple Interfaces
        ElectricCar ev = new ElectricCar("TN-10-EV-8888", true, 50);
        System.out.println("2. Electric Vehicle Instantiation:");
        System.out.println("   Vehicle: " + ev);
        System.out.println("   Fast Charging Supported (Default/Overridden): " + ev.isFastChargingSupported());

        // Plug in and calculate bill
        ev.plugInForCharging();
        int evBill = ev.computeFinalBill(2); // 2 hours = 2 * 20 (Rs.40) + Rs.50 EV charging = Rs.90
        System.out.println("   Total Bill (2 hrs + EV Charging): Rs. " + evBill);
        ev.recordPayment(evBill);
        ev.printReceipt(evBill); // Interface default method call
        ev.unplugCharging();
        System.out.println();

        // C. Standard Bike implementing Payable
        Bike bike = new Bike("TN-10-BK-4444");
        System.out.println("3. Standard Bike Settlement:");
        System.out.println("   Vehicle: " + bike);
        int bikeBill = bike.computeFinalBill(2); // 2 hours = 2 * 10 = Rs.20
        System.out.println("   Total Bill (2 hrs): Rs. " + bikeBill);
        bike.recordPayment(bikeBill);
        bike.printReceipt(bikeBill);
        System.out.println();

        // D. Polymorphic Capability Reference
        System.out.println("4. Polymorphic Interface Binding:");
        Chargeable chargerPort = ev; // Upcasting to Chargeable interface
        System.out.println("   Bound to Chargeable interface reference: Charging Fee = Rs. " + 
                           chargerPort.getChargingSurcharge());

        System.out.println("==================================================");
        System.out.println("Task 09 Demonstration completed successfully.");
    }
}

