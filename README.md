# Campus Smart-Parking System - Java OOP Assignment (Tasks 1–12)

## Project Overview
The **Campus Smart-Parking System** is a complete, runnable Java project developed for university multi-floor parking facilities. It models and validates object-oriented principles covering Java fundamentals through advanced design patterns: data types, control structures, jagged arrays, constructors, static members, inheritance, polymorphism, abstract classes, interfaces, encapsulation, packages, and strategy-based dynamic pricing.

---

## Student & Submission Details
- **Student Name:** Ganesh (`ganesh-299`)
- **Register Number:** `192472212`
- **Course:** Object-Oriented Programming in Java (Course Outcome 1)
- **Target GitHub Repository:** `https://github.com/ganesh-299/CSA0911-JAVA-CO1-192472212.git`
- **Java Version:** OpenJDK 26 (Compatible with Java 17, 21, and 26 LTS/Current)

---

## Project Structure
```
CampusSmartParking/
├── README.md                          # Project documentation and quickstart
├── ASSIGNMENT_REPORT.md               # Complete academic report for Tasks 1-12 & Viva guide
├── ASSUMPTIONS.md                     # System business rules and assumptions
├── .gitignore                         # Standard git ignore file for Java projects
├── bin/                               # Compiled .class bytecode binaries
├── src/                               # Integrated production-grade application
│   ├── main/
│   │   └── java/
│   │       └── parking/
│   │           ├── model/             # Vehicle, Bike, Car, ElectricCar, EBike, DeliveryVan, Slots
│   │           ├── pricing/           # PricingRule, Hourly, FlatFee, DeliveryVan pricing
│   │           ├── pass/              # Pass, MonthlyPass, OneDayPass, PassType
│   │           ├── service/           # ParkingLot service orchestrator
│   │           └── app/               # Main application showcase
│   └── test/
│       └── java/
│           └── parking/
│               └── ParkingSystemTest.java # Pure Java automated test suite (17 tests)
├── Task01/                            # Task 01: Problem Understanding & Candidate Classes
│   ├── explanation.md
│   └── code/Task01Demo.java
├── Task02/                            # Task 02: Data Representation & Jagged Arrays
│   ├── explanation.md
│   └── code/DataRepresentationDemo.java
├── Task03/                            # Task 03: Control Logic & SIMATS Pseudocode
│   ├── explanation.md
│   └── code/ParkingFacility.java
├── Task04/                            # Task 04: Constructors & Copy Constructor Pattern
│   ├── explanation.md
│   └── code/ConstructorDemo.java
├── Task05/                            # Task 05: Static Members & Object Lifecycle
│   ├── explanation.md
│   └── code/StaticLifecycleDemo.java
├── Task06/                            # Task 06: Inheritance Hierarchy & Constructor Tracing
│   ├── explanation.md
│   └── code/InheritanceDemo.java
├── Task07/                            # Task 07: Method Overriding, Final, equals/hashCode
│   ├── explanation.md
│   └── code/OverrideDemo.java
├── Task08/                            # Task 08: Compile & Runtime Polymorphism
│   ├── explanation.md
│   └── code/PolymorphismDemo.java
├── Task09/                            # Task 09: Abstraction & Multiple Interfaces
│   ├── explanation.md
│   └── code/AbstractionDemo.java
├── Task10/                            # Task 10: Encapsulation, Packages & Wrapper Quirk
│   ├── explanation.md
│   └── code/parking/...
├── Task11/                            # Task 11: Original Array-Based System & Defense
│   ├── explanation.md
│   └── code/OriginalParkingSystem.java
├── Task12/                            # Task 12: Adapted Design (5 Floors, EBike, Van, Passes)
│   ├── explanation.md
│   └── code/AdaptedParkingSystem.java
└── test-results/                      # Execution logs and verification
    ├── compile-output.txt             # Real terminal javac compilation output
    ├── task-test-results.txt          # Complete execution logs of all 14 programs
    └── screenshots/                   # Terminal run logs and screenshot records
        └── terminal-run.txt
```

---

## How to Compile and Run in Antigravity IDE / Terminal

### Prerequisites
- OpenJDK 17 or higher installed (`java -version` and `javac -version`).
- Terminal or Antigravity IDE terminal session open in `CampusSmartParking`.

### 1. One-Step Compilation of All Files
```bash
# From inside CampusSmartParking/
mkdir -p bin
javac -d bin $(find src Task* -name "*.java")
```

