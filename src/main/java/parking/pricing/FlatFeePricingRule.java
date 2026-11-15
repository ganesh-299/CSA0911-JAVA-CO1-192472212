package parking.pricing;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Flat fee pricing rule (used for EBikes @ Rs. 15).
 */
public class FlatFeePricingRule implements PricingRule {
    private final int flatAmount;

    public FlatFeePricingRule(int flatAmount) {
        if (flatAmount < 0) {
            throw new IllegalArgumentException("Flat amount cannot be negative: " + flatAmount);
        }
        this.flatAmount = flatAmount;
    }

    @Override
    public int calculateFee(LocalDateTime entryTime, LocalDateTime exitTime) {
        long minutes = Duration.between(entryTime, exitTime).toMinutes();
        return (minutes <= 0) ? 0 : flatAmount;
    }

    public int getFlatAmount() {
        return flatAmount;
    }

    @Override
    public String getRuleDescription() {
        return "Flat Tariff @ Rs. " + flatAmount;
    }
}

