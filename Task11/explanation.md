# Task 11: Implement, Test and Defend the Design

## 1. Evaluation of Core OOP Pillars in the Implementation

### A. Encapsulation:
- **Evidence:** `Vehicle.registrationNumber` and `slots[][]` are `private`/`protected`. Mutating occupancy requires calling validated methods (`parkVehicle()`, `exitVehicle()`).
- **Limitation:** In the array-based design, `activeTickets` is an internal fixed array. While the array reference itself is encapsulated, locating a ticket requires iterating through potential `null` gaps caused by departed vehicles.

### B. Inheritance:
- **Evidence:** `Bike` and `Car` extend abstract `Vehicle`; `ElectricCar` extends `Car`. Common state (`registrationNumber`, `type`) and lifecycle methods are written once in the superclass.
- **Limitation:** Multilevel inheritance binds `ElectricCar` tightly to `Car`. If an electric two-wheeler (`EBike`) enters the system, it cannot inherit from `ElectricCar` without inheriting car attributes (e.g., seating capacity).

### C. Polymorphism:
- **Evidence:** Dynamic method dispatch invokes `ratePerHour()` and EV surcharge methods polymorphically without requiring `switch` statements during fee computation.
- **Limitation:** Overriding rates works well for simple vehicles, but complex time-dependent pricing policies (e.g., peak-hour delivery vans, dynamic congestion tariffs) cannot be modeled cleanly inside vehicle classes alone without bloating the vehicle model.

### D. Abstraction:
- **Evidence:** Callers interact with abstract concepts (`Vehicle`, `parkVehicle()`, `exitVehicle()`), while slot allocation mechanics and timestamp arithmetic are hidden within the service layer.
- **Limitation:** In this Task 11 design, the pricing logic is coupled directly to `ratePerHour()` methods on vehicles, making it difficult to swap pricing algorithms without changing class declarations.

---

## 2. Test Execution Table (Evidence from Real Execution)

| Test Category | Test Case Description | Input Data | Expected Output | Actual Output | Status |
| :--- | :--- | :--- | :--- | :--- | :---: |
| **Normal** | Park Bike, Car, EV | `TN-01-BK-0001`, `TN-01-CR-0002`, `TN-01-EV-0003` | Slots `F0-S0`, `F0-S1`, `F1-S0` allocated | Allocated `F0-S0`, `F0-S1`, `F1-S0` | **PASS** |
| **Boundary** | Lot full condition | 4th vehicle into 3-slot facility | `Parking full: No slots available` | `Parking full: No slots available` | **PASS** |
| **Boundary** | Zero-minute turnaround | Entry: 10:00, Exit: 10:00 (0 mins) | Fee: Rs. 0 | Fee: Rs. 0 | **PASS** |
| **Boundary** | Exactly 60 minutes stay | Car parked 10:00 to 11:00 (60 mins) | Fee: Rs. 20 (1 billable hr) | Fee: Rs. 20 | **PASS** |
| **Boundary** | Exactly 61 minutes stay | Car parked 10:00 to 11:01 (61 mins) | Fee: Rs. 40 (2 billable hrs) | Fee: Rs. 40 | **PASS** |
| **Invalid** | Blank registration number | `new Car("   ")` | `IllegalArgumentException` | `Registration number cannot be blank.` | **PASS** |
| **Invalid** | Duplicate active registration | Park `TN-01-CR-0002` while still inside | `IllegalStateException` | `Duplicate active registration: ... is already parked.` | **PASS** |
| **Invalid** | Exit before entry | Exit time = `Entry - 30 mins` | `IllegalArgumentException` | `Exit time cannot be earlier than entry time.` | **PASS** |
| **Invalid** | Exit of unknown registration | Exit `TN-99-GHOST-9999` | `IllegalArgumentException` | `Vehicle ... not found in active parking.` | **PASS** |
| **Revenue** | Daily collection scenario | 3 vehicles (Bike 2h, Car 3h, EV 1h+charge) | Hand-calc: Rs. 150 | Actual: Rs. 150 (Match: true) | **PASS** |

---

## 3. Daily Revenue Reconciliation: Hand Calculation vs. System Output

### Scenario Specification:
- **Vehicle A (Bike):** Parked for 120 minutes (2 billable hours) at Rs. 10/hr:
  $$\text{Fee}_A = 2 \times 10 = \text{Rs. } 20$$
- **Vehicle B (Car):** Parked for 180 minutes (3 billable hours) at Rs. 20/hr:
  $$\text{Fee}_B = 3 \times 20 = \text{Rs. } 60$$
- **Vehicle C (Electric Car):** Parked for 60 minutes (1 billable hour) at Rs. 20/hr + Rs. 50 flat charging surcharge:
  $$\text{Fee}_C = (1 \times 20) + 50 = 20 + 50 = \text{Rs. } 70$$

$$\text{Total Expected Revenue} = 20 + 60 + 70 = \mathbf{\text{Rs. } 150}$$
$$\text{Actual System Revenue} = \mathbf{\text{Rs. } 150} \quad (\text{Discrepancy: Rs. } 0)$$

---

## 4. Alternative Data Structures for Active Ticket Management

| Data Structure | Vehicle Search on Exit | Removal after Locating | Memory / Space Overhead | Capacity Limitations |
| :--- | :--- | :--- | :--- | :--- |
| **`ParkingTicket[]` (Task 11 Design)** | $O(n)$ linear scan through array. | $O(1)$ set slot to `null`. (Leaves fragmentation / holes). | $O(N)$ space where $N = \text{capacity}$. | Fixed array size; overflows if ticket volume exceeds pre-allocated size. |
| **`ArrayList<ParkingTicket>`** | $O(n)$ linear scan comparing registration. | $O(n)$ time: Removing an element requires memory shifting of subsequent elements. | $O(n)$ space; dynamically grows via 1.5x resizing. | Dynamically unbounded, but removal causes memory copy churn on large lots. |
| **`HashMap<String, ParkingTicket>`** | **$O(1)$ average time** (constant time bucket hash lookup). | **$O(1)$ average time** (unlinks node in bucket without element shifting). | $O(n)$ space; additional hash entry node overhead. | Dynamically unbounded; optimal for rapid high-throughput lookups by registration number. |

### Conclusion on Data Structure Evolution:
While the fixed array `ParkingTicket[]` served effectively to demonstrate fundamental array mechanics, bounds constraints, and linear search in Task 11, the **$O(1)$ lookup and removal efficiency of `HashMap<String, ParkingTicket>`** makes it the superior, industry-standard choice for the Task 12 adapted architecture.

