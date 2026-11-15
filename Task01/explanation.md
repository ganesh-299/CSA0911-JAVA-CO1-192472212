# Task 01: Understand the Problem and Identify the Objects

## 1. Problem Overview, Inputs, and Outputs
The **Campus Smart-Parking System** manages a university multi-floor parking facility utilized by students, faculty/staff, and visitors. The system coordinates vehicle entries, assigns suitable vacant parking slots, issues unique tickets, records timestamps, and calculates parking fees upon departure while recognizing pass exemptions (such as free parking for valid monthly pass holders).

### Inputs:
- **Vehicle Details:** Registration number (alphanumeric string), vehicle type (`Bike`, `Car`, `ElectricCar`).
- **User Classification:** Visitor, Student, or Staff/Faculty.
- **Pass Details:** Pass type (Monthly Pass, One-Day Pass), issue date, expiry date, holder ID.
- **Timestamp Events:** Vehicle entry time and exit time (`LocalDateTime`).
- **Facility Capacity:** Number of floors and number of slots per floor (jagged floor layout).

### Outputs:
- **Parking Ticket:** Unique ticket ID, assigned floor and slot number, vehicle registration number, and entry timestamp.
- **Occupancy Display:** Current live status of floors and vacant/occupied slots.
- **Exit Receipt & Billing:** Parking duration, applicable hourly/flat rates, pass discount, EV charging surcharge (if applicable), net fee payable, and slot release confirmation.

---

## 2. Candidate Classes and Justifications

| Candidate Class | Primary Responsibility | Justification |
| :--- | :--- | :--- |
| **`ParkingLot` / `ParkingSystem`** | Overall facility orchestrator | Manages multi-floor slot arrays, coordinates entry/exit workflows, tracks active tickets, and maintains daily revenue collections. |
| **`ParkingFloor`** | Manages slots on a single floor | Encapsulates floor-level capacity, slot arrays, and floor-specific vacancy counters. |
| **`ParkingSlot`** | Represents an individual physical parking space | Stores slot coordinates (floor, slot number), occupancy state, and reference to the parked vehicle. |
| **`Vehicle`** | Base abstraction for any motor vehicle | Encapsulates universal vehicle state: registration number, vehicle type, and pricing strategy. |
| **`Bike` / `Car` / `ElectricCar`** | Concrete vehicle specializations | Implements specific operational needs (e.g., EV battery charging attributes, specific hourly tariffs). |
| **`ParkingTicket`** | Proof of entry & active parking contract | Binds vehicle, assigned slot, entry time, and unique sequential ticket ID. |
| **`ParkingPass` / `Pass`** | Pre-purchased parking privileges | Encapsulates student/staff monthly exemptions or visitor one-day passes. |
| **`FeeCalculator` / `PricingRule`** | Financial business logic | Computes billable hours, started-hour ceilings, slab tariffs, and pass discounts without cluttering vehicle or slot models. |

---

## 3. Mapping Problem Nouns to Classes and Verbs to Methods

### Nouns to Classes:
- Parking lot / facility $\rightarrow$ `ParkingLot`
- Floor $\rightarrow$ `ParkingFloor`
- Parking slot / bay $\rightarrow$ `ParkingSlot`
- Vehicle $\rightarrow$ `Vehicle` (and subclasses `Bike`, `Car`, `ElectricCar`)
- Ticket $\rightarrow$ `ParkingTicket`
- Monthly pass / One-day pass $\rightarrow$ `MonthlyPass`, `OneDayPass`
- Parking fee $\rightarrow$ `FeeCalculator`, `PricingRule`

### Verbs to Methods:
- Park / Enter vehicle $\rightarrow$ `parkVehicle(Vehicle v)` / `allocateSlot(Vehicle v)`
- Find free slot $\rightarrow$ `findAvailableSlot(VehicleType type)`
- Issue ticket $\rightarrow$ `issueTicket(Vehicle v, ParkingSlot slot)`
- Release slot / Exit vehicle $\rightarrow$ `releaseSlot()` / `processExit(String regNo)`
- Calculate fee $\rightarrow$ `calculateFee(ParkingTicket ticket, LocalDateTime exitTime)`
- Validate pass $\rightarrow$ `isValid(LocalDate date)`

---

## 4. Business Rules
1. **Single Occupancy:** A parking slot can contain at most one vehicle at any time.
2. **Single Slot per Vehicle:** A vehicle can occupy at most one slot concurrently.
3. **Unique Ticket IDs:** Every entry generates a strictly unique ticket number using a static counter or ID generator.
4. **Valid Allocation:** Only currently vacant slots suitable for the vehicle type may be allocated.
5. **Slot Release on Exit:** When a vehicle exits and clears payment, its slot is immediately marked vacant and becomes available for incoming traffic.
6. **Pass Exemption:** Students and faculty holding a verified, active monthly pass are charged Rs. 0 parking fee.
7. **EV Charging Compatibility:** Electric vehicles requiring charging must be prioritized for EV-equipped slots with appropriate charging surcharge tracking.

---

## 5. Alternative Approach: Parallel Arrays
Before adopting an Object-Oriented Design (OOD), a procedural approach might represent parked vehicles using separate parallel arrays:
```java
String[] regNo = new String[MAX_SLOTS];
String[] vehicleType = new String[MAX_SLOTS];
long[] entryTimeEpoch = new long[MAX_SLOTS];
boolean[] slotOccupied = new boolean[MAX_SLOTS];
```

### Comparison: OOP vs. Parallel Arrays

| Evaluation Criteria | Object-Oriented Design (Classes & Objects) | Procedural Parallel Arrays |
| :--- | :--- | :--- |
| **Data Cohesion** | High: All attributes of a vehicle, slot, or ticket are bound into cohesive self-validating objects. | Low: Data is scattered across multiple disconnected arrays; index synchronization bugs are frequent. |
| **Search by Reg. Number** | Linear search $O(n)$ across ticket objects (or $O(1)$ using `HashMap`). | Linear scan $O(n)$ comparing `regNo[i]`. |
| **Adding New Attribute** | Trivial: Add a field (e.g., `boolean chargingRequired`) to `Vehicle` or `ElectricCar`. Existing code remains intact. | Cumbersome: Requires creating a brand-new array `boolean[] chargingRequired`, resizing all methods, and maintaining matching indices everywhere. |
| **Memory / Space Complexity** | $O(n)$ total space: Small object header overhead per instance, but modular and garbage-collected cleanly. | $O(n)$ space: Less per-object overhead, but fixed array sizing wastes space if under-allocated or throws overflow if exceeded. |
| **Type Safety & Encapsulation** | Strong compile-time type safety; validation inside constructors/setters. | Weak: Arrays can accidentally desynchronize if one array is updated and another is missed. |

---

## 6. Conclusion
Decomposing the problem into candidate classes (`ParkingLot`, `ParkingFloor`, `ParkingSlot`, `Vehicle`, `ParkingTicket`, `FeeCalculator`) provides strong encapsulation, eliminates fragile index-synchronization bugs inherent in parallel arrays, and establishes a clean foundation for subsequent inheritance, polymorphism, and dynamic fee calculation tasks.

