package parking.model;

import parking.pricing.PricingRule;
import java.util.Objects;

/**
 * Universal abstract base class for all campus vehicles.
 * Composed with a PricingRule strategy for flexible tariff calculation.
 */
public abstract class Vehicle {
    protected final String registrationNumber;
    protected final VehicleType vehicleType;
    protected final PricingRule pricingRule;

    public Vehicle(String registrationNumber, VehicleType vehicleType, PricingRule pricingRule) {
        if (registrationNumber == null || registrationNumber.trim().isEmpty()) {
            throw new IllegalArgumentException("Registration number cannot be null or blank.");
        }
        this.registrationNumber = registrationNumber.trim().toUpperCase();
        this.vehicleType = Objects.requireNonNull(vehicleType, "Vehicle type cannot be null.");
        this.pricingRule = Objects.requireNonNull(pricingRule, "Pricing rule cannot be null.");
    }

    public final String getRegistrationNumber() {
        return registrationNumber;
    }

    public final VehicleType getVehicleType() {
        return vehicleType;
    }

    public final PricingRule getPricingRule() {
        return pricingRule;
    }

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

    @Override
    public String toString() {
        return vehicleType + " [" + registrationNumber + "]";
    }
}

