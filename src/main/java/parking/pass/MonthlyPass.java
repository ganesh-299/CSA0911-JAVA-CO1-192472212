package parking.pass;

import java.time.LocalDate;

/**
 * Monthly Pass for Students / Staff / Faculty.
 * Provides free parking (100% discount on exit fee) across its validity window.
 */
public class MonthlyPass implements Pass {
    private final String passId;
    private final String associatedRegistration;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final PassType passType;

    public MonthlyPass(String passId, String associatedRegistration, 
                       LocalDate startDate, LocalDate endDate, PassType passType) {
        if (passId == null || passId.trim().isEmpty()) {
            throw new IllegalArgumentException("Pass ID cannot be blank.");
        }
        if (associatedRegistration == null || associatedRegistration.trim().isEmpty()) {
            throw new IllegalArgumentException("Associated registration cannot be blank.");
        }
        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot precede start date.");
        }
        this.passId = passId.trim();
        this.associatedRegistration = associatedRegistration.trim().toUpperCase();
        this.startDate = startDate;
        this.endDate = endDate;
        this.passType = passType;
    }

    @Override
    public boolean isValidFor(LocalDate date) {
        if (date == null) return false;
        return !date.isBefore(startDate) && !date.isAfter(endDate);
    }

    @Override
    public int getPurchaseCost() {
        return 0; // Pre-purchased institutional subscription
    }

    @Override
    public String getPassDescription() {
        return passType + " [" + passId + ", Valid: " + startDate + " to " + endDate + "]";
    }

    @Override
    public String getAssociatedRegistration() {
        return associatedRegistration;
    }

    public String getPassId() {
        return passId;
    }
}

