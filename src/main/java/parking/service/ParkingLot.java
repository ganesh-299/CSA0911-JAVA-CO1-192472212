package parking.service;

import parking.model.*;
import parking.pass.OneDayPass;
import parking.pass.Pass;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Core Orchestrator Service: ParkingLot
 * - Manages multi-floor facility via a jagged array of ParkingFloor instances.
 * - Manages active tickets with a high-performance HashMap<String, ParkingTicket>.
 * - Issues passes, coordinates vehicle parking and exit, and audits financial revenues.
 */
public class ParkingLot {
    private final String facilityName;
    private final ParkingFloor[] floors;
    private final Map<String, ParkingTicket> activeTickets;
    private final Map<String, Pass> registeredPasses;

    private static int ticketCounter = 3000;
    private static int passCounter = 100;

    private int totalParkingFeesCollected = 0;
    private int totalPassSalesCollected = 0;

    /**
     * Constructs a ParkingLot with default 5-floor campus capacities {5, 8, 6, 10, 4}.
     */
    public ParkingLot(String facilityName) {
        this(facilityName, new int[]{5, 8, 6, 10, 4});
    }

    /**
     * Constructs a ParkingLot with custom floor capacities (jagged array layout).
     */
    public ParkingLot(String facilityName, int[] floorCapacities) {
        if (floorCapacities == null || floorCapacities.length == 0) {
            throw new IllegalArgumentException("Facility must have at least one floor.");
        }
        this.facilityName = facilityName;
        this.floors = new ParkingFloor[floorCapacities.length];
        for (int i = 0; i < floorCapacities.length; i++) {
            this.floors[i] = new ParkingFloor(i, floorCapacities[i]);
        }
        this.activeTickets = new HashMap<>();
        this.registeredPasses = new HashMap<>();
    }

    // --- Pass Management ---

    /**
     * Registers an existing pre-paid pass (e.g. Student / Staff Monthly Pass).
     */
    public void registerPass(Pass pass) {
        if (pass == null) throw new IllegalArgumentException("Pass cannot be null.");
        registeredPasses.put(pass.getAssociatedRegistration(), pass);
    }

    /**
     * Sells a new Visitor One-Day Pass for Rs. 100.
     */
    public OneDayPass sellOneDayPass(String registrationNumber, LocalDate date) {
        if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be blank.");
        }
        String reg = registrationNumber.trim().toUpperCase();
        String passId = "PASS-1D-" + (++passCounter);
        OneDayPass pass = new OneDayPass(passId, reg, date);
        registeredPasses.put(reg, pass);
        totalPassSalesCollected += pass.getPurchaseCost();
        return pass;
    }

    // --- Entry Workflow ---

    /**
     * Parks a vehicle at the current timestamp, associating any registered pass.
     */
    public ParkingTicket parkVehicle(Vehicle vehicle) {
        return parkVehicle(vehicle, LocalDateTime.now());
    }

    /**
     * Parks a vehicle at a specified entry timestamp.
     */
    public synchronized ParkingTicket parkVehicle(Vehicle vehicle, LocalDateTime entryTime) {
        if (vehicle == null) throw new IllegalArgumentException("Vehicle cannot be null.");
        if (entryTime == null) throw new IllegalArgumentException("Entry time cannot be null.");

        String reg = vehicle.getRegistrationNumber();

        // 1. Check duplicate active registration
        if (activeTickets.containsKey(reg)) {
            throw new IllegalStateException("Duplicate entry rejected: Vehicle " + reg + " is already parked.");
        }

        // 2. Find first available slot (scanning floors 0 to N-1)
        ParkingSlot allocatedSlot = null;
        for (ParkingFloor floor : floors) {
            for (int s = 0; s < floor.getCapacity(); s++) {
                ParkingSlot slot = floor.getSlot(s);
                if (!slot.isOccupied()) {
                    allocatedSlot = slot;
                    break;
                }
            }
            if (allocatedSlot != null) break;
        }

        if (allocatedSlot == null) {
            throw new IllegalStateException("Facility Full: All slots across all " + floors.length + " floors are occupied.");
        }

        // 3. Occupy slot
        allocatedSlot.occupy(vehicle);

        // 4. Check for existing registered pass
        Pass pass = registeredPasses.get(reg);

        // 5. Generate Ticket
        String ticketId = "TKT-" + (++ticketCounter);
        ParkingTicket ticket = new ParkingTicket(ticketId, vehicle, allocatedSlot, entryTime, pass);

        // 6. Store in active tickets map (O(1))
        activeTickets.put(reg, ticket);
        return ticket;
    }

    // --- Exit Workflow ---

    /**
     * Processes vehicle exit at the current timestamp.
     */
    public int exitVehicle(String registrationNumber) {
        return exitVehicle(registrationNumber, LocalDateTime.now());
    }

    /**
     * Processes vehicle exit at a specified timestamp, releases slot, calculates fee.
     */
    public synchronized int exitVehicle(String registrationNumber, LocalDateTime exitTime) {
        if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration cannot be blank.");
        }
        if (exitTime == null) throw new IllegalArgumentException("Exit time cannot be null.");

        String reg = registrationNumber.trim().toUpperCase();

        // 1. O(1) active ticket lookup
        ParkingTicket ticket = activeTickets.get(reg);
        if (ticket == null) {
            throw new IllegalArgumentException("Exit failed: Vehicle " + reg + " is not currently parked in this facility.");
        }

        // 2. Validate timestamp
        if (exitTime.isBefore(ticket.getEntryTime())) {
            throw new IllegalArgumentException("Exit time cannot precede entry time.");
        }

        // 3. Fee calculation
        int calculatedFee = 0;
        Pass pass = ticket.getAssociatedPass();
        boolean isCoveredByPass = (pass != null && pass.isValidFor(exitTime.toLocalDate()));

        if (isCoveredByPass) {
            calculatedFee = 0; // 100% pass discount
        } else {
            // Apply vehicle's pricing strategy
            calculatedFee = ticket.getVehicle().getPricingRule().calculateFee(ticket.getEntryTime(), exitTime);

            // Add EV charging surcharge if applicable
            if (ticket.getVehicle() instanceof Chargeable ev && ev.isChargingRequested()) {
                calculatedFee += ev.getChargingFee();
            }
        }

        // 4. Release physical slot
        ticket.getSlot().vacate();

        // 5. Remove from active map (O(1))
        activeTickets.remove(reg);

        // 6. Audit total collections
        totalParkingFeesCollected += calculatedFee;

        return calculatedFee;
    }

    // --- Queries and Audit ---

    public int getTotalSlotCapacity() {
        int sum = 0;
        for (ParkingFloor f : floors) sum += f.getCapacity();
        return sum;
    }

    public int getTotalOccupiedSlots() {
        return activeTickets.size();
    }

    public int getTotalAvailableSlots() {
        return getTotalSlotCapacity() - getTotalOccupiedSlots();
    }

    public ParkingTicket getActiveTicket(String regNo) {
        if (regNo == null) return null;
        return activeTickets.get(regNo.trim().toUpperCase());
    }

    public int getTotalParkingFeesCollected() { return totalParkingFeesCollected; }
    public int getTotalPassSalesCollected() { return totalPassSalesCollected; }
    public int getGrandTotalRevenue() { return totalParkingFeesCollected + totalPassSalesCollected; }
    public String getFacilityName() { return facilityName; }
    public int getFloorCount() { return floors.length; }
    public ParkingFloor getFloor(int floorIndex) { return floors[floorIndex]; }
}

