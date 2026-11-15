package parking.model;

import parking.pricing.HourlyPricingRule;

/**
 * Standard Two-Wheeler / Bike.
 * Billed at baseline Rs. 10 / hour.
 */
public class Bike extends Vehicle {
    public static final int BIKE_HOURLY_RATE = 10;

    public Bike(String registrationNumber) {
        super(registrationNumber, VehicleType.BIKE, new HourlyPricingRule(BIKE_HOURLY_RATE));
    }
}

