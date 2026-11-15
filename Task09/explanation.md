# Task 09: Introduce Abstraction

## 1. Abstract Classes vs. Interfaces in Java

Abstraction hides complex internal implementation details while exposing a clear, standardized contract to the outside system.

| Characteristic | Abstract Class (`abstract class Vehicle`) | Interface (`interface Chargeable`, `Payable`) |
| :--- | :--- | :--- |
| **Primary Architectural Role** | Defines an **is-a** identity and provides shared state and core plumbing. | Defines a **can-do** capability or contractual behavior across unrelated types. |
| **Instance State / Fields** | Can declare instance fields (`protected final String registrationNumber`). | Cannot hold per-instance state. Fields are implicitly `public static final` constants. |
| **Constructors** | **Has constructors:** Called via `super(...)` during subclass instantiation to initialize shared state. | **No constructors:** Cannot be instantiated and cannot initialize instance variables. |
| **Inheritance Multiplicity** | Single class inheritance: A class can extend at most one abstract superclass. | Multiple inheritance: A class can implement any number of interfaces (`implements Chargeable, Payable`). |
| **Methods** | Concrete, template, final, and `abstract` methods. | Abstract methods, `default` methods (with fallback logic), and `static` utility methods. |

---

## 2. Why `Vehicle` is an Abstract Class and `Chargeable` is an Interface

1. **`Vehicle` as an Abstract Class:**
   All vehicles (`Bike`, `Car`, `ElectricCar`, `EBike`) share universal physical identity: a state-issued registration number, an entry timestamp, and common parking bay associations. Declaring `Vehicle` as an abstract class allows this state to be declared once, protected by encapsulation, and reliably initialized via a base constructor.
2. **`Chargeable` as an Interface:**
   Charging is **not** an inherent property of all vehicles. A gasoline car or standard bicycle cannot plug into an electrical grid. Defining `Chargeable` as an interface models an optional capability. Furthermore, non-vehicular campus equipment (e.g., maintenance carts, emergency backup batteries) could also implement `Chargeable` without forcing them into the `Vehicle` inheritance hierarchy.

---

## 3. Alternative Architecture: Inverting Abstract Class and Interface

### What happens if `Vehicle` is made an interface and `Chargeable` is an abstract class?
```java
public interface Vehicle {
    String getRegistrationNumber();
    int ratePerHour();
}

public abstract class ChargeableEntity {
    protected int batteryCapacity;
    // ...
}
```

### Critical Flaws and Limitations of this Inversion:
1. **Loss of Shared Instance State:** Interfaces cannot declare non-static instance fields. Every single concrete vehicle class (`Bike`, `Car`, `DeliveryVan`, etc.) would be forced to duplicate the exact same `private String registrationNumber;` field, constructor parameter, and getter boilerplate.
2. **Single Inheritance Bottleneck:** In Java, a class can only extend one abstract class. If `ElectricCar` extends `ChargeableEntity`, it cannot extend `Car`. The natural hierarchy (`ElectricCar extends Car`) is completely severed because Java prohibits multiple class inheritance (`class ElectricCar extends Car, ChargeableEntity` is illegal syntax).
3. **Conclusion:** Modeling the primary identity (`Vehicle`) as the abstract class and peripheral capabilities (`Chargeable`, `Payable`) as interfaces is the only design that complies with Java's single class inheritance model and DRY (Don't Repeat Yourself) principle.

---

## 4. Complexity & Performance Analysis
- **Method Call Dispatch:** Both abstract class virtual method calls and interface method calls resolve in $O(1)$ constant time (the JVM JIT compiler optimizes interface table lookups via inline caching and itables).
- **Per-Object Memory Footprint:** $O(1)$ memory per instance. An abstract class stores state directly in the object's contiguous heap layout; implementing an interface adds zero per-instance data fields, preserving minimal memory consumption.

---

## 5. Conclusion
Using `abstract class Vehicle` guarantees consistent initialization and eliminates state duplication. Interfaces (`Chargeable`, `Payable`) enable loose coupling and flexible multi-capability composition, allowing `ElectricCar` to satisfy multiple architectural roles seamlessly.

