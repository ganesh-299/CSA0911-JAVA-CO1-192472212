package task02;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Task02: Choose the Data Representation
 * Demonstrates:
 * 1. Primitive and Object Data Types (int, String, LocalDateTime, long, boolean).
 * 2. Jagged Arrays for variable floor capacities.
 * 3. Integer Division pitfalls vs. Started-Hour Ceiling billing.
 * 4. Scanner console input processing.
 */
public class DataRepresentationDemo {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("  Task 02: Data Representation Demonstration");
        System.out.println("==================================================");

        // 1. Primitive and Reference Types
        int floorNumber = 0;                        // int for floor index
        int slotNumber = 2;                         // int for slot index
        String regNumber = "TN-09-XY-9999";         // String for vehicle registration
        boolean isOccupied = true;                  // boolean for occupancy state
        LocalDateTime entryTime = LocalDateTime.now(); // LocalDateTime for timestamps
        long durationMinutes = 90;                  // long/int for duration in minutes
        int feeInRupees = 40;                       // int for currency (avoids float drift)

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("Data Types Demo:");
        System.out.println("  Floor (int): " + floorNumber + ", Slot (int): " + slotNumber);
        System.out.println("  Registration (String): " + regNumber);
        System.out.println("  Occupied (boolean): " + isOccupied);
        System.out.println("  Entry Time (LocalDateTime): " + entryTime.format(dtf));
        System.out.println("  Duration (long): " + durationMinutes + " minutes");
        System.out.println("  Fee (int rupees): Rs. " + feeInRupees);
        System.out.println();

        // 2. Jagged Array for Multi-Floor Parking with Different Slot Counts
        // Floor 0: 3 slots, Floor 1: 5 slots, Floor 2: 2 slots
        int[][] floorSlots = new int[3][];
        floorSlots[0] = new int[3]; // Floor 0 has 3 slots
        floorSlots[1] = new int[5]; // Floor 1 has 5 slots
        floorSlots[2] = new int[2]; // Floor 2 has 2 slots

        System.out.println("Jagged Array Layout (Floors with Variable Slot Counts):");
        for (int f = 0; f < floorSlots.length; f++) {
            System.out.println("  Floor " + f + " capacity: " + floorSlots[f].length + " slots.");
        }
        System.out.println();

        // 3. Integer Division Pitfall vs. Started-Hour Ceiling Billing
        System.out.println("Integer Division vs. Ceiling Billing Analysis (Duration = 90 mins):");
        int ratePerHour = 20;

        // Truncation risk:
        int truncatedHours = (int) (durationMinutes / 60); // 90 / 60 = 1 (truncates remainder 30 mins!)
        int truncatedFee = truncatedHours * ratePerHour;
        System.out.println("  Truncated integer division (90 / 60): " + truncatedHours + 
                           " hour -> Under-billed Fee: Rs. " + truncatedFee);

        // Floating-point division:
        double exactHours = durationMinutes / 60.0; // 90 / 60.0 = 1.5
        System.out.println("  Floating-point division (90 / 60.0): " + exactHours + " hours.");

        // Correct Started-Hour Billing using integer ceiling formula: (mins + 59) / 60
        long billableHoursIntCeil = (durationMinutes + 59) / 60;
        int correctFeeIntCeil = (int) (billableHoursIntCeil * ratePerHour);
        System.out.println("  Integer Ceiling Formula ((90 + 59) / 60): " + billableHoursIntCeil + 
                           " hours -> Correct Fee: Rs. " + correctFeeIntCeil);

        // Correct Started-Hour Billing using Math.ceil:
        long billableHoursMathCeil = (long) Math.ceil(durationMinutes / 60.0);
        int correctFeeMathCeil = (int) (billableHoursMathCeil * ratePerHour);
        System.out.println("  Math.ceil(90 / 60.0): " + billableHoursMathCeil + 
                           " hours -> Correct Fee: Rs. " + correctFeeMathCeil);
        System.out.println();

        // 4. Scanner Console Input Demonstration (Safe Simulated Input for automation)
        String simulatedInput = "KA-05-MJ-4321 75\n";
        Scanner scanner = new Scanner(simulatedInput);
        System.out.println("Scanner Input Processing (Simulated: \"" + simulatedInput.trim() + "\"):");
        if (scanner.hasNext()) {
            String scannedReg = scanner.next();
            int scannedMins = scanner.nextInt();
            long scannedBillableHours = (scannedMins + 59) / 60;
            int scannedFee = (int) (scannedBillableHours * ratePerHour);

            System.out.println("  Parsed Vehicle: " + scannedReg);
            System.out.println("  Parsed Duration: " + scannedMins + " mins");
            System.out.println("  Billable Hours: " + scannedBillableHours + " hours");
            System.out.println("  Billed Amount: Rs. " + scannedFee);
        }
        scanner.close();

        System.out.println("==================================================");
        System.out.println("Task 02 Demonstration completed successfully.");
    }
}

