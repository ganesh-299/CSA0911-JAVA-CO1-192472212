package parking.pricing;

import java.time.LocalDateTime;

/**
 * Strategy interface defining the contract for parking fee calculations.
 */
public interface PricingRule {
    /**
     * Calculates the parking fee in integer rupees.
     * @param entryTime timestamp when vehicle parked
     * @param exitTime timestamp when vehicle departs
     * @return fee in Indian Rupees
     */
    int calculateFee(LocalDateTime entryTime, LocalDateTime exitTime);

    /**
     * Descriptive name of the tariff rule.
     */
    String getRuleDescription();
}

