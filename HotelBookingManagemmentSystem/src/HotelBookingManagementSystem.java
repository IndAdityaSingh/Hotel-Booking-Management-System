import java.util.HashMap;
import java.util.Map;

/**
 * Hotel Booking Management System - Use Case 4: Room Search & Availability Check
 *
 * This class serves as the application entry point for demonstrating room search functionality.
 * It showcases how to implement read-only access to inventory and room information without
 * modifying system state, emphasizing safe data access and clear separation of responsibilities.
 *
 * @author IndAdityaSingh
 * @version 4.0
 * @since 2026-03-25
 */
public class HotelBookingManagementSystem {

    /**
     * Main method - Entry point for the Hotel Booking Management System.
     * Initializes the system with rooms and inventory, then demonstrates search operations.
     *
     * @param args Command-line arguments (not used in this use case)
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Hotel Booking Management System - Use Case 4");
        System.out.println("Room Search & Availability Check");
        System.out.println("========================================\n");

        // Initialize room inventory
        RoomInventory inventory = new RoomInventory();
        inventory.registerRoom("Single Room", 5);
        inventory.registerRoom("Double Room", 3);
        inventory.registerRoom("Suite Room", 2);

        // Initialize room catalog with domain models
        RoomCatalog roomCatalog = new RoomCatalog();
        roomCatalog.addRoom("Single Room", new SingleRoom("SR001", 1, 200.0));
        roomCatalog.addRoom("Double Room", new DoubleRoom("DR001", 2, 350.0));
        roomCatalog.addRoom("Suite Room", new SuiteRoom("SU001", 2, 500.0));

        System.out.println("Current Inventory Status:");
        inventory.displayInventory();
        System.out.println();

        // Create search service
        RoomSearchService searchService = new RoomSearchService(inventory, roomCatalog);

        // Perform room search - read-only operation
        System.out.println("========== Guest Room Search ==========\n");
        System.out.println("Guest initiates room search request...\n");
        search*
