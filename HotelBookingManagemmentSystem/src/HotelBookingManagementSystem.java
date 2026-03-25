import java.util.HashMap;
import java.util.Map;

/**
 * Hotel Booking Management System - Use Case 3: Centralized Room Inventory Management
 *
 * This class serves as the application entry point for demonstrating centralized inventory management.
 * It showcases how a HashMap data structure solves the problem of scattered state management,
 * providing a single source of truth for room availability across the system.
 *
 * @author IndAdityaSingh
 * @version 3.0
 * @since 2026-03-25
 */
public class HotelBookingManagementSystem {

    /**
     * Main method - Entry point for the Hotel Booking Management System.
     * Initializes the inventory system and demonstrates centralized room availability management.
     *
     * @param args Command-line arguments (not used in this use case)
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Hotel Booking Management System - Use Case 3");
        System.out.println("Centralized Room Inventory Management");
        System.out.println("========================================\n");

        // Initialize the room inventory system
        RoomInventory inventory = new RoomInventory();

        // Register room types with their available counts
        inventory.registerRoom("Single Room", 5);
        inventory.registerRoom("Double Room", 3);
        inventory.registerRoom("Suite Room", 2);

        // Display current inventory
        System.out.println("Initial Room Inventory:\n");
        inventory.displayInventory();
        System.out.println();

        // Demonstrate inventory operations
        System.out.println("========== Inventory Operations ==========\n");

        // Check availability
        System.out.println("Checking availability of Single Room: " +
                inventory.getAvailableRooms("Single Room") + " rooms available\n");

        // Book a single room
        System.out.println("Booking 1 Single Room...");
        inventory.bookRoom("Single Room", 1);
        System.out.println("Single Room available after booking: " +
                inventory.getAvailableRooms("Single Room") + " rooms\n");

        // Book multiple double rooms
        System.out.println("Booking 2 Double Rooms...");
        inventory.bookRoom("Double Room", 2);
        System.out.println("Double Room available after booking: " +
                inventory.getAvailableRooms("Double Room") + " rooms\n");

        // Release a suite room
        System.out.println("Cancelling 1 Suite Room booking...");
        inventory.releaseRoom("Suite Room", 1);
        System.out.println("Suite Room available after cancellation: " +
                inventory.getAvailableRooms("Suite Room") + " rooms\n");

        // Display updated inventory
        System.out.println("========== Updated Room Inventory ==========\n");
        inventory.displayInventory();

        System.out.println("\n========================================");
        System.out.println("Application terminated successfully!");
    }
}

/**
 * RoomInventory class - Manages centralized room availability.
 *
 * This class encapsulates all inventory-related operations using a HashMap data structure.
 * It provides a single source of truth for room availability, eliminating the scattered state
 * management problem from previous use cases. All availability data is stored and accessed
 * through controlled methods, ensuring consistency and scalability.
 *
 * @author IndAdityaSingh
 * @version 3.0
 * @since 2026-03-25
 */
class RoomInventory {

    /**
     * HashMap to store room types and their available counts.
     * Key: Room type name (String)
     * Value: Number of available rooms (Integer)
     *
     * This centralized structure provides O(1) average-case lookup and update operations.
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
     * This method adds a new entry to the inventory map or updates an existing one.
     *
     * @param roomType The type of room (e.g., "Single Room", "Double Room")
     * @param availableCount The number of rooms available for this type
     */
    public void registerRoom(String roomType, int availableCount) {
        inventoryMap.put(roomType, availableCount);
        System.out.println("Registered: " + roomType + " with " + availableCount + " rooms available");
    }

    /**
     * Retrieves the current availability of a specific room type.
     *
     * @param roomType The type of room
     * @return The number of available rooms of that type, or 0 if room type not found
     */
    public int getAvailableRooms(String roomType) {
        return inventoryMap.getOrDefault(roomType, 0);
    }

    /**
     * Books one or more rooms of a specified type.
     * Reduces the availability count by the number of rooms booked.
     *
     * @param roomType The type of room to book
     * @param quantity The number of rooms to book
     */
    public void bookRoom(String roomType, int quantity) {
        if (inventoryMap.containsKey(roomType)) {
            int currentAvailability = inventoryMap.get(roomType);
            if (currentAvailability >= quantity) {
                inventoryMap.put(roomType, currentAvailability - quantity);
            } else {
                System.out.println("Error: Only " + currentAvailability + " " + roomType +
                        " available. Cannot book " + quantity);
            }
        } else {
            System.out.println("Error: Room type '" + roomType + "' not found in inventory");
        }
    }

    /**
     * Releases one or more rooms of a specified type (cancellation).
     * Increases the availability count by the number of rooms released.
     *
     * @param roomType The type of room to release
     * @param quantity The number of rooms to release
     */
    public void releaseRoom(String roomType, int quantity) {
        if (inventoryMap.containsKey(roomType)) {
            int currentAvailability = inventoryMap.get(roomType);
            inventoryMap.put(roomType, currentAvailability + quantity);
        } else {
            System.out.println("Error: Room type '" + roomType + "' not found in inventory");
        }
    }

    /**
     * Displays the current state of the entire inventory.
     * Iterates through all room types and their availability counts.
     */
    public void displayInventory() {
        System.out.println("Current Inventory Status:");
        System.out.println("--------------------------");

        for (Map.Entry<String, Integer> entry : inventoryMap.entrySet()) {
            String roomType = entry.getKey();
            int availableCount = entry.getValue();
            System.out.println("  " + roomType + ": " + availableCount + " rooms available");
        }
    }

    /**
     * Returns the total number of room types registered in the inventory.
     *
     * @return The number of distinct room types
     */
    public int getRoomTypeCount() {
        return inventoryMap.size();
    }

    /**
     * Checks if a specific room type exists in the inventory.
     *
     * @param roomType The type of room to check
     * @return true if room type exists, false otherwise
     */
    public boolean roomTypeExists(String roomType) {
        return inventoryMap.containsKey(roomType);
    }
}