package parking.model;

/**
 * Task10: Car extending Vehicle.
 */
public class Car extends Vehicle {
    private int occupantCount;

    public Car(String registrationNumber, String internalCode, int occupantCount) {
        super(registrationNumber, internalCode);
        setOccupantCount(occupantCount);
    }

    // Mutator with defensive boundary validation
    public void setOccupantCount(int count) {
        if (count < 1 || count > 8) {
            throw new IllegalArgumentException("Occupant count must be between 1 and 8. Received: " + count);
        }
        this.occupantCount = count;
    }

    public int getOccupantCount() {
        return occupantCount;
    }

    @Override
    public int ratePerHour() {
        return 20;
    }
}

