# Campus Smart-Parking System - System Rules & Design Assumptions

This document lists the business rules, operating assumptions, and design decisions adopted for the Campus Smart-Parking System across Tasks 01 to 12.

---

## 1. Core Entity & Identity Assumptions
1. **Vehicle Registration Number:**
   - Every vehicle must have a non-empty, alphanumeric registration number (e.g., `TN-01-AB-1234`).
   - Registration numbers are trimmed and normalized to upper-case for case-insensitive uniqueness and search.
   - At any given time, registration numbers are strictly unique among *currently parked* vehicles. Re-entry of an already parked registration is rejected with an error.

2. **Single Occupancy & Allocation:**
   - A parking slot can hold at most **one** vehicle at a time.
   - A vehicle can occupy at most **one** parking slot at a time.
   - Slot allocation follows a deterministic first-available strategy: scanning floor-by-floor from lowest floor (0) and lowest slot index (0) upwards.

3. **Ticket Identification:**
   - Every parking entry generates a unique ticket ID.
   - Ticket IDs are generated sequentially using an atomic/static counter (e.g., `TKT-1001`, `TKT-1002`, ...), ensuring uniqueness across concurrent allocations.

---

## 2. Temporal & Operational Assumptions
1. **Time Representation:**
   - Entry and exit timestamps are captured using `java.time.LocalDateTime`.
   - Durations are evaluated in minutes (`java.time.Duration.between(entry, exit).toMinutes()`).

2. **Validation of Timestamps:**
   - Exit time cannot precede entry time. Negative durations throw `IllegalArgumentException` or are rejected.
   - If duration is exactly `0` minutes (instant turn-around), parking fee is `Rs. 0` (unless a flat fee or pass was purchased).

3. **Started-Hour Billing Rule (Ceiling):**
   - Parking fees are billed on an hourly slab basis.
   - Any fraction of an hour started is rounded **up** to the next full hour using integer ceiling arithmetic:
     $$\text{billableHours} = \frac{\text{durationMinutes} + 59}{60}$$
   - *Example:* 1 min = 1 hour, 60 mins = 1 hour, 61 mins = 2 hours, 120 mins = 2 hours.

---

## 3. Financial & Currency Rules
1. **Currency Unit:**
   - All standard fees are computed and stored in integer Indian Rupees (`Rs.`) to prevent IEEE-754 floating-point rounding inaccuracies in financial transactions.

2. **Baseline Vehicle Hourly Rates (Tasks 01–11):**
   - **Bike (Two-Wheeler):** Rs. 10 / hour
   - **Car (Four-Wheeler):** Rs. 20 / hour
   - **Electric Car (EV):** Rs. 20 / hour base parking fee + optional flat charging fee of Rs. 50 if charging is requested.

3. **Adapted Rates (Task 12):**
   - **E-Bike:** Flat parking fee of Rs. 15 regardless of duration.
   - **Delivery Van:**
     - Peak Window: 09:00 AM to 05:00 PM (09:00 to 17:00).
     - Peak Rate: Double the car rate = Rs. 40 / hour.
     - Off-Peak Rate: Standard car rate = Rs. 20 / hour.
     - *Window calculation:* If vehicle stay spans both peak and off-peak periods, hours occurring within the 09:00–17:00 window are billed at Rs. 40/hr and outside at Rs. 20/hr. (A simplified entry-time rule is also documented as an alternative comparison).

4. **Passes & Exemptions:**
   - **Monthly Pass (Student / Faculty / Staff):**
     - Valid monthly pass gives **100% discount (Rs. 0)** on parking fees during the pass validity period.
   - **One-Day Pass (Visitor):**
     - Purchased for Rs. 100 upfront for a specific calendar date (`LocalDate`).
     - Covers all parking fees for that calendar date; at exit on that date, parking fee is Rs. 0.
     - Pass purchase fee is accounted for separately in daily revenue collections.

---

## 4. Architectural & Structural Assumptions
1. **Jagged Floor Array:**
   - Campus parking buildings have varied structural layouts across floors. Therefore, floors have different slot capacities.
   - Represented as a jagged 2D array `ParkingSlot[][]` or `int[][]`, e.g., Floor 0: 5 slots, Floor 1: 8 slots, Floor 2: 6 slots, Floor 3: 10 slots, Floor 4: 4 slots.

2. **Active Ticket Storage Evolution:**
   - **Task 11 (Original Design):** Uses a fixed-size array `ParkingTicket[]` to demonstrate linear search $O(n)$, element removal/nulling $O(1)$, and capacity boundaries.
   - **Task 12 & Integrated System:** Migrates to a `HashMap<String, ParkingTicket>` keyed by vehicle registration number for average $O(1)$ constant-time lookups and removals.

