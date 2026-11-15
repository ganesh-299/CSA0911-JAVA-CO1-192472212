# Task 02: Choose the Data Representation

## 1. Selected Data Types and Justifications

| Attribute | Chosen Data Type | Justification |
| :--- | :--- | :--- |
| **Floor / Slot Index** | `int` | Discrete non-negative indices. Memory-efficient 32-bit signed integer natively supported in array subscript indexing. |
| **Registration Number** | `String` | Alphanumeric representation containing letters, numbers, and hyphens (e.g., `TN-09-AB-1234`). Immutable, thread-safe, and provides built-in `.trim()`, `.toUpperCase()`, and `.equals()` support. |
| **Timestamps (Entry / Exit)** | `LocalDateTime` | Modern Java `java.time` API (immutable, thread-safe, avoids deprecated `java.util.Date`). Directly integrates with `Duration.between()` to compute elapsed time. |
| **Duration** | `long` / `int` | Measured in minutes. Duration in minutes easily exceeds integer range if multi-week, but for standard parking `long` or `int` minutes prevents decimal loss. |
| **Occupancy State** | `boolean` | Binary flag (`true` = occupied, `false` = vacant), compact 1-bit logical representation. |
| **Parking Fee** | `int` (Rupees) | Currency is represented in integer rupees. **Why not `double`?** IEEE-754 floating-point arithmetic introduces binary rounding drift (e.g., `0.1 + 0.2 = 0.30000000000000004`). In financial billing, rounding errors accumulate. `int` (or `long` paise) ensures exact arithmetic. |

---

## 2. Multi-Floor Jagged Array Structure
Real-world campus parking facilities rarely feature identical slot capacities across all floors due to architectural constraints (ground floor ramps, administrative booths, rooftop height restrictions).
A **jagged array** (an array of arrays where each row has a distinct length) is the optimal contiguous representation:
```java
// Floor 0: 3 slots, Floor 1: 5 slots, Floor 2: 2 slots
ParkingSlot[][] facility = new ParkingSlot[3][];
facility[0] = new ParkingSlot[3];
facility[1] = new ParkingSlot[5];
facility[2] = new ParkingSlot[2];
```
- Direct access to any slot is $O(1)$: `facility[floor][slot]`.
- Space allocation matches exact physical bay counts without reserving phantom slots.

---

## 3. Integer Division Pitfall vs. Started-Hour Ceiling Billing
When computing hourly parking tariffs, duration in minutes must be converted to billable hours.
In Java:
$$\text{int hours} = 90 / 60; \quad // \text{Yields } 1 \text{ (truncates remainder 30 minutes!)}$$
This results in severe revenue leakage: a motorist parked for 90 minutes would only be billed for 1 hour.

### Floating-Point Division:
$$90 / 60.0 \implies 1.5 \text{ hours}$$

### The Started-Hour Billing Rule:
University parking rules specify that **any started fraction of an hour is rounded UP to the next full hour**.
1. **Using `Math.ceil()`:**
   ```java
   long billableHours = (long) Math.ceil(durationMinutes / 60.0);
   ```
2. **Using Pure Integer Arithmetic Ceiling:**
   $$\text{billableHours} = \frac{\text{durationMinutes} + 59}{60}$$
   - If minutes = $1 \implies (1 + 59) / 60 = 60 / 60 = 1$ hour.
   - If minutes = $60 \implies (60 + 59) / 60 = 119 / 60 = 1$ hour.
   - If minutes = $61 \implies (61 + 59) / 60 = 120 / 60 = 2$ hours.
   This avoids floating-point operations altogether while guaranteeing mathematically sound ceiling division.

---

## 4. Alternative Data Representations & Complexity Analysis

### Comparison of Layouts:

| Data Structure | Indexing / Direct Access | Memory Efficiency | Ragged / Variable Capacities |
| :--- | :--- | :--- | :--- |
| **Jagged 2D Array** `ParkingSlot[f][s]` | $O(1)$ time | $O(\sum S_f)$ space: Allocates exact physical slot slots. | **Native support**: Each floor array dimensioned independently. |
| **Flat 1D Array** `ParkingSlot[N]` | $O(1)$ time via index formula: $\text{idx} = \text{floor} \times S + \text{slot}$ | $O(F \times S_{\max})$ space. | **Fails for variable floors**: The standard linear formula $\text{floor} \times \text{slotsPerFloor} + \text{slot}$ assumes equal rows. Unequal rows require an auxiliary offset array, complicating bounds checking. |
| **`ArrayList<String[]>`** | $O(1)$ row access, $O(1)$ column access | Dynamic, but high overhead (object headers, pointer indirection, boxing). | Supported dynamically, but loses compile-time type safety if storing raw string arrays. |

### Complexity Summary:
- **Jagged Array Lookup:** Time $O(1)$, Space $O(\text{Total Slots})$.
- **Linear Scan Allocation:** Time $O(\text{Total Slots})$, Space $O(1)$ auxiliary memory.

---

## 5. Conclusion
Choosing `int` for discrete indices and integer rupees for currency eliminates floating-point representation bugs. Employing a jagged 2D array natively and accurately reflects multi-floor campus architectures with differing floor layouts while preserving $O(1)$ direct slot indexing.

