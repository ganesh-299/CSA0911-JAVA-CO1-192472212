package parking.model;

import parking.pricing.HourlyPricingRule;
import parking.pricing.PricingRule;

/**
 * Standard Four-Wheeler / Car.
 * Billed at baseline Rs. 20 / hour.
 */
public class Car extends Vehicle {
    public static final int CAR_HOURLY_RATE = 20;

    public Car(String registrationNumber) {
        super(registrationNumber, VehicleType.CAR, new HourlyPricingRule(CAR_HOURLY_RATE));
    }

    // Protected constructor allowing subclasses (e.g. ElectricCar) to specify type and pricing rule
    protected Car(String registrationNumber, VehicleType type, PricingRule rule) {
        super(registrationNumber, type, rule);
    }
}

