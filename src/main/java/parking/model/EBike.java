package parking.model;

import parking.pricing.FlatFeePricingRule;

/**
 * Electric Two-Wheeler (E-Bike).
 * Task 12 requirement: Flat parking fee of Rs. 15 regardless of duration.
 */
public class EBike extends Vehicle {
    public static final int EBIKE_FLAT_FEE = 15;

    public EBike(String registrationNumber) {
        super(registrationNumber, VehicleType.E_BIKE, new FlatFeePricingRule(EBIKE_FLAT_FEE));
    }
}

