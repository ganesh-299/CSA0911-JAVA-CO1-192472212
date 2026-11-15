# Task 05: Apply Static Members and Object Life Cycle

## 1. Static Members in Java

### Static Fields (`static int totalTicketsIssued`)
- Belong to the class itself rather than any individual instance.
- Only **one copy** exists in JVM memory (in the Metaspace/Class area), shared across all instances of `Ticket`.
- Ideal for system-wide counters, global configurations, or thread-safe atomic sequences.

### Static Methods (`public static int getTotalTicketsIssued()`)
- Can be invoked directly on the class without instantiating an object (`Ticket.getTotalTicketsIssued()`).
- Cannot access instance variables (`this.ticketId`) because no `this` context exists in a static frame.

### Static Initialization Blocks (`static { ... }`)
- Executed exactly **once** when the class loader loads the bytecode into the JVM.
- Used for complex one-time setup (e.g., seeding configuration variables, loading environment properties, or initializing cryptographic constants).

---

## 2. Object Life Cycle in the Parking System

An object moves through four distinct lifecycle phases:

```
[1. Instantiation]  -->  [2. Active Use]  -->  [3. Closure/Unreachability]  -->  [4. Garbage Collection]
   new Ticket()           Passed to methods           t = null;                   JVM frees heap memory
```

1. **Instantiation:**
   When a vehicle enters, `new Ticket(...)` allocates heap memory, initializes instance fields, and increments the shared static counter.
2. **Active Use (Passing Objects):**
   The ticket reference is passed to validation methods (`inspectAndProcessExit(ticket)`) or stored in parking registries. Java passes object references *by value*.
3. **Closure and Unreachability:**
   When the vehicle exits, payment is completed and the ticket is marked closed. Once the reference is removed from active collections and local variables (`ticketRef = null`), the object becomes **unreachable**.
4. **Garbage Collection (GC):**
   - An unreachable object is eligible for garbage collection.
   - **Important:** Garbage collection is **not immediate**. The JVM triggers GC nondeterministically based on heap memory pressure and GC generational thresholds. Invoking `System.gc()` is merely an advisory hint, not a guarantee of immediate reclamation.

---

## 3. Comparison: Global Static Counter vs. Per-Facility Instance Counters

When managing $k$ distinct campus parking facilities (e.g., North Gate, South Gate, Tech Park) with $n$ total tickets:

| Feature | Single Global Static Counter | $k$ Per-Facility Instance Counters |
| :--- | :--- | :--- |
| **Memory Space** | **$O(1)$ space:** Exactly one integer memory cell across the entire JVM. | **$O(k)$ space:** Exactly one integer per parking facility instance. |
| **Ticket Uniqueness** | Guaranteed globally unique ticket numbers across the entire university campus. | Unique only within each local facility (e.g., both gates could produce ticket `#1`). |
| **ID Format** | `UNIV-0001`, `UNIV-0002` | `NORTH-0001`, `SOUTH-0001` |
| **Concurrency Contention** | Higher contention if thousands of gates update the single static counter simultaneously. | Low contention: Each gate increments its isolated local counter independently. |
| **When Appropriate?** | Correct when the university operates a unified ticketing and audit database where ticket IDs must never collide. | Correct when facilities are autonomous, operate offline, or prefix IDs with distinct gate identifiers. |

---

## 4. Complexity Analysis
- **Static Counter Update:** $O(1)$ time to increment.
- **Static Lookup:** $O(1)$ time to read.
- **Space Complexity:** $O(1)$ auxiliary storage for the global static counter, compared to $O(n)$ space for $n$ instantiated ticket records on the heap.

---

## 5. Conclusion
Static members provide centralized state management for ticket sequencing without wasting memory across instances. Understanding the object lifecycle ensures memory leaks are prevented by severing references to closed tickets, allowing the JVM Garbage Collector to reclaim heap resources.

