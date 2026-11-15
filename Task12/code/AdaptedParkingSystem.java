package task12;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Task12: Re-evaluate and Adapt the Design.
 * Implements:
 * - 5 floors with variable slot capacities {5, 8, 6, 10, 4} using a jagged array.
 * - HashMap<String, ParkingTicket> for O(1) active ticket operations.
 * - Strategy Pattern with PricingRule interface for modular fee policies.
 * - EBike with FlatFeePricingRule (Rs. 15).
 * - DeliveryVan with DeliveryVanPricingRule (Rs. 40/hr peak 09:00-17:00, Rs. 20/hr off-peak).
 * - Pass interface with OneDayPass (Rs. 100 purchase, Rs. 0 exit fee on valid date).
 */
public class AdaptedParkingSystem {

    // ==========================================
    // 1. Pricing Strategy (Strategy Pattern)
    // ==========================================
    public interface PricingRule {
        int calculateFee(LocalDateTime entryTime, LocalDateTime exitTime);
        String getRuleDescription();
    }

    // Standard Hourly Pricing Rule
    public static class HourlyPricingRule implements PricingRule {
        private final int hourlyRate;

        public HourlyPricingRule(int hourlyRate) {
            this.hourlyRate = hourlyRate;
        }

        @Override
        public int calculateFee(LocalDateTime entryTime, LocalDateTime exitTime) {
            long minutes = Duration.between(entryTime, exitTime).toMinutes();
            if (minutes <= 0) return 0;
            long billableHours = (minutes + 59) / 60;
            return (int) (billableHours * hourlyRate);
        }

        @Override
        public String getRuleDescription() {
            return "Hourly Tariff @ Rs. " + hourlyRate + "/hr (Ceiling Billed)";
        }
    }

    // Flat Fee Rule (for EBike)
    public static class FlatFeePricingRule implements PricingRule {
        private final int flatAmount;

        public FlatFeePricingRule(int flatAmount) {
            this.flatAmount = flatAmount;
        }

        @Override
        public int calculateFee(LocalDateTime entryTime, LocalDateTime exitTime) {
            long minutes = Duration.between(entryTime, exitTime).toMinutes();
            return (minutes <= 0) ? 0 : flatAmount;
        }

        @Override
        public String getRuleDescription() {
            return "Flat Fee @ Rs. " + flatAmount;
        }
    }

    // Delivery Van Dynamic Window Rule (Peak 9 AM - 5 PM @ Rs. 40/hr, Off-peak @ Rs. 20/hr)
    public static class DeliveryVanPricingRule implements PricingRule {
        private static final LocalTime PEAK_START = LocalTime.of(9, 0);
        private static final LocalTime PEAK_END = LocalTime.of(17, 0);
        private static final int PEAK_RATE = 40;     // Double car rate
        private static final int OFF_PEAK_RATE = 20; // Standard car rate

        @Override
        public int calculateFee(LocalDateTime entryTime, LocalDateTime exitTime) {
            long totalMinutes = Duration.between(entryTime, exitTime).toMinutes();
            if (totalMinutes <= 0) return 0;

            // Accurate minute-by-minute overlap evaluation
            long peakMinutes = 0;
            long offPeakMinutes = 0;

            LocalDateTime cursor = entryTime;
            while (cursor.isBefore(exitTime)) {
                LocalTime time = cursor.toLocalTime();
                if (!time.isBefore(PEAK_START) && time.isBefore(PEAK_END)) {
                    peakMinutes++;
                } else {
                    offPeakMinutes++;
                }
                cursor = cursor.plusMinutes(1);
            }

            // Convert minutes to billable hours
            long peakHours = (peakMinutes + 59) / 60;
            long offPeakHours = (offPeakMinutes + 59) / 60;

            // If the total hours exceed ceiling of total duration due to independent rounding, normalize:
            long totalCeilHours = (totalMinutes + 59) / 60;
            if (peakHours + offPeakHours > totalCeilHours) {
                if (peakHours > 0 && offPeakHours > 0) {
                    offPeakHours = Math.max(0, totalCeilHours - peakHours);
                }
            }

            return (int) ((peakHours * PEAK_RATE) + (offPeakHours * OFF_PEAK_RATE));
        }

        @Override
        public String getRuleDescription() {
            return "Delivery Van Window: Rs. 40/hr (09:00-17:00), Rs. 20/hr outside";
        }
    }

    // ==========================================
    // 2. Pass Subsystem
    // ==========================================
    public interface Pass {
        boolean isValidFor(LocalDate date);
        int getPurchaseCost();
        String getPassType();
    }

    public static class OneDayPass implements Pass {
        private final String passId;
        private final String vehicleRegNo;
        private final LocalDate validDate;
        private static final int COST = 100;

