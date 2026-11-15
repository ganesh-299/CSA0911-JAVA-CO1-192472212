package parking.model;

import parking.pricing.HourlyPricingRule;

/**
 * Electric Car (EV) extending Car and implementing Chargeable.
 * Billed at base Car rate (Rs. 20/hr) + optional flat charging fee (Rs. 50).
 */
public class ElectricCar extends Car implements Chargeable {
    private boolean chargingRequested;
    public static final int EV_CHARGING_FLAT_FEE = 50;

    public ElectricCar(String registrationNumber, boolean chargingRequested) {
        super(registrationNumber, VehicleType.ELECTRIC_CAR, new HourlyPricingRule(CAR_HOURLY_RATE));
        this.chargingRequested = chargingRequested;
    }

    @Override
    public void plugIn() {
        this.chargingRequested = true;
    }

    @Override
    public void unplug() {
        this.chargingRequested = false;
    }

    @Override
    public boolean isChargingRequested() {
        return chargingRequested;
    }

    @Override
    public int getChargingFee() {
        return chargingRequested ? EV_CHARGING_FLAT_FEE : 0;
    }

    @Override
    public boolean supportsFastCharging() {
        return true;
    }

    @Override
    public String toString() {
        return super.toString() + " (EV, Charging=" + chargingRequested + ")";
    }
}

