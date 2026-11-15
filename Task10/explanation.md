# Task 10: Enforce Encapsulation through Packages and Access Modifiers

## 1. Java Package Structure & Modifiers

Encapsulation bundles data (fields) and the methods that operate on them into a single self-governing unit while strictly restricting direct unauthorized access from external code.

```
CampusSmartParking/Task10/code/
└── parking/
    ├── model/      -> Vehicle.java, Car.java (Domain entities)
    ├── pricing/    -> Pricing rules and rate policies
    ├── service/    -> ParkingService.java (Workflow coordination)
    ├── pass/       -> Pass.java (Privilege rules)
    └── app/        -> EncapsulationDemo.java (Executable interface)
```

### Access Modifier Matrix:

| Modifier | Inside Same Class | Inside Same Package | Subclasses in Other Packages | Unrelated Classes Anywhere |
| :--- | :---: | :---: | :---: | :---: |
| **`private`** | **Yes** | No | No | No |
| *(default / package-private)* | **Yes** | **Yes** | No | No |
| **`protected`** | **Yes** | **Yes** | **Yes** (via inheritance) | No |
| **`public`** | **Yes** | **Yes** | **Yes** | **Yes** |

---

## 2. Defensive Validation in Constructors & Setters

### Centralized Boundary Protection:
- **Registration Strings:** Rejected if `null`, empty, or purely whitespace. Normalized using `.trim().toUpperCase()`.
- **Occupant / Capacity Limits:** Bounds-checked (e.g., occupant count strictly in $[1, 8]$).
- **Floor & Slot Indices:** Checked against facility array bounds $[0, \text{floors} - 1]$ and $[0, \text{slots} - 1]$, raising descriptive `IndexOutOfBoundsException` messages.
- **Durations & Fees:** Durations $< 0$ are immediately rejected, preventing negative invoices.

---

## 3. Wrapper Classes, Autoboxing, and the `Integer ==` Pitfall

### Autoboxing & Unboxing:
- **Autoboxing:** The automatic conversion that the Java compiler makes between the primitive type (`int`) and its corresponding object wrapper class (`Integer`).
- **Unboxing:** The reverse conversion from `Integer` object back to primitive `int`.

### The `Integer == Integer` Pitfall:
In Java, the `==` operator on reference variables tests **reference identity** (whether both variables point to the exact same memory address on the heap), **not** numeric equality!
1. **The JVM Integer Cache Quirk:**
   The JVM automatically caches `Integer` instances for numbers in the range **$-128$ to $127$**.
   - `Integer a = 100; Integer b = 100; a == b` evaluates to `true` because both point to the same cached singleton instance in the Metaspace/Heap.
2. **Outside the Cache:**
   - `Integer a = 1000; Integer b = 1000; a == b` evaluates to **`false`**! Each autoboxing call instantiates a completely separate `Integer` object on the heap at different memory addresses.
3. **The Mandatory Rule:**
   Always use **`.equals()`** when comparing object wrappers (`a.equals(b)` yields `true`), or unbox to primitive types (`int`) before applying `==`.

---

## 4. Alternative Approach: Public Fields with Caller Validation

### What happens if fields are `public` and callers do their own validation?
```java
// Anti-pattern:
public class Car {
    public String registrationNumber;
    public int occupantCount;
}
```

### Risk & Comparative Complexity:

| Criteria | Encapsulated (Private + Mutator Validation) | Open Public Fields (Caller Validation) |
| :--- | :--- | :--- |
| **Integrity Guarantee** | **Absolute:** An object can *never* enter an illegal or corrupt state. | **Fragile:** Any developer across 50 classes can write `car.occupantCount = -999;` bypass checks. |
| **Validation Cost** | $O(1)$ constant time check executed once in the constructor/setter. | $O(1)$ per check, but checks must be duplicated across every single caller in the codebase. |
| **Maintenance Burden** | Single point of change: Updating a rule requires modifying one setter. | High bug risk: Updating a validation rule requires hunting down and modifying hundreds of external call sites. |

---

## 5. Conclusion
Organizing the system into explicit packages (`parking.model`, `parking.service`, `parking.app`) combined with `private` fields and defensive validation preserves data integrity. Understanding the Integer cache quirk prevents critical financial billing bugs when working with Java wrapper objects.

