import java.util.LinkedList;
import java.util.List;

/**
 * Hotel Booking Management System - Use Case 7: Add-On Service Selection
 *
 * This class demonstrates how optional add-on services can be attached to reservations
 * without modifying core booking or inventory logic. It showcases:
 * - One-to-many relationship between reservations and services using Map<String, List<Service>>
 * - Business extensibility by composing services with reservations
 * - Separation of core and optional features
 * - Cost aggregation for pricing calculations
 *
 * Key Data Structures:
 * - AddOnServiceManager: Manages Map<String, List<AddOnService>>
 * - LinkedList: Preserves insertion order of services
 * - HashMap: Efficient lookup of services by reservation ID
 *
 * @author IndAdityaSingh
 * @version 7.0
 * @since 2026-03-25
 */
public class HotelBookingManagementSystem {

    /**
     * Main method - Entry point for Use Case 7.
     * Demonstrates attaching optional services to reservations.
     *
     * @param args Command-line arguments (not used in this use case)
     */
    public static void main(String[] args) {
        System.out.println("================================================");
        System.out.println("Hotel Booking Management System - Use Case 7");
        System.out.println("Add-On Service Selection");
        System.out.println("================================================\n");

        // Initialize the Add-On Service Manager
        AddOnServiceManager serviceManager = new AddOnServiceManager();

        // Create available add-on services
        System.out.println("========== Available Add-On Services ==========\n");

        AddOnService breakfastService = new AddOnService(
                "SVC001",
                "Complimentary Breakfast",
                "Daily breakfast buffet from 6:00 AM to 10:00 AM",
                15.00
        );

        AddOnService wifiService = new AddOnService(
                "SVC002",
                "High-Speed WiFi",
                "Premium high-speed internet access for entire stay",
                10.00
        );

        AddOnService parkingService = new AddOnService(
                "SVC003",
                "Valet Parking",
                "Professional valet parking service",
                20.00
        );

        AddOnService fitnessService = new AddOnService(
                "SVC004",
                "Fitness Center Access",
                "24/7 access to fully equipped fitness center",
                12.00
        );

        AddOnService spaService = new AddOnService(
                "SVC005",
                "Spa Package",
                "Relaxing spa treatment (60 minutes massage)",
                50.00
        );

        AddOnService airportTransferService = new AddOnService(
                "SVC006",
                "Airport Transfer",
                "Round-trip airport transportation",
                35.00
        );

        // Display all available services
        List<AddOnService> availableServices = new LinkedList<>();
        availableServices.add(breakfastService);
        availableServices.add(wifiService);
        availableServices.add(parkingService);
        availableServices.add(fitnessService);
        availableServices.add(spaService);
        availableServices.add(airportTransferService);

        int serviceNum = 1;
        for (AddOnService service : availableServices) {
            System.out.println(serviceNum + ". " + service);
            serviceNum++;
        }

        System.out.println();
        System.out.println("========== Guest Reservations & Service Selection ==========\n");

        // Reservation 1: Guest 1 selects multiple services
        String reservation1Id = "RES001";
        System.out.println("Reservation 1 (ID: " + reservation1Id + ") - Guest: John Doe");
        System.out.println("Room Type: Double Room | Duration: 2 nights | Base Cost: $200");
        System.out.println("Selected Services:");
        serviceManager.addServiceToReservation(reservation1Id, breakfastService);
        serviceManager.addServiceToReservation(reservation1Id, wifiService);
        serviceManager.addServiceToReservation(reservation1Id, parkingService);
        System.out.println();

        // Reservation 2: Guest 2 selects different services
        String reservation2Id = "RES002";
        System.out.println("Reservation 2 (ID: " + reservation2Id + ") - Guest: Jane Smith");
        System.out.println("Room Type: Single Room | Duration: 1 night | Base Cost: $100");
        System.out.println("Selected Services:");
        serviceManager.addServiceToReservation(reservation2Id, breakfastService);
        serviceManager.addServiceToReservation(reservation2Id, fitnessService);
        System.out.println();

        // Reservation 3: Guest 3 selects premium services
        String reservation3Id = "RES003";
        System.out.println("Reservation 3 (ID: " + reservation3Id + ") - Guest: Alice Johnson");
        System.out.println("Room Type: Suite Room | Duration: 3 nights | Base Cost: $450");
        System.out.println("Selected Services:");
        serviceManager.addServiceToReservation(reservation3Id, breakfastService);
        serviceManager.addServiceToReservation(reservation3Id, spaService);
        serviceManager.addServiceToReservation(reservation3Id, airportTransferService);
        System.out.println();

        // Reservation 4: Guest 4 no add-on services
        String reservation4Id = "RES004";
        System.out.println("Reservation 4 (ID: " + reservation4Id + ") - Guest: Bob Wilson");
        System.out.println("Room Type: Double Room | Duration: 1 night | Base Cost: $200");
        System.out.println("Selected Services: None");
        System.out.println();

        // Reservation 5: Guest 5 selects multiple services
        String reservation5Id = "RES005";
        System.out.println("Reservation 5 (ID: " + reservation5Id + ") - Guest: Carol Davis");
        System.out.println("Room Type: Single Room | Duration: 2 nights | Base Cost: $200");
        System.out.println("Selected Services:");
        serviceManager.addServiceToReservation(reservation5Id, wifiService);
        serviceManager.addServiceToReservation(reservation5Id, parkingService);
        serviceManager.addServiceToReservation(reservation5Id, fitnessService);
        System.out.println();

        // Display service details and cost breakdown
        System.out.println("========== Detailed Service & Cost Breakdown ==========\n");

        displayReservationWithServices(reservation1Id, "John Doe", 200.00, serviceManager);
        displayReservationWithServices(reservation2Id, "Jane Smith", 100.00, serviceManager);
        displayReservationWithServices(reservation3Id, "Alice Johnson", 450.00, serviceManager);
        displayReservationWithServices(reservation4Id, "Bob Wilson", 200.00, serviceManager);
        displayReservationWithServices(reservation5Id, "Carol Davis", 200.00, serviceManager);

        // Summary of all services across all reservations
        System.out.println("========== System Summary ==========\n");

        System.out.println("Total Reservations: 5");
        System.out.println("Reservations with Add-On Services: " + countReservationsWithServices(serviceManager));
        System.out.println();

        double totalAdditionalRevenue = calculateTotalAdditionalRevenue(
                serviceManager,
                new String[]{reservation1Id, reservation2Id, reservation3Id, reservation4Id, reservation5Id}
        );

        System.out.println("Total Additional Revenue from Services: $" + String.format("%.2f", totalAdditionalRevenue));
        System.out.println();

        // Display key concepts
        System.out.println("========== Key Data Structure Concepts ==========\n");
        System.out.println("1. Business Extensibility:");
        System.out.println("   - Services can be added independently without modifying core booking logic");
        System.out.println();
        System.out.println("2. One-to-Many Relationship:");
        System.out.println("   - One reservation can have multiple associated services");
        System.out.println("   - Represented using Map<String, List<AddOnService>>");
        System.out.println();
        System.out.println("3. Composition over Inheritance:");
        System.out.println("   - Services are composed with reservations, not inherited");
        System.out.println("   - Enables flexible feature growth");
        System.out.println();
        System.out.println("4. Separation of Concerns:");
        System.out.println("   - Add-on logic is independent of room allocation and inventory");
        System.out.println("   - Core booking workflow remains unchanged");
        System.out.println();
        System.out.println("5. Cost Aggregation:");
        System.out.println("   - Service costs calculated separately and combined for pricing");
        System.out.println("   - Keeps pricing logic modular and extensible");
        System.out.println();
    }

