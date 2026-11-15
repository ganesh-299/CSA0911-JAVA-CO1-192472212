package parking.pass;

import java.time.LocalDate;

/**
 * Task10: Pass interface and simple MonthlyPass model.
 */
public interface Pass {
    boolean isValid(LocalDate date);
    int getDiscountPercent();
}

