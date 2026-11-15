package parking.model;

import java.util.Objects;

/**
 * Task10: Vehicle demonstrating Encapsulation, Access Modifiers, and Defensive Validation.
 */
public abstract class Vehicle {
    // 1. private field: completely hidden from all other classes, accessible only inside Vehicle
    private final String internalTrackingCode;

    // 2. protected field: accessible within package parking.model and by subclasses in other packages
    protected final String registrationNumber;

    public Vehicle(String registrationNumber, String internalCode) {
        // Defensive validation inside constructor: guarantees object cannot be created in invalid state
        if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be null or blank.");
        }
        if (internalCode == null || internalCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Internal tracking code cannot be blank.");
        }
        this.registrationNumber = registrationNumber.trim().toUpperCase();
        this.internalTrackingCode = internalCode.trim();
    }

    // 3. public method: exposed API accessible across all packages
    public String getRegistrationNumber() {
        return registrationNumber;
    }

    // Package-private helper method: accessible ONLY by classes within package parking.model
    String getInternalTrackingCode() {
        return internalTrackingCode;
    }

    public abstract int ratePerHour();

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || !(obj instanceof Vehicle other)) return false;
        return Objects.equals(this.registrationNumber, other.registrationNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registrationNumber);
    }
}