        public OneDayPass(String passId, String vehicleRegNo, LocalDate validDate) {
            this.passId = passId;
            this.vehicleRegNo = vehicleRegNo.trim().toUpperCase();
            this.validDate = validDate;
        }

        @Override
        public boolean isValidFor(LocalDate date) {
            return validDate.equals(date);
        }

        @Override
        public int getPurchaseCost() {
            return COST;
        }

        @Override
        public String getPassType() {
            return "One-Day Pass (Rs. " + COST + ")";
        }

        public String getVehicleRegNo() { return vehicleRegNo; }
    }

    public static class MonthlyPass implements Pass {
        private final LocalDate startDate;
        private final LocalDate endDate;

        public MonthlyPass(LocalDate startDate, LocalDate endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        @Override
        public boolean isValidFor(LocalDate date) {
            return !date.isBefore(startDate) && !date.isAfter(endDate);
        }

        @Override
        public int getPurchaseCost() { return 0; } // Campus pre-paid subscription

        @Override
        public String getPassType() { return "Monthly Pass (Free Parking)"; }
    }

    // ==========================================
    // 3. Vehicles
    // ==========================================
    public static abstract class Vehicle {
        private final String registrationNumber;
        private final PricingRule pricingRule;

        public Vehicle(String registrationNumber, PricingRule pricingRule) {
            if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
                throw new IllegalArgumentException("Registration cannot be blank.");
            }
            this.registrationNumber = registrationNumber.trim().toUpperCase();
            this.pricingRule = Objects.requireNonNull(pricingRule, "Pricing rule cannot be null.");
        }

