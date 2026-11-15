package task05;

import java.time.LocalDateTime;

/**
 * Task05: Apply Static Members and Object Life Cycle
 * Demonstrates:
 * - Static fields, static methods, and static initialization blocks.
 * - Ticket object lifecycle: creation, active use, closure, and eligibility for GC.
 * - Passing objects as method arguments and returning objects from methods.
 * - Comparison: Static shared counter vs. Per-facility instance counter.
 */
public class StaticLifecycleDemo {

    public static class Ticket {
        // 1. Static Members: Shared across all instances in the JVM
        private static int totalTicketsIssued;
        private static String systemCode;

        // Static Initialization Block (executes once when class is loaded)
        static {
            systemCode = "UNIV-PARK-2026";
            totalTicketsIssued = 0;
            System.out.println("[Static Init Block] System Code initialized to: " + systemCode);
        }

        // Instance Members: Unique to each Ticket object
        private final String ticketId;
        private final String registrationNumber;
        private final LocalDateTime entryTime;
        private LocalDateTime exitTime;
        private boolean active;

        // Constructor incrementing static counter
        public Ticket(String registrationNumber) {
            this.registrationNumber = registrationNumber;
            this.entryTime = LocalDateTime.now();
            this.active = true;
            // Increments the class-level shared counter
            totalTicketsIssued++;
            this.ticketId = systemCode + "-" + String.format("%04d", totalTicketsIssued);
        }

        public void closeTicket(LocalDateTime exitTime) {
            this.exitTime = exitTime;
            this.active = false;
        }

        public String getTicketId() { return ticketId; }
        public String getRegistrationNumber() { return registrationNumber; }
        public boolean isActive() { return active; }

        // Static method accessing static members
        public static int getTotalTicketsIssued() {
            return totalTicketsIssued;
        }

        public static String getSystemCode() {
            return systemCode;
        }

        @Override
        public String toString() {
            return "Ticket{" + ticketId + ", reg=" + registrationNumber + ", active=" + active + "}";
        }
    }

    // Demonstrates: Returning an object from a method (Factory pattern)
    public static Ticket issueParkingTicket(String regNumber) {
        System.out.println("-> [Lifecycle] Allocating and issuing new Ticket for: " + regNumber);
        return new Ticket(regNumber);
    }

    // Demonstrates: Passing an object into a method (Processing / Inspection)
    public static void inspectAndProcessExit(Ticket ticket) {
        if (ticket == null) {
            System.out.println("-> [Lifecycle Error] Null ticket passed for inspection.");
            return;
        }
        System.out.println("-> [Lifecycle] Inspecting Ticket: " + ticket.getTicketId() + 
                           " (Reg: " + ticket.getRegistrationNumber() + ")");
        ticket.closeTicket(LocalDateTime.now());
        System.out.println("-> [Lifecycle] Ticket " + ticket.getTicketId() + " successfully closed.");
    }

    // Model for demonstrating Facility-level Instance Counter vs Global Static Counter
    public static class FacilityInstanceCounter {
        private final String facilityName;
        private int localCounter; // Instance counter per facility

        public FacilityInstanceCounter(String name) {
            this.facilityName = name;
            this.localCounter = 0;
        }

        public String issueLocalTicket() {
            localCounter++;
            return facilityName + "-LOCAL-" + localCounter;
        }

        public int getLocalCount() { return localCounter; }
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  Task 05: Static Members & Object Lifecycle Demo");
        System.out.println("==================================================");

        // A. Static method query before instance creation
        System.out.println("Initial Total Tickets Issued: " + Ticket.getTotalTicketsIssued());

        // B. Object Lifecycle Phase 1: Creation & Returning from method
        Ticket t1 = issueParkingTicket("TN-02-ZZ-1111");
        Ticket t2 = issueParkingTicket("TN-02-ZZ-2222");
        System.out.println("Issued Tickets: \n  " + t1 + "\n  " + t2);
        System.out.println("Current Total Tickets (via static method): " + Ticket.getTotalTicketsIssued());

        // C. Object Lifecycle Phase 2: Active use & Passing to method
        inspectAndProcessExit(t1);

        // D. Object Lifecycle Phase 3: Eligibility for Garbage Collection
        // Setting t1 reference to null removes the stack reference.
        // The Ticket object on the heap now becomes unreachable and eligible for GC.
        t1 = null; 
        System.out.println("Reference t1 set to null -> Heap object is now unreachable and eligible for GC.");

        // Note on GC execution:
        // System.gc() is merely a suggestion to the JVM; GC runs at the JVM's discretion and is not immediate.
        System.out.println("GC Status: Object reclamation is handled autonomously by the JVM Garbage Collector.");

        // E. Comparison: Global Static Counter vs. k Facility-Specific Instance Counters
        System.out.println("\n--- Static Counter vs. Instance Counter Comparison ---");
        FacilityInstanceCounter northGate = new FacilityInstanceCounter("NorthGate");
        FacilityInstanceCounter southGate = new FacilityInstanceCounter("SouthGate");

        System.out.println("North Gate Ticket: " + northGate.issueLocalTicket());
        System.out.println("North Gate Ticket: " + northGate.issueLocalTicket());
        System.out.println("South Gate Ticket: " + southGate.issueLocalTicket());

        System.out.println("North Gate Local Count: " + northGate.getLocalCount());
        System.out.println("South Gate Local Count: " + southGate.getLocalCount());
        System.out.println("Global Static Counter Total: " + Ticket.getTotalTicketsIssued());

        System.out.println("==================================================");
        System.out.println("Task 05 Demonstration completed successfully.");
    }
}

