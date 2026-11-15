# Task 04: Design Classes, Objects and Constructors

## 1. Class Design & Constructor Fundamentals

### Fields & Responsibilities of `Ticket`:
- `ticketId` (`String`): Unique sequential identifier for tracking.
- `registrationNumber` (`String`): Associated vehicle's license plate.
- `floorNumber` (`int`), `slotNumber` (`int`): Physical parking coordinates.
- `entryTime` (`LocalDateTime`): Exact timestamp of entry.
- `isReissuedCopy` (`boolean`): Flag indicating if ticket is an original or reissued reprint.

### Constructor Types Demonstrated:
1. **Default Constructor:**
   Provides baseline default values (`"UNREGISTERED"`, Floor 0, Slot 0). Useful for temporary or unassigned placeholder tickets.
2. **Parameterized Constructor:**
   Accepts core attributes (`registrationNumber`, `floorNumber`, `slotNumber`) and captures the current system timestamp.
3. **Overloaded Constructor:**
   Provides an alternate parameter list, allowing entry timestamps to be explicitly passed (vital for restoring ticket data from logs or simulated tests).
4. **Distinguishing Fields with `this`:**
   In `this.registrationNumber = registrationNumber;`, `this` explicitly resolves the instance variable on the heap, preventing variable shadowing caused by parameter names.
5. **Constructor Chaining via `this(...)`:**
   Instead of duplicating initialization logic across multiple constructors, secondary constructors chain to a master private constructor using `this(...)`. This enforces DRY (Don't Repeat Yourself) design and centralizes validation.

---

## 2. Copy Constructor Pattern in Java
Unlike C++, **Java does not provide a built-in copy constructor**. In Java, duplicating an object is achieved via:
1. The **Copy Constructor Pattern**: A developer-defined constructor taking an instance of the same class (`public Ticket(Ticket other)`).
2. The `Cloneable` interface / `clone()` method (widely discouraged in modern Java due to awkward exception handling and broken final field mechanics).

### Implementation in Parking:
```java
public Ticket(Ticket other) {
    if (other == null) throw new IllegalArgumentException("Cannot copy null ticket");
    this.ticketId = "TKT-DUP-" + (++counter); // Generates a distinct reissued ID
    this.registrationNumber = other.registrationNumber;
    this.floorNumber = other.floorNumber;
    this.slotNumber = other.slotNumber;
    this.entryTime = other.entryTime;
    this.isReissuedCopy = true;
}
```
This pattern allows the campus parking kiosk to reissue a lost receipt while preserving original entry data and clearly labeling the new ticket as a duplicate copy.

---

## 3. Java Memory Model: Stack References, Heap Objects, and Garbage Collection
- **Stack Memory:**
  Local reference variables (`tParam`, `tRef`, `tReissued`) live on the thread's execution stack frame. They store memory addresses (references) pointing to locations in heap memory.
- **Heap Memory:**
  Actual object instances created with the `new` operator (`new Ticket(...)`) are allocated on the Java heap.
- **Reference Assignment:**
  Executing `Ticket tRef = tParam;` does **not** duplicate the ticket. It merely creates a second reference variable on the stack pointing to the *exact same heap object*. Thus, `tParam == tRef` evaluates to `true`.
- **Copy Constructor Object:**
  Executing `Ticket tReissued = new Ticket(tParam);` allocates a brand-new, independent memory block on the heap. Thus, `tParam == tReissued` evaluates to `false`, even though their field values match.
- **Garbage Collection (GC):**
  When an object on the heap has no live stack references pointing to it (e.g., when a vehicle exits and its ticket is set to `null` or falls out of scope), the object becomes eligible for garbage collection. The JVM's Garbage Collector automatically reclaims its memory asynchronously during subsequent GC cycles.

---

## 4. Alternative Approaches & Complexity Analysis

### Comparison: Constructors vs. Setters vs. Static Factory Methods

| Strategy | Immutability & Thread-Safety | Validity Guarantee | Flexibility |
| :--- | :--- | :--- | :--- |
| **Parameterized Constructors** | **High:** Fields can be declared `final`. Immutable once instantiated. | Object cannot exist in an incomplete, partially-initialized state. | Rigid parameter ordering. |
| **Default Constructor + Setters** | **Low:** Fields must be mutable. Vulnerable to race conditions. | **Weak:** An object can be instantiated and used before critical setters are called. | Easy to update individual fields. |
| **Static Factory Methods** (e.g., `Ticket.createForCar(...)`) | **High:** Returns fully-formed immutable instances. | **Strong:** Validation occurs before object allocation; can return cached instances. | High; method names are descriptive. |

### Complexity Analysis:
- **Time Complexity:** Creating $n$ tickets takes $O(n)$ time regardless of whether constructors, setters, or factories are used (each allocation takes $O(1)$ fixed-time initialization).
- **Space Complexity:** $O(n)$ space across all fixed-size approaches to store $n$ ticket objects in heap memory.

---

## 5. Conclusion
Constructors with constructor chaining (`this(...)`) ensure that tickets are always born in a valid, immutable state. The copy constructor pattern provides safe, explicit object duplication for ticket reissuance without resorting to fragile cloning mechanisms.