        public String getRegistrationNumber() { return registrationNumber; }
        public PricingRule getPricingRule() { return pricingRule; }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " [" + registrationNumber + "]";
        }
    }

    public static class Bike extends Vehicle {
        public Bike(String regNo) { super(regNo, new HourlyPricingRule(10)); }
    }

    public static class Car extends Vehicle {
        public Car(String regNo) { super(regNo, new HourlyPricingRule(20)); }
    }

    public static class ElectricCar extends Car {
        private final boolean chargingRequired;
        public ElectricCar(String regNo, boolean chargingRequired) {
            super(regNo);
            this.chargingRequired = chargingRequired;
        }
        public int getChargingFee() { return chargingRequired ? 50 : 0; }
    }

    public static class EBike extends Vehicle {
        public EBike(String regNo) {
            super(regNo, new FlatFeePricingRule(15)); // Task 12 requirement: Flat fee Rs. 15
        }
    }

    public static class DeliveryVan extends Vehicle {
        public DeliveryVan(String regNo) {
            super(regNo, new DeliveryVanPricingRule()); // Task 12 requirement: Peak/off-peak rule
        }
    }

    // ==========================================
    // 4. Ticket Model
    // ==========================================
    public static class ParkingTicket {
        private final String ticketId;
        private final Vehicle vehicle;
        private final int floor;
        private final int slot;
        private final LocalDateTime entryTime;
        private Pass associatedPass;

        public ParkingTicket(String ticketId, Vehicle vehicle, int floor, int slot, 
                             LocalDateTime entryTime, Pass pass) {
            this.ticketId = ticketId;
            this.vehicle = vehicle;
            this.floor = floor;
            this.slot = slot;
            this.entryTime = entryTime;
            this.associatedPass = pass;
        }

        public String getTicketId() { return ticketId; }
        public Vehicle getVehicle() { return vehicle; }
        public int getFloor() { return floor; }
        public int getSlot() { return slot; }
        public LocalDateTime getEntryTime() { return entryTime; }
        public Pass getAssociatedPass() { return associatedPass; }
        public void setAssociatedPass(Pass pass) { this.associatedPass = pass; }
    }

    // ==========================================
    // 5. Parking Facility Service
    // ==========================================
    public static class AdaptedParkingFacility {
        // Jagged array for 5 floors: capacities {5, 8, 6, 10, 4}
        private final boolean[][] floorSlots;
        // HashMap for O(1) active ticket lookups
        private final Map<String, ParkingTicket> activeTickets;
        private int ticketSequence = 2000;
        private int totalParkingCollections = 0;
        private int totalPassSalesCollections = 0;

        public AdaptedParkingFacility(int[] floorCapacities) {
            this.floorSlots = new boolean[floorCapacities.length][];
            for (int f = 0; f < floorCapacities.length; f++) {
                this.floorSlots[f] = new boolean[floorCapacities[f]];
            }
            this.activeTickets = new HashMap<>();
        }

        public int getTotalSlotCapacity() {
            int sum = 0;
            for (boolean[] floor : floorSlots) sum += floor.length;
            return sum;
        }

        public int getAvailableSlotCount() {
            int count = 0;
            for (boolean[] floor : floorSlots) {
                for (boolean occ : floor) if (!occ) count++;
            }
            return count;
        }

        // Sell One-Day Pass
        public OneDayPass sellOneDayPass(String regNo, LocalDate date) {
            OneDayPass pass = new OneDayPass("PASS-" + (++ticketSequence), regNo, date);
            totalPassSalesCollections += pass.getPurchaseCost();
            return pass;
        }

        // Park Vehicle
        public ParkingTicket parkVehicle(Vehicle vehicle, LocalDateTime entryTime, Pass pass) {
            String reg = vehicle.getRegistrationNumber();
            if (activeTickets.containsKey(reg)) {
                throw new IllegalStateException("Duplicate active registration: " + reg + " is already parked.");
            }

            // Lowest-floor first slot allocation
            int allocFloor = -1, allocSlot = -1;
            for (int f = 0; f < floorSlots.length; f++) {
                for (int s = 0; s < floorSlots[f].length; s++) {
                    if (!floorSlots[f][s]) {
                        allocFloor = f;
                        allocSlot = s;
                        floorSlots[f][s] = true;
                        break;
                    }
                }
                if (allocFloor != -1) break;
            }

            if (allocFloor == -1) {
                throw new IllegalStateException("Facility Full: All slots across all 5 floors are occupied.");
            }

            String ticketId = "TKT-ADP-" + (++ticketSequence);
            ParkingTicket ticket = new ParkingTicket(ticketId, vehicle, allocFloor, allocSlot, entryTime, pass);
            // O(1) HashMap insertion
            activeTickets.put(reg, ticket);
            return ticket;
        }

        // Exit Vehicle
        public int exitVehicle(String regNo, LocalDateTime exitTime) {
            String reg = regNo.trim().toUpperCase();
            // O(1) HashMap lookup
            ParkingTicket ticket = activeTickets.get(reg);
            if (ticket == null) {
                throw new IllegalArgumentException("Vehicle " + reg + " not found in active parking.");
            }

            if (exitTime.isBefore(ticket.getEntryTime())) {
                throw new IllegalArgumentException("Exit time cannot be earlier than entry time.");
            }

            int fee = 0;
            Pass pass = ticket.getAssociatedPass();
            boolean passValid = (pass != null && pass.isValidFor(exitTime.toLocalDate()));

            if (passValid) {
                fee = 0; // Covered by Pass
            } else {
                fee = ticket.getVehicle().getPricingRule().calculateFee(ticket.getEntryTime(), exitTime);
                if (ticket.getVehicle() instanceof ElectricCar ev) {
                    fee += ev.getChargingFee();
                }
            }

            // Release slot
            floorSlots[ticket.getFloor()][ticket.getSlot()] = false;

            // O(1) HashMap removal
            activeTickets.remove(reg);
            totalParkingCollections += fee;
            return fee;
        }

        public int getTotalParkingCollections() { return totalParkingCollections; }
        public int getTotalPassSalesCollections() { return totalPassSalesCollections; }
        public int getGrandTotalRevenue() { return totalParkingCollections + totalPassSalesCollections; }
    }

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  Task 12: Adapted Parking System Verification");
        System.out.println("==================================================");

        // 1. Initialize 5 Floors with Capacities: {5, 8, 6, 10, 4}
        int[] capacities = new int[]{5, 8, 6, 10, 4};
        AdaptedParkingFacility facility = new AdaptedParkingFacility(capacities);
        System.out.println("1. Facility Capacities across 5 Floors: " + Arrays.toString(capacities));
        System.out.println("   Total Slots: " + facility.getTotalSlotCapacity());
        System.out.println("   Initial Available: " + facility.getAvailableSlotCount() + "\n");

        LocalDate today = LocalDate.of(2026, 11, 15);

        // 2. Test E-Bike Flat Fee (Rs. 15)
        System.out.println("2. Testing E-Bike (Flat Fee Policy):");
        EBike ebike = new EBike("TN-02-EB-1001");
        facility.parkVehicle(ebike, LocalDateTime.of(today, LocalTime.of(8, 0)), null);
        int feeEbikeShort = facility.exitVehicle(ebike.getRegistrationNumber(), 
                                                LocalDateTime.of(today, LocalTime.of(8, 30))); // 30 mins
        System.out.println("   E-Bike 30 mins exit fee: Rs. " + feeEbikeShort + " (Expected: Rs. 15)");

        facility.parkVehicle(ebike, LocalDateTime.of(today, LocalTime.of(9, 0)), null);
        int feeEbikeLong = facility.exitVehicle(ebike.getRegistrationNumber(), 
                                               LocalDateTime.of(today, LocalTime.of(15, 0))); // 6 hours
        System.out.println("   E-Bike 6 hours exit fee: Rs. " + feeEbikeLong + " (Expected: Rs. 15)\n");

        // 3. Test Delivery Van Peak vs. Off-Peak
        System.out.println("3. Testing Delivery Van Pricing:");
        DeliveryVan van = new DeliveryVan("TN-02-DV-2002");

        // Case A: Inside Peak Window (10:00 to 12:00 = 2 hrs @ Rs. 40 = Rs. 80)
        facility.parkVehicle(van, LocalDateTime.of(today, LocalTime.of(10, 0)), null);
        int feeVanPeak = facility.exitVehicle(van.getRegistrationNumber(), 
                                             LocalDateTime.of(today, LocalTime.of(12, 0)));
        System.out.println("   Delivery Van Peak (10:00 - 12:00, 2 hrs @ Rs. 40): Rs. " + 
                           feeVanPeak + " (Expected: Rs. 80)");

        // Case B: Outside Peak Window (19:00 to 21:00 = 2 hrs @ Rs. 20 = Rs. 40)
        facility.parkVehicle(van, LocalDateTime.of(today, LocalTime.of(19, 0)), null);
        int feeVanOffPeak = facility.exitVehicle(van.getRegistrationNumber(), 
                                                LocalDateTime.of(today, LocalTime.of(21, 0)));
        System.out.println("   Delivery Van Off-Peak (19:00 - 21:00, 2 hrs @ Rs. 20): Rs. " + 
                           feeVanOffPeak + " (Expected: Rs. 40)");

        // Case C: Spanning Window (16:00 to 18:00 = 1 hr Peak @ 40 + 1 hr Off-peak @ 20 = Rs. 60)
        facility.parkVehicle(van, LocalDateTime.of(today, LocalTime.of(16, 0)), null);
        int feeVanSpan = facility.exitVehicle(van.getRegistrationNumber(), 
                                             LocalDateTime.of(today, LocalTime.of(18, 0)));
        System.out.println("   Delivery Van Spanning (16:00 - 18:00, 1h peak + 1h off-peak): Rs. " + 
                           feeVanSpan + " (Expected: Rs. 60)\n");

        // 4. Test One-Day Pass
        System.out.println("4. Testing One-Day Pass Purchase & Exemption:");
        Car visitorCar = new Car("KA-05-VISITOR-9");
        OneDayPass pass = facility.sellOneDayPass(visitorCar.getRegistrationNumber(), today);
        System.out.println("   Sold One-Day Pass: " + pass.getPassType() + " for " + pass.getVehicleRegNo());
        System.out.println("   Current Pass Sales Revenue: Rs. " + facility.getTotalPassSalesCollections());

        // Park car with valid pass
        facility.parkVehicle(visitorCar, LocalDateTime.of(today, LocalTime.of(9, 30)), pass);
        int exitFeeWithPass = facility.exitVehicle(visitorCar.getRegistrationNumber(), 
                                                  LocalDateTime.of(today, LocalTime.of(17, 30))); // 8 hrs stay
        System.out.println("   Visitor Car Exit Fee (Covered by One-Day Pass): Rs. " + 
                           exitFeeWithPass + " (Expected: Rs. 0)\n");

        // 5. Test Expired / Invalid Pass
        System.out.println("5. Testing Invalid / Expired Pass Date:");
        OneDayPass expiredPass = new OneDayPass("PASS-OLD", visitorCar.getRegistrationNumber(), today.minusDays(1));
        facility.parkVehicle(visitorCar, LocalDateTime.of(today, LocalTime.of(10, 0)), expiredPass);
        int exitFeeExpired = facility.exitVehicle(visitorCar.getRegistrationNumber(), 
                                                 LocalDateTime.of(today, LocalTime.of(12, 0))); // 2 hrs @ Rs. 20 = Rs. 40
        System.out.println("   Car with Expired Pass Exit Fee: Rs. " + exitFeeExpired + " (Expected: Rs. 40)\n");

        // 6. Summary Revenue
        System.out.println("6. Financial Reconciliation:");
        System.out.println("   Total Parking Exit Fees: Rs. " + facility.getTotalParkingCollections());
        System.out.println("   Total Pass Sales Revenue:Rs. " + facility.getTotalPassSalesCollections());
        System.out.println("   Grand Total Collections: Rs. " + facility.getGrandTotalRevenue());

        System.out.println("==================================================");
        System.out.println("Task 12 Verification completed successfully.");
    }
}

