package parking.model;

import parking.pricing.DeliveryVanPricingRule;

/**
 * Commercial Delivery Van.
 * Task 12 requirement: Double car rate (Rs. 40/hr) during 9 AM - 5 PM,
 * and standard car rate (Rs. 20/hr) outside peak hours.
 */
public class DeliveryVan extends Vehicle {
    public DeliveryVan(String registrationNumber) {
        super(registrationNumber, VehicleType.DELIVERY_VAN, new DeliveryVanPricingRule());
    }
}

