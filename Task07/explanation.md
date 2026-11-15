# Task 07: Override and Specialise Behaviour

## 1. Method Overriding and the `@Override` Annotation

Method overriding occurs when a subclass provides its own specific implementation for a method already defined in its superclass.

### Core Rules of Method Overriding in Java:
1. **Method Signature Match:** The method name, parameter types, and order of parameters must match the superclass declaration identically.
2. **Return Type Compatibility:** The return type must be identical or a subtype (known as a *covariant return type*).
3. **Access Privileges:** The overriding method cannot reduce the visibility of the superclass method (e.g., a `protected` method in `Vehicle` cannot be overridden as `private` or default package-private in `Bike`, but can be widened to `public`).
4. **Exception Handling:** The overriding method cannot throw broader or new checked exceptions than those declared by the superclass method.
5. **Private/Static/Final Methods:** Cannot be overridden. `static` methods are shadowed/hidden, not overridden; `final` methods produce a compile error if overridden.

### The Role of the `@Override` Annotation:
The `@Override` annotation instructs the Java compiler (`javac`) to verify that a method is genuinely overriding a superclass method. If the programmer introduces a typo in the method name (e.g., `ratePerhour()` instead of `ratePerHour()`) or alters the parameter signature, the compiler throws a compile-time error immediately, preventing subtle runtime bugs.

---

## 2. The `final` Keyword in Java

| Application | Syntax Example | Behavioral Guarantee |
| :--- | :--- | :--- |
| **`final` Variable** | `public static final int MAX_PARKING_HOURS = 72;` | Constant: Once initialized, the reference or primitive value cannot be changed or reassigned. |
| **`final` Method** | `public final String getRegistrationNumber()` | Subclasses are strictly forbidden from overriding this method. Protects core security and mathematical billing rules (such as `calculateFee(int hours)` template method). |
| **`final` Class** | `public final class SystemConfig` | The class cannot be extended or subclassed. Prevents unauthorized architectural modifications or tampering. |

---

## 3. Overriding `toString()`, `equals()`, and `hashCode()`

### Business Identity Assumption:
In a motor vehicle registry and parking management system, **two vehicle objects are physically and logically identical if they share the same normalized registration number** (e.g., `"TN-09-AB-1234"`). Registration numbers are unique state-issued identifiers.

### Implementing the Contract:
```java
@Override
public boolean equals(Object obj) {
    if (this == obj) return true; // Step 1: Reflexive identity check
    if (obj == null || !(obj instanceof Vehicle other)) return false; // Step 2: Null and type check
    return Objects.equals(this.registrationNumber, other.registrationNumber); // Step 3: Field equivalence
}

@Override
public int hashCode() {
    return Objects.hash(registrationNumber);
}
```

### The `equals()` and `hashCode()` Contract:
1. **Consistency:** If `v1.equals(v2)` returns `true`, then `v1.hashCode()` **must** produce the exact same integer hash code as `v2.hashCode()`.
2. **Collection Integrity:** If this contract is violated, storing vehicles in hash-based collections (`HashSet<Vehicle>` or `HashMap<Vehicle, ParkingTicket>`) will fail catastrophically: the set will permit duplicate vehicles, and hash map lookups will return `null`.

---

## 4. Alternative Approach: Procedural Switch vs. Dynamic Method Dispatch

```java
// Procedural approach:
public static int getRate(String type) {
    switch(type) {
        case "BIKE": return 10;
        case "CAR": return 20;
        case "ELECTRIC_CAR": return 25;
        default: return 15;
    }
}
```

### Comparative Analysis:

| Evaluation Criteria | Dynamic Method Dispatch (OOP Polymorphism) | Procedural `switch` / `if-else` |
| :--- | :--- | :--- |
| **Dispatch Time** | $O(1)$ constant time lookup through the JVM virtual method table (vtable). | $O(k)$ worst-case checks where $k$ is the number of supported types (or $O(1)$ jump table for compact enums/ints). |
| **Open-Closed Principle (OCP)** | **Strong:** Adding a new vehicle (e.g., `EBike`) requires only creating a new subclass. Zero changes to existing tested code. | **Weak:** Adding a new vehicle type requires opening and modifying existing `switch` statements across every module in the application. |
| **Maintainability** | Highly modular: Tariff logic resides inside the responsible vehicle class. | High risk: Missing a `case` branch in one switch leads to runtime calculation errors. |

---

## 5. Conclusion
Method overriding coupled with `@Override` guarantees type safety and dynamic dispatch. Enforcing `final` protects foundational business rules, while implementing consistent `equals()` and `hashCode()` ensures reliable vehicle deduplication in hash-based collections.