### 2. Run the Integrated Main Application
```bash
java -cp bin parking.app.Main
```

### 3. Run the Automated Test Suite (17 Tests)
```bash
java -cp bin parking.ParkingSystemTest
```

### 4. Run Any Standalone Task Demo
```bash
# Task 01: Candidate Classes
java -cp bin task01.Task01Demo

# Task 02: Data Types & Ceiling Division
java -cp bin task02.DataRepresentationDemo

# Task 03: Control Logic & Jagged Slots (Standard Expected Output)
java -cp bin task03.ParkingFacility

# Task 04: Constructors & Copy Pattern
java -cp bin task04.ConstructorDemo

# Task 05: Static Members & Lifecycle
java -cp bin task05.StaticLifecycleDemo

# Task 06: Inheritance & Constructor Tracing
java -cp bin task06.InheritanceDemo

# Task 07: Overriding & Identity
java -cp bin task07.OverrideDemo

# Task 08: Polymorphic Dynamic Dispatch
java -cp bin task08.PolymorphismDemo

# Task 09: Abstraction & Interfaces
java -cp bin task09.AbstractionDemo

# Task 10: Encapsulation & Packages
java -cp bin parking.app.EncapsulationDemo

# Task 11: Original Array-Based System & Boundaries
java -cp bin task11.OriginalParkingSystem

# Task 12: Adapted Design (5 Floors, EBike, Van, Passes)
java -cp bin task12.AdaptedParkingSystem
```

---

## Actual Automated Test Results Summary
The automated test suite (`parking.ParkingSystemTest`) validates 17 core test cases with **100% pass rate**:

```
======================================================================
                 TEST SUITE EXECUTION SUMMARY
======================================================================
Total Tests Run:     17
Tests Passed:        17
Tests Failed:        0
Success Rate:        100.00%
======================================================================
ALL TESTS PASSED SUCCESSFULLY!
```

### Key Test Case Verifications:
- **Normal Operations:** Bike (Rs. 10/hr), Car (Rs. 20/hr), EV (Rs. 20/hr + Rs. 50 flat charging) $\rightarrow$ **PASS**
- **Boundary Durations:** 0 minutes (Rs. 0), 60 minutes (Rs. 20), 61 minutes (Rs. 40) $\rightarrow$ **PASS**
- **Capacity Limits:** Lot full exception intercepted $\rightarrow$ **PASS**
- **Defensive Invariants:** Duplicate active registration, negative duration, blank registration, non-parked exits $\rightarrow$ **PASS**
- **Task 12 Adaptations:** EBike flat fee (Rs. 15), Delivery Van peak (Rs. 40) vs off-peak (Rs. 20), One-Day Pass (Rs. 100 purchase, Rs. 0 exit) $\rightarrow$ **PASS**

---

## Progressive GitHub Submission Checklist
Follow these steps to submit your assignment:
- [ ] **Step 1:** Initialize git repository in this folder:
  ```bash
  git init
  ```
- [ ] **Step 2:** Fill in your real Name and Register Number in:
  - `README.md`
  - `ASSIGNMENT_REPORT.md`
- [ ] **Step 3:** Create a new remote repository on GitHub named:
  `CSA09-[YourRegisterNumber]-Assignment1`
- [ ] **Step 4:** Stage and commit your files progressively:
  ```bash
  git add .gitignore ASSUMPTIONS.md README.md
  git commit -m "docs: Initial project structure and assumptions"
  git add Task01 Task02 Task03
  git commit -m "feat: Tasks 1-3 problem definition, data types, and control logic"
  git add Task04 Task05 Task06 Task07 Task08 Task09 Task10
  git commit -m "feat: Tasks 4-10 core OOP concepts, constructors, polymorphism, packages"
  git add Task11 Task12 src test-results
  git commit -m "feat: Tasks 11-12 implementation, adapted design, tests, and execution logs"
  git add ASSIGNMENT_REPORT.md
  git commit -m "docs: Comprehensive assignment report with UML, pseudocode, and viva answers"
  ```
- [ ] **Step 5:** Link remote and push:
  ```bash
  git remote add origin https://github.com/[YourUsername]/CSA09-[YourRegisterNumber]-Assignment1.git
  git branch -M main
  git push -u origin main
  ```
- [ ] **Step 6:** Capture terminal screenshots of `parking.app.Main` and `parking.ParkingSystemTest` execution and save them in `test-results/screenshots/`.
- [ ] **Step 7:** Submit the repository URL and report PDF/Markdown to your faculty portal.

