# Task 08: Apply Polymorphism

## 1. Forms of Polymorphism in Java

### 1. Compile-Time Polymorphism (Static Binding / Method Overloading):
Occurs when multiple methods within the same class share the same name but possess distinct parameter lists (different arity or parameter types).
- `calculateFee(int minutes)`
- `calculateFee(int minutes, boolean hasPass)`
- `calculateFee(LocalTime in, LocalTime out)`
The compiler resolves which method overload to invoke at compile time based strictly on argument signatures.

### 2. Runtime Polymorphism (Dynamic Binding / Method Overriding):
Occurs when a superclass reference points to a subclass object (upcasting):
```java
Vehicle v = new ElectricCar("TN-09-EV-9003", true);
int fee = v.calculateFee(90);
```
At runtime, the JVM inspects the actual object on the heap and invokes `ElectricCar.ratePerHour()`, rather than the base method in `Vehicle`.

---

## 2. Dynamic Method Dispatch Trace

Consider iterating over `Vehicle[] fleet`:
```
Iteration: v = fleet[2] (ElectricCar instance)
Declared Type:      Vehicle
Actual Object Type: ElectricCar
Method Invoked:     v.calculateFee(90)
Dispatch Chain:     Vehicle.calculateFee() -> calls this.ratePerHour()
                    -> Dynamically resolves to ElectricCar.ratePerHour() (Rs. 25/hr)
Result:             2 billable hours * Rs. 25 = Rs. 50
```

### Why Polymorphism Eliminates Long If-Else Chains:
Without polymorphism, the parking system would require exhaustive type checking:
```java
if (type.equals("BIKE")) { ... }
else if (type.equals("CAR")) { ... }
else if (type.equals("EV")) { ... }
```
Such conditional chains violate the **Open-Closed Principle (OCP)**. Every new vehicle type requires opening and modifying existing code. With dynamic dispatch, the caller simply invokes `v.calculateFee(duration)` without knowing or caring about the underlying vehicle specialization.

---

## 3. Alternative Approach: `instanceof` Cascades with Type Casting

```java
public static int calculateFeeViaInstanceof(Vehicle v, int minutes) {
    if (v instanceof ElectricCar ev) {
        return ev.isChargingRequired() ? 25 : 20;
    } else if (v instanceof Car) {
        return 20;
    } else if (v instanceof Bike) {
        return 10;
    } ...
}
```

### Comparison: Dynamic Dispatch vs. `instanceof` Chains

| Evaluation Metric | Dynamic Method Dispatch | `instanceof` Inspection + Explicit Casting |
| :--- | :--- | :--- |
| **Call Time Complexity** | $O(1)$ constant time via JVM virtual table (vtable) index. | $O(k)$ worst case, where $k$ is the number of checked subclasses. |
| **Total Evaluation over $n$ Vehicles** | $O(n)$ total time ($n \times O(1)$). | $O(n \times k)$ worst-case execution time. |
| **Type Safety** | High compile-time safety; no risk of `ClassCastException`. | Fragile: If the order of checks is incorrect (e.g., checking `Car` before `ElectricCar`), the EV check is shadowed and never reached. |
| **Extensibility** | Zero modifications required to existing code when introducing new classes. | High coupling: Every addition requires updating the `instanceof` ladder. |

---

## 4. Conclusion
Runtime polymorphism decouples the parking facility's fee calculation engine from specific vehicle implementations. It reduces runtime complexity from $O(n \times k)$ to $O(n)$, enforces the Open-Closed Principle, and guarantees correct dispatch across diverse vehicle types.

