package parking.pass;

import java.time.LocalDate;

/**
 * Universal interface for parking privilege passes.
 */
public interface Pass {
    /**
     * Checks if pass is valid on the given calendar date.
     */
    boolean isValidFor(LocalDate date);

    /**
     * Upfront purchase price in rupees (e.g. Rs. 100 for OneDayPass, Rs. 0 for pre-paid MonthlyPass).
     */
    int getPurchaseCost();

    /**
     * Description of the pass.
     */
    String getPassDescription();

    /**
     * Vehicle registration number bound to this pass.
     */
    String getAssociatedRegistration();
}