    /**
     * Display reservation details with selected services and total cost.
     *
     * @param reservationId Unique identifier for the reservation
     * @param guestName Name of the guest
     * @param baseRoomCost Base cost of the room
     * @param serviceManager Manager for add-on services
     */
    private static void displayReservationWithServices(
            String reservationId,
            String guestName,
            double baseRoomCost,
            AddOnServiceManager serviceManager) {

        System.out.println("Reservation ID: " + reservationId + " | Guest: " + guestName);
        System.out.println("  Base Room Cost: $" + String.format("%.2f", baseRoomCost));

        List<AddOnService> services = serviceManager.getServicesForReservation(reservationId);

        if (services.isEmpty()) {
            System.out.println("  Add-On Services: None");
            System.out.println("  Total Additional Cost: $0.00");
        } else {
            System.out.println("  Add-On Services:");
            for (AddOnService service : services) {
                System.out.println("    - " + service.getServiceName() + ": $" + String.format("%.2f", service.getServiceCost()));
            }

            double additionalCost = serviceManager.calculateTotalServiceCost(reservationId);
            System.out.println("  Total Additional Cost: $" + String.format("%.2f", additionalCost));
        }

        double totalCost = baseRoomCost + serviceManager.calculateTotalServiceCost(reservationId);
        System.out.println("  *** Total Reservation Cost: $" + String.format("%.2f", totalCost) + " ***");
        System.out.println();
    }

    /**
     * Count reservations that have at least one service.
     *
     * @param serviceManager Manager for add-on services
     * @return Count of reservations with services
     */
    private static int countReservationsWithServices(AddOnServiceManager serviceManager) {
        int count = 0;
        for (String resId : new String[]{"RES001", "RES002", "RES003", "RES004", "RES005"}) {
            if (serviceManager.hasServices(resId)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Calculate total additional revenue from all services across reservations.
     *
     * @param serviceManager Manager for add-on services
     * @param reservationIds Array of reservation IDs
     * @return Total additional revenue
     */
    private static double calculateTotalAdditionalRevenue(
            AddOnServiceManager serviceManager,
            String[] reservationIds) {

        double totalRevenue = 0.0;
        for (String resId : reservationIds) {
            totalRevenue += serviceManager.calculateTotalServiceCost(resId);
        }
        return totalRevenue;
    }
}