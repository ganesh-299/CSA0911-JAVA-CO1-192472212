package parking.pass;

import java.time.LocalDate;

/**
 * Visitor One-Day Pass:
 * - Costs Rs. 100 upfront.
 * - Valid strictly for the purchased calendar date.
 * - Exempts holder from exit parking tariffs on that date (Exit Fee = Rs. 0).
 */
public class OneDayPass implements Pass {
    private final String passId;
    private final String associatedRegistration;
    private final LocalDate validDate;
    public static final int PASS_COST = 100; // Rs. 100

    public OneDayPass(String passId, String associatedRegistration, LocalDate validDate) {
        if (passId == null || passId.trim().isEmpty()) {
            throw new IllegalArgumentException("Pass ID cannot be blank.");
        }
        if (associatedRegistration == null || associatedRegistration.trim().isEmpty()) {
            throw new IllegalArgumentException("Associated registration cannot be blank.");
        }
        if (validDate == null) {
            throw new IllegalArgumentException("Valid date cannot be null.");
        }
        this.passId = passId.trim();
        this.associatedRegistration = associatedRegistration.trim().toUpperCase();
        this.validDate = validDate;
    }

    @Override
    public boolean isValidFor(LocalDate date) {
        return validDate.equals(date);
    }

    @Override
    public int getPurchaseCost() {
        return PASS_COST;
    }

    @Override
    public String getPassDescription() {
        return "Visitor One-Day Pass [ID=" + passId + ", Date=" + validDate + ", Cost=Rs." + PASS_COST + "]";
    }

    @Override
    public String getAssociatedRegistration() {
        return associatedRegistration;
    }

    public String getPassId() {
        return passId;
    }

    public LocalDate getValidDate() {
        return validDate;
    }
}

