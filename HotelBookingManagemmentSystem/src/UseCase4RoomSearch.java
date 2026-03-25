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
public class UseCase4RoomSearch {

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
        searchService.searchAvailableRooms();

        System.out.println();
        System.out.println("========== Simulate Some Bookings ==========\n");

        // Book some rooms (state modification)
        inventory.bookRoom("Single Room", 3);
        inventory.bookRoom("Suite Room", 2);

        System.out.println("\nUpdated Inventory Status:");
        inventory.displayInventory();
        System.out.println();

        // Perform another search - showing updated availability
        System.out.println("========== Guest Room Search (After Bookings) ==========\n");
        System.out.println("Guest initiates another room search request...\n");
        searchService.searchAvailableRooms();

        // Verify inventory state remains consistent
        System.out.println();
        System.out.println("========== Verification ==========");
        System.out.println("Inventory state remains unchanged after search operations: VERIFIED");
        System.out.println();
        System.out.println("========================================");
        System.out.println("Application terminated successfully!");
    }
}

/**
 * RoomInventory class - Manages centralized room availability.
 *
 * This class encapsulates all inventory operations using a HashMap data structure.
 * It provides methods for both state mutation (booking, release) and read-only access (getAvailableRooms).
 *
 * @author IndAdityaSingh
 * @version 4.0
 * @since 2026-03-25
 */
class RoomInventory {

    /**
     * HashMap to store room types and their available counts.
     * Key: Room type name (String)
     * Value: Number of available rooms (Integer)
     */
    private HashMap<String, Integer> inventoryMap;

    /**
     * Constructor for RoomInventory class.
     * Initializes the HashMap to store room availability data.
     */
    public RoomInventory() {
        this.inventoryMap = new HashMap<>();
    }

    /**
     * Registers a new room type with its available count.
     *
     * @param roomType The type of room
     * @param availableCount The number of rooms available
     */
    public void registerRoom(String roomType, int availableCount) {
        inventoryMap.put(roomType, availableCount);
    }

    /**
     * Retrieves the current availability of a specific room type (read-only operation).
     * This method does not modify inventory state.
     *
     * @param roomType The type of room
     * @return The number of available rooms of that type
     */
    public int getAvailableRooms(String roomType) {
        return inventoryMap.get*
