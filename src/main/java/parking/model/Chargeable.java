package parking.model;

/**
 * Interface representing charging capability for electric vehicles.
 */
public interface Chargeable {
    void plugIn();
    void unplug();
    boolean isChargingRequested();
    int getChargingFee();

    default boolean supportsFastCharging() {
        return false;
    }
}

