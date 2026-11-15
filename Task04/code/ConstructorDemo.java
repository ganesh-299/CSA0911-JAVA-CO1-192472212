package task04;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Task04: Design Classes, Objects and Constructors
 * Demonstrates:
 * - Default, Parameterized, and Overloaded Constructors.
 * - Constructor Chaining using this(...).
 * - Distinguishing instance fields from parameters using this.
 * - Copy Constructor Pattern (user-defined pattern for ticket reissuing).
 * - Stack reference vs. Heap object demonstration.
 */
public class ConstructorDemo {

    public static class Ticket {
        private final String ticketId;
        private final String registrationNumber;
        private final int floorNumber;
        private final int slotNumber;
        private final LocalDateTime entryTime;
        private final boolean isReissuedCopy;

        private static int counter = 5000;

        // 1. Default Constructor (demonstrating constructor chaining via this(...))
        public Ticket() {
            this("UNREGISTERED", 0, 0); // Chains to the 3-parameter constructor
        }

        // 2. Parameterized Constructor
        public Ticket(String registrationNumber, int floorNumber, int slotNumber) {
            // Chaining to the 4-parameter master constructor with current time
            this(registrationNumber, floorNumber, slotNumber, LocalDateTime.now(), false);
        }

        // 3. Overloaded Constructor (allowing specific entry timestamp)
        public Ticket(String registrationNumber, int floorNumber, int slotNumber, LocalDateTime entryTime) {
            this(registrationNumber, floorNumber, slotNumber, entryTime, false);
        }

        // Master Private Constructor (enforcing initialization and field distinction)
        private Ticket(String registrationNumber, int floorNumber, int slotNumber, 
                       LocalDateTime entryTime, boolean isReissuedCopy) {
            // "this.fieldName" disambiguates instance variable from parameter name
            this.ticketId = "TKT-" + (++counter);
            this.registrationNumber = registrationNumber;
            this.floorNumber = floorNumber;
            this.slotNumber = slotNumber;
            this.entryTime = entryTime;
            this.isReissuedCopy = isReissuedCopy;
        }

        // 4. Copy Constructor Pattern
        // In Java, copy constructors are not built-in; they are a developer-defined pattern.
        public Ticket(Ticket other) {
            if (other == null) {
                throw new IllegalArgumentException("Cannot copy null ticket");
            }
            // Generates a new unique ticket ID while cloning original parameters
            this.ticketId = "TKT-DUP-" + (++counter);
            this.registrationNumber = other.registrationNumber;
            this.floorNumber = other.floorNumber;
            this.slotNumber = other.slotNumber;
            this.entryTime = other.entryTime;
            this.isReissuedCopy = true;
        }

        public String getTicketId() { return ticketId; }
        public String getRegistrationNumber() { return registrationNumber; }
        public int getFloorNumber() { return floorNumber; }
        public int getSlotNumber() { return slotNumber; }
        public LocalDateTime getEntryTime() { return entryTime; }
        public boolean isReissuedCopy() { return isReissuedCopy; }

        @Override
        public String toString() {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return "Ticket [ID=" + ticketId + 
                   ", Reg=" + registrationNumber + 
                   ", Slot=F" + floorNumber + "-S" + slotNumber + 
                   ", Time=" + entryTime.format(dtf) + 
                   ", Duplicate=" + isReissuedCopy + "]";
        }
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  Task 04: Constructors & Memory Model Demo");
        System.out.println("==================================================");

        // A. Default constructor (chained)
        Ticket tDefault = new Ticket();
        System.out.println("1. Default Constructor Ticket:\n   " + tDefault);

        // B. Parameterized constructor
        Ticket tParam = new Ticket("TN-07-AL-1234", 1, 4);
        System.out.println("2. Parameterized Constructor Ticket:\n   " + tParam);

        // C. Overloaded constructor (custom entry time)
        LocalDateTime pastTime = LocalDateTime.now().minusHours(2);
        Ticket tOverloaded = new Ticket("KA-01-EE-9876", 0, 2, pastTime);
        System.out.println("3. Overloaded Constructor (Custom Time) Ticket:\n   " + tOverloaded);

        // D. Copy constructor pattern (reissuing duplicate ticket)
        Ticket tReissued = new Ticket(tParam);
        System.out.println("4. Reissued Ticket via Copy Constructor Pattern:\n   " + tReissued);

        // E. Demonstrating Stack References vs. Heap Objects
        // tParam and tRef point to the exact same object on the heap
        Ticket tRef = tParam; 
        System.out.println("\nMemory Reference Verification:");
        System.out.println("  tParam == tRef (Same Heap Object): " + (tParam == tRef));
        System.out.println("  tParam == tReissued (Independent Heap Object): " + (tParam == tReissued));
        System.out.println("  tParam registration equals tReissued registration: " + 
                           tParam.getRegistrationNumber().equals(tReissued.getRegistrationNumber()));

        System.out.println("==================================================");
        System.out.println("Task 04 Demonstration completed successfully.");
    }
}

