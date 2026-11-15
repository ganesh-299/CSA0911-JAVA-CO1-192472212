# Task 03: Design the Control Logic

## 1. SIMATS-Style Structured Pseudocode

### Procedure 1: Finding and Allocating a Free Slot
```text
ALGORITHM AllocateSlot(slots[][])
INPUT: slots - 2D jagged boolean array representing parking floors and bays (TRUE = occupied, FALSE = free)
OUTPUT: Coordinate array [floor, slot] of allocated space, or NULL if facility is full

BEGIN
    FOR floorIndex ← 0 TO LENGTH(slots) - 1 DO
        FOR slotIndex ← 0 TO LENGTH(slots[floorIndex]) - 1 DO
            IF slots[floorIndex][slotIndex] == FALSE THEN
                slots[floorIndex][slotIndex] ← TRUE   // Mark bay as occupied
                RETURN [floorIndex, slotIndex]         // Immediate exit with allocated coordinates
            END IF
        END FOR
    END FOR
    
    // If all floors and slots have been evaluated without finding a free slot:
    RETURN NULL   // Indicates facility is completely full
END
```

### Procedure 2: Calculating Parking Fee
```text
ALGORITHM CalculateParkingFee(vehicleType, minutesParked, hasValidPass)
INPUT: vehicleType (STRING), minutesParked (INTEGER), hasValidPass (BOOLEAN)
OUTPUT: feeInRupees (INTEGER)

BEGIN
    // Rule 1: Check pass exemption
    IF hasValidPass == TRUE THEN
        RETURN 0
    END IF

    // Rule 2: Zero minutes duration
    IF minutesParked <= 0 THEN
        RETURN 0
    END IF

    // Rule 3: Started-hour ceiling formula
    billableHours ← (minutesParked + 59) / 60

    // Rule 4: Vehicle-specific rates and daily caps
    SWITCH UPPERCASE(vehicleType) DO
        CASE "BIKE":
            ratePerHour ← 10
            dailyMaxCap ← 100
        CASE "CAR":
            ratePerHour ← 20
            dailyMaxCap ← 250
        CASE "ELECTRIC_CAR", "EV":
            ratePerHour ← 20
            dailyMaxCap ← 250
        DEFAULT:
            RAISE ERROR "Unknown vehicle type"
    END SWITCH

    calculatedFee ← billableHours * ratePerHour

    // Rule 5: Apply daily maximum ceiling
    IF calculatedFee > dailyMaxCap THEN
        feeInRupees ← dailyMaxCap
    ELSE
        feeInRupees ← calculatedFee
    END IF

    RETURN feeInRupees
END
```

---

## 2. Control Flow Analysis: `break`, `continue`, `return`, and Full Facility Handling
- **`return` Statement:** In `allocateSlot()`, an immediate `return new int[]{f, s}` is executed the millisecond an unoccupied slot is identified. This terminates execution of both nested loops immediately, guaranteeing that redundant slot checks are avoided once a solution is found.
- **`break` Statement:** Used to prematurely exit the innermost loop. For example, if a floor is flagged as closed for maintenance, a `break` aborts checking the remaining slots on that floor and transitions execution to the next floor.
- **`continue` Statement:** Skips the rest of the current iteration and advances the loop counter. For example, if an EV is being parked, non-EV slots can be bypassed using `if (!isEVSlot) continue;`.
- **Handling a Full Lot:** When all elements across all floors evaluate to `true` (occupied), the nested loops exhaust naturally. The method then returns `null` (or in object systems, throws a custom `ParkingFullException`). The calling layer intercepts `null` and informs the driver with a user-friendly message rather than crashing with an unhandled exception.

---

## 3. Standard Verification & Output
The compiled Java program `ParkingFacility.java` was executed. The facility was initialized with 2 floors (Floor 0: 2 slots, Floor 1: 1 slot).

### Actual Terminal Execution Output:
```
[0, 0]
[0, 1]
[1, 0]
null
true false false
[0, 1]
```
- Line 1–3: Successive allocations consume `[0, 0]`, `[0, 1]`, and `[1, 0]`.
- Line 4: Attempting a 4th allocation when full correctly returns `null`.
- Line 5: After releasing `[0, 1]`, slot occupancy query verifies: `[0, 0]` is occupied (`true`), `[0, 1]` is free (`false`), and the facility is no longer full (`false`).
- Line 6: Re-invoking `allocateSlot()` scans from floor 0 and correctly re-allocates the vacated `[0, 1]`.

---

## 4. Alternative Approach: Free-Slot Stacks vs. Linear Scan

| Evaluation Metric | Sequential Scanning (Nested Loops) | Free-Slot Stack / Deque per Type |
| :--- | :--- | :--- |
| **Allocation Time** | $O(F \times S)$ in the worst case (scans every bay before finding free slot or reporting full). | $O(1)$ constant time (pops top available slot coordinate directly from stack). |
| **Release Time** | $O(1)$ direct array index assignment: `slots[f][s] = false;`. | $O(1)$ constant time (pushes released coordinate back onto free stack). |
| **Auxiliary Storage** | $O(1)$ extra space beyond the slot matrix. | $O(N)$ extra memory where $N = \text{Total Slots}$ to maintain references/coordinates in stack data structures. |
| **Ordered Allocation** | Guarantees deterministic lowest-floor, lowest-slot allocation order naturally. | Requires a `PriorityQueue` (Min-Heap) if lowest-floor order is mandatory, increasing push/pop to $O(\log N)$. |

---

## 5. Conclusion
Nested iteration with short-circuit `return` provides intuitive, predictable, lowest-floor slot allocation with zero auxiliary memory overhead. For campus facilities with dozens to hundreds of slots, $O(F \times S)$ linear scan takes microseconds, making it a reliable and robust design.

