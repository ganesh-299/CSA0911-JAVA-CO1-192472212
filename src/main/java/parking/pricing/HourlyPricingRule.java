package parking.pricing;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * Standard hourly pricing rule with started-hour ceiling billing.
 */
public class HourlyPricingRule implements PricingRule {
    private final int ratePerHour;

    public HourlyPricingRule(int ratePerHour) {
        if (ratePerHour <= 0) {
            throw new IllegalArgumentException("Hourly rate must be positive: " + ratePerHour);
        }
        this.ratePerHour = ratePerHour;
    }

    @Override
    public int calculateFee(LocalDateTime entryTime, LocalDateTime exitTime) {
        long minutes = Duration.between(entryTime, exitTime).toMinutes();
        if (minutes <= 0) {
            return 0; // 0 minutes turnaround is free
        }
        // Started-hour ceiling billing formula: (minutes + 59) / 60
        long billableHours = (minutes + 59) / 60;
        return (int) (billableHours * ratePerHour);
    }

    public int getRatePerHour() {
        return ratePerHour;
    }

    @Override
    public String getRuleDescription() {
        return "Standard Hourly Rule @ Rs. " + ratePerHour + "/hr";
    }
}

