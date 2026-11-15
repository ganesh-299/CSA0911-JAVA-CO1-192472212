package parking.model;

import parking.pass.Pass;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Encapsulates an active parking session contract.
 */
public class ParkingTicket {
    private final String ticketId;
    private final Vehicle vehicle;
    private final ParkingSlot slot;
    private final LocalDateTime entryTime;
    private Pass associatedPass;

    public ParkingTicket(String ticketId, Vehicle vehicle, ParkingSlot slot, 
                         LocalDateTime entryTime, Pass associatedPass) {
        if (ticketId == null || ticketId.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket ID cannot be blank.");
        }
        if (vehicle == null) throw new IllegalArgumentException("Vehicle cannot be null.");
        if (slot == null) throw new IllegalArgumentException("Parking slot cannot be null.");
        if (entryTime == null) throw new IllegalArgumentException("Entry time cannot be null.");

        this.ticketId = ticketId.trim();
        this.vehicle = vehicle;
        this.slot = slot;
        this.entryTime = entryTime;
        this.associatedPass = associatedPass;
    }

    public String getTicketId() { return ticketId; }
    public Vehicle getVehicle() { return vehicle; }
    public ParkingSlot getSlot() { return slot; }
    public LocalDateTime getEntryTime() { return entryTime; }
    public Pass getAssociatedPass() { return associatedPass; }
    public void setAssociatedPass(Pass pass) { this.associatedPass = pass; }

    @Override
    public String toString() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "Ticket[ID=" + ticketId + 
               ", Vehicle=" + vehicle.getRegistrationNumber() + 
               " (" + vehicle.getVehicleType() + ")" +
               ", Slot=F" + slot.getFloorNumber() + "-S" + slot.getSlotNumber() + 
               ", Entry=" + entryTime.format(dtf) + 
               ", Pass=" + (associatedPass != null ? associatedPass.getPassDescription() : "None") + "]";
    }
}

