package parking.pricing;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Dynamic window pricing rule for Delivery Vans:
 * - Peak Hours (09:00 - 17:00): Double car rate = Rs. 40 / hour.
 * - Off-Peak Hours: Standard car rate = Rs. 20 / hour.
 * Evaluates exact minute overlap across window.
 */
public class DeliveryVanPricingRule implements PricingRule {
    public static final LocalTime PEAK_START = LocalTime.of(9, 0);
    public static final LocalTime PEAK_END = LocalTime.of(17, 0);
    public static final int PEAK_RATE = 40;     // Rs. 40 / hr
    public static final int OFF_PEAK_RATE = 20; // Rs. 20 / hr

    @Override
    public int calculateFee(LocalDateTime entryTime, LocalDateTime exitTime) {
        long totalMinutes = Duration.between(entryTime, exitTime).toMinutes();
        if (totalMinutes <= 0) {
            return 0;
        }

        long peakMinutes = 0;
        long offPeakMinutes = 0;

        LocalDateTime cursor = entryTime;
        while (cursor.isBefore(exitTime)) {
            LocalTime time = cursor.toLocalTime();
            if (!time.isBefore(PEAK_START) && time.isBefore(PEAK_END)) {
                peakMinutes++;
            } else {
                offPeakMinutes++;
            }
            cursor = cursor.plusMinutes(1);
        }

        long peakHours = (peakMinutes + 59) / 60;
        long offPeakHours = (offPeakMinutes + 59) / 60;

        // Normalize ceiling if combined rounded hours exceed total duration ceiling
        long totalCeilHours = (totalMinutes + 59) / 60;
        if (peakHours + offPeakHours > totalCeilHours) {
            if (peakHours > 0 && offPeakHours > 0) {
                offPeakHours = Math.max(0, totalCeilHours - peakHours);
            }
        }

        return (int) ((peakHours * PEAK_RATE) + (offPeakHours * OFF_PEAK_RATE));
    }

    @Override
    public String getRuleDescription() {
        return "Delivery Van Rule: Rs. 40/hr (09:00-17:00 Peak), Rs. 20/hr Off-Peak";
    }
}

