import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Custom exception for invalid booking scenarios.
 * Provides clear error messages for domain-specific booking failures.
 */
class InvalidBookingException extends Exception {

    /**
     * Constructor with error message.
     *
     * @param message Description of the invalid booking scenario
     */
    public InvalidBookingException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause.
     *
     * @param message Description of the invalid booking scenario
     * @param cause The underlying exception that caused this error
     */
    public InvalidBookingException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Custom exception for invalid inventory operations.
 * Raised when inventory constraints are violated.
 */
class InvalidInventoryException extends Exception {

    /**
     * Constructor with error message.
     *
     * @param message Description of the inventory violation
     */
    public InvalidInventoryException(String message) {
        super(message);
    }

    /**
     * Constructor with message and cause.
     *
     * @param message Description of the inventory violation
     * @param cause The underlying exception that caused this error
     */
    public InvalidInventoryException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Validates booking requests and system state.
 * Implements input validation and fail-fast design patterns.
 *
 * Key Responsibilities:
 * - Validate guest names
 * - Validate room types against allowed values
 * - Validate number of nights
 * - Validate inventory constraints
 * - Provide clear error messages
 */
class BookingValidator {
    private static final Set<String> VALID_ROOM_TYPES = new HashSet<>();
    private static final int MIN_NIGHTS = 1;
    private static final int MAX_NIGHTS = 365;
    private static final int MIN_INVENTORY = 0;

    static {
        VALID_ROOM_TYPES.add("Single Room");
        VALID_ROOM_TYPES.add("Double Room");
        VALID_ROOM_TYPES.add("Suite Room");
    }

    /**
     * Validate guest name.
     *
     * @param guestName Name of the guest
     * @throws InvalidBookingException If guest name is invalid
     */
    public static void validateGuestName(String guestName) throws InvalidBookingException {
        if (guestName == null || guestName.trim().isEmpty()) {
            throw new InvalidBookingException("ERROR: Guest name cannot be null or empty");
        }

        if (guestName.length() < 2) {
            throw new InvalidBookingException("ERROR: Guest name must be at least 2 characters long");
        }

        if (guestName.length() > 100) {
            throw new InvalidBookingException("ERROR: Guest name cannot exceed 100 characters");
        }
    }

    /**
     * Validate room type against allowed values.
     * Validation is case-sensitive.
     *
     * @param roomType Type of room
     * @throws InvalidBookingException If room type is not valid
     */
    public static void validateRoomType(String roomType) throws InvalidBookingException {
        if (roomType == null || roomType.trim().isEmpty()) {
            throw new InvalidBookingException("ERROR: Room type cannot be null or empty");
        }

        if (!VALID_ROOM_TYPES.contains(roomType)) {
            throw new InvalidBookingException(
                    "ERROR: Invalid room type '" + roomType + "'. " +
                            "Valid room types are: Single Room, Double Room, Suite Room (case-sensitive)"
            );
        }
    }

    /**
     * Validate number of nights.
     *
     * @param numberOfNights Number of nights for the stay
     * @throws InvalidBookingException If number of nights is invalid
     */
    public static void validateNumberOfNights(int numberOfNights) throws InvalidBookingException {
        if (numberOfNights < MIN_NIGHTS) {
            throw new InvalidBookingException(
                    "ERROR: Number of nights must be at least " + MIN_NIGHTS
            );
        }

        if (numberOfNights > MAX_NIGHTS) {
            throw new InvalidBookingException(
                    "ERROR: Number of nights cannot exceed " + MAX_NIGHTS
            );
        }
    }

    /**
     * Validate inventory availability.
     *
     * @param availableRooms Number of available rooms
     * @throws InvalidInventoryException If inventory count is invalid
     */
    public static void validateInventoryAvailability(int availableRooms) throws InvalidInventoryException {
        if (availableRooms < MIN_INVENTORY) {
            throw new InvalidInventoryException(
                    "ERROR: Inventory count cannot be negative. Current: " + availableRooms
            );
        }
    }

    /**
     * Validate that sufficient inventory exists for booking.
     *
     * @param availableRooms Current available rooms
     * @param requestedRooms Number of rooms requested
     * @throws InvalidBookingException If insufficient inventory
     */
    public static void validateSufficientInventory(int availableRooms, int requestedRooms)
            throws InvalidBookingException {
        if (requestedRooms <= 0) {
            throw new InvalidBookingException(
                    "ERROR: Requested rooms must be greater than 0. Requested: " + requestedRooms
            );
        }

        if (availableRooms < requestedRooms) {
            throw new InvalidBookingException(
                    "ERROR: Insufficient inventory. Available: " + availableRooms +
                            ", Requested: " + requestedRooms
            );
        }
    }

    /**
     * Validate booking object state.
     *
     * @param guestName Name of the guest
     * @param roomType Type of room
     * @param numberOfNights Number of nights
     * @throws InvalidBookingException If any validation fails
     */
    public static void validateBookingInput(String guestName, String roomType, int numberOfNights)
            throws InvalidBookingException {
        validateGuestName(guestName);
        validateRoomType(roomType);
        validateNumberOfNights(numberOfNights);
    }

    /**
     * Get list of valid room types.
     *
     * @return Set of valid room type strings
     */
    public static Set<String> getValidRoomTypes() {
        return new HashSet<>(VALID_ROOM_TYPES);
    }
}

/**
 * Manages room inventory with validation and error handling.
 * Implements guarding system state pattern to prevent invalid transitions.
 *
 * Key Responsibilities:
 * - Store room inventory counts
 * - Validate operations before state changes
 * - Throw exceptions on constraint violations
 * - Maintain consistency after errors
 */
class SafeInventoryManager {
    private Map<String, Integer> roomInventory;

    /**
     * Constructor initializes empty inventory.
     */
    public SafeInventoryManager() {
        this.roomInventory = new HashMap<>();
    }

    /**
     * Register a room type with initial inventory.
     *
     * @param roomType Type of room
     * @param initialCount Initial number of rooms available
     * @throws InvalidInventoryException If parameters are invalid
     */
    public void registerRoom(String roomType, int initialCount) throws InvalidInventoryException {
        try {
            BookingValidator.validateRoomType(roomType);
        } catch (InvalidBookingException e) {
            throw new InvalidInventoryException("Cannot register room: " + e.getMessage());
        }

        if (initialCount < 0) {
            throw new InvalidInventoryException(
                    "ERROR: Initial inventory count cannot be negative. Provided: " + initialCount
            );
        }

        roomInventory.put(roomType, initialCount);
        System.out.println("✓ Registered: " + roomType + " - " + initialCount + " rooms");
    }

    /**
     * Get available inventory for a room type.
     *
     * @param roomType Type of room
     * @return Number of available rooms, or 0 if room type not registered
     */
    public int getAvailableRooms(String roomType) {
        return roomInventory.getOrDefault(roomType, 0);
    }

    /**
     * Allocate rooms from inventory (decrease available count).
     * Validates before state change (guarding).
     *
     * @param roomType Type of room
     * @param quantity Number of rooms to allocate
     * @throws InvalidBookingException If allocation would violate constraints
     * @throws InvalidInventoryException If inventory state is invalid
     */
    public void allocateRooms(String roomType, int quantity)
            throws InvalidBookingException, InvalidInventoryException {
        try {
            BookingValidator.validateRoomType(roomType);
        } catch (InvalidBookingException e) {
            throw new InvalidBookingException("Cannot allocate rooms: " + e.getMessage());
        }

        if (!roomInventory.containsKey(roomType)) {
            throw new InvalidBookingException(
                    "ERROR: Room type '" + roomType + "' is not registered in inventory"
            );
        }

        int currentAvailable = getAvailableRooms(roomType);

        try {
            BookingValidator.validateInventoryAvailability(currentAvailable);
            BookingValidator.validateSufficientInventory(currentAvailable, quantity);
        } catch (InvalidBookingException | InvalidInventoryException e) {
            throw e;
        }

        // Guard: Check before updating state
        if (currentAvailable - quantity < 0) {
            throw new InvalidBookingException(
                    "ERROR: Cannot allocate " + quantity + " rooms. " +
                            "Available: " + currentAvailable + ", would result in negative inventory"
            );
        }

        // Safe to update state
        roomInventory.put(roomType, currentAvailable - quantity);
        System.out.println("✓ Allocated " + quantity + " " + roomType +
                " (Remaining: " + roomInventory.get(roomType) + ")");
    }

    /**
     * Release rooms back to inventory (increase available count).
     * Validates before state change (guarding).
     *
     * @param roomType Type of room
     * @param quantity Number of rooms to release
     * @throws InvalidInventoryException If operation would violate constraints
     */
    public void releaseRooms(String roomType, int quantity) throws InvalidInventoryException {
        try {
            BookingValidator.validateRoomType(roomType);
        } catch (InvalidBookingException e) {
            throw new InvalidInventoryException("Cannot release rooms: " + e.getMessage());
        }

        if (!roomInventory.containsKey(roomType)) {
            throw new InvalidInventoryException(
                    "ERROR: Room type '" + roomType + "' is not registered in inventory"
            );
        }

        if (quantity < 0) {
            throw new InvalidInventoryException(
                    "ERROR: Release quantity cannot be negative. Provided: " + quantity
            );
        }

        int currentAvailable = getAvailableRooms(roomType);
        int newCount = currentAvailable + quantity;

        roomInventory.put(roomType, newCount);
        System.out.println("✓ Released " + quantity + " " + roomType +
                " (Available: " + newCount + ")");
    }

    /**
     * Display current inventory status.
     */
    public void displayInventory() {
        System.out.println("\n--- Current Inventory Status ---");
        if (roomInventory.isEmpty()) {
            System.out.println("No rooms registered in inventory");
            return;
        }

        for (Map.Entry<String, Integer> entry : roomInventory.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " available");
        }
    }
}

/**
 * Booking service with comprehensive validation and error handling.
 * Implements fail-fast design to detect errors early.
 *
 * Key Responsibilities:
 * - Validate booking requests
 * - Process bookings with error handling
 * - Report errors clearly
 * - Maintain system stability
 */
class ValidatedBookingService {
    private SafeInventoryManager inventory;

    /**
     * Constructor accepts inventory manager.
     *
     * @param inventory The inventory manager
     */
    public ValidatedBookingService(SafeInventoryManager inventory) {
        this.inventory = inventory;
    }

    /**
     * Process a booking request with full validation.
     * Implements fail-fast by validating before state changes.
     *
     * @param guestName Name of the guest
     * @param roomType Type of room requested
     * @param numberOfNights Number of nights
     * @return true if booking successful, false otherwise
     */
    public boolean processBooking(String guestName, String roomType, int numberOfNights) {
        System.out.println("\n--- Processing Booking Request ---");
        System.out.println("Guest: " + guestName);
        System.out.println("Room: " + roomType);
        System.out.println("Nights: " + numberOfNights);

        try {
            // Step 1: Validate input (fail-fast)
            System.out.println("\n[Step 1] Validating input...");
            BookingValidator.validateBookingInput(guestName, roomType, numberOfNights);
            System.out.println("✓ Input validation passed");

            // Step 2: Check inventory availability (fail-fast)
            System.out.println("[Step 2] Checking inventory...");
            int availableRooms = inventory.getAvailableRooms(roomType);
            System.out.println("  Available rooms: " + availableRooms);

            BookingValidator.validateSufficientInventory(availableRooms, 1);
            System.out.println("✓ Sufficient inventory available");

            // Step 3: Allocate rooms (state change after validation)
            System.out.println("[Step 3] Allocating room...");
            inventory.allocateRooms(roomType, 1);

            // Step 4: Confirm booking
            System.out.println("[Step 4] Confirming booking...");
            System.out.println("✓ BOOKING CONFIRMED");
            System.out.println("  Reservation ID: " + generateReservationId());
            System.out.println("  Total Cost: $" + String.format("%.2f", numberOfNights * getRoomRate(roomType)));

            return true;

        } catch (InvalidBookingException e) {
            System.out.println("✗ BOOKING FAILED");
            System.out.println("  Reason: " + e.getMessage());
            System.out.println("  Status: Request rejected - no state changes made");
            return false;

        } catch (InvalidInventoryException e) {
            System.out.println("✗ BOOKING FAILED");
            System.out.println("  Reason: " + e.getMessage());
            System.out.println("  Status: Inventory constraint violated");
            return false;

        } catch (Exception e) {
            System.out.println("✗ BOOKING FAILED - Unexpected Error");
            System.out.println("  Reason: " + e.getMessage());
            System.out.println("  Status: System remains in stable state");
            return false;
        }
    }

    /**
     * Generate a reservation ID.
     */
    private String generateReservationId() {
        return "RES" + System.currentTimeMillis();
    }

    /**
     * Get room rate based on type.
     */
    private double getRoomRate(String roomType) {
        switch (roomType) {
            case "Single Room":
                return 100.0;
            case "Double Room":
                return 150.0;
            case "Suite Room":
                return 250.0;
            default:
                return 100.0;
        }
    }
}

/**
 * Hotel Booking Management System - Use Case 9: Error Handling & Validation
 *
 * This class demonstrates structured validation, custom exceptions, and error handling
 * to strengthen system reliability and prevent invalid states.
 *
 * Key Concepts:
 * - Input Validation: Validates incoming data before processing
 * - Custom Exceptions: Domain-specific exceptions for clear error handling
 * - Fail-Fast Design: Detects errors early and stops further processing
 * - Guarding System State: Validates before state changes
 * - Graceful Failure: Errors handled clearly without crashing
 * - Correctness over Happy Path: Handles invalid usage patterns
 *
 * @author IndAdityaSingh
 * @version 9.0
 * @since 2026-03-25
 */
public class HotelBookingManagementSystem {

    /**
     * Main method - Entry point for Use Case 9.
     * Demonstrates validation and error handling.
     *
     * @param args Command-line arguments (not used in this use case)
     */
    public static void main(String[] args) {
        System.out.println("================================================");
        System.out.println("Hotel Booking Management System - Use Case 9");
        System.out.println("Error Handling & Validation");
        System.out.println("================================================\n");

        // Initialize inventory manager
        SafeInventoryManager inventory = new SafeInventoryManager();

        // Initialize booking service
        ValidatedBookingService bookingService = new ValidatedBookingService(inventory);

        System.out.println("========== Setting Up Inventory ==========\n");

        try {
            inventory.registerRoom("Single Room", 5);
            inventory.registerRoom("Double Room", 3);
            inventory.registerRoom("Suite Room", 2);
        } catch (InvalidInventoryException e) {
            System.out.println("✗ Failed to register room: " + e.getMessage());
        }

        inventory.displayInventory();

        System.out.println("\n========== Test Case 1: Valid Booking ==========");
        bookingService.processBooking("John Doe", "Double Room", 2);
        inventory.displayInventory();

        System.out.println("\n========== Test Case 2: Invalid Room Type (case-sensitive) ==========");
        bookingService.processBooking("Jane Smith", "double room", 1);

        System.out.println("\n========== Test Case 3: Empty Guest Name ==========");
        bookingService.processBooking("", "Single Room", 1);

        System.out.println("\n========== Test Case 4: Invalid Number of Nights ==========");
        bookingService.processBooking("Alice Johnson", "Suite Room", 0);

        System.out.println("\n========== Test Case 5: Insufficient Inventory ==========");
        // First book all Suite Rooms
        bookingService.processBooking("Bob Wilson", "Suite Room", 1);
        bookingService.processBooking("Carol Davis", "Suite Room", 1);

        // Now try to book when none available
        bookingService.processBooking("David Lee", "Suite Room", 1);

        System.out.println("\n========== Test Case 6: Valid Bookings with Error Recovery ==========");
        // Try an invalid booking
        bookingService.processBooking("Emma Martinez", "Invalid Room", 1);

        // Then continue with valid bookings
        bookingService.processBooking("Frank Brown", "Single Room", 1);
        inventory.displayInventory();

        System.out.println("\n========== Test Case 7: Null Guest Name ==========");
        bookingService.processBooking(null, "Double Room", 1);

        System.out.println("\n========== Test Case 8: Negative Night Count ==========");
        bookingService.processBooking("Grace Wilson", "Single Room", -2);

        System.out.println("\n========== Test Case 9: Excessive Nights ==========");
        bookingService.processBooking("Henry Taylor", "Single Room", 400);

        System.out.println("\n========== Test Case 10: Very Long Guest Name ==========");
        String longName = "A".repeat(150);
        bookingService.processBooking(longName, "Double Room", 1);

        System.out.println("\n========== Valid Bookings Resume ==========");
        bookingService.processBooking("Isaac Newton", "Single Room", 3);
        inventory.displayInventory();

        System.out.println("\n========== Key Design Patterns Demonstrated ==========\n");
        System.out.println("1. Input Validation:");
        System.out.println("   - All inputs validated before processing");
        System.out.println("   - Prevents invalid data from entering system");
        System.out.println();
        System.out.println("2. Custom Exceptions:");
        System.out.println("   - InvalidBookingException: For booking-specific errors");
        System.out.println("   - InvalidInventoryException: For inventory constraint violations");
        System.out.println("   - Makes error causes explicit and clear");
        System.out.println();
        System.out.println("3. Fail-Fast Design:");
        System.out.println("   - Errors detected as early as possible");
        System.out.println("   - Processing stops immediately on validation failure");
        System.out.println("   - Prevents cascading failures");
        System.out.println();
        System.out.println("4. Guarding System State:");
        System.out.println("   - All constraints checked before state changes");
        System.out.println("   - Inventory updates only after successful validation");
        System.out.println("   - Ensures consistency of critical data");
        System.out.println();
        System.out.println("5. Graceful Failure Handling:");
        System.out.println("   - Errors caught and handled with clear messages");
        System.out.println("   - Application continues running safely");
        System.out.println("   - No partial or corrupted state transitions");
        System.out.println();
        System.out.println("6. Correctness over Happy Path:");
        System.out.println("   - System handles invalid usage patterns");
        System.out.println("   - Case-sensitive validation (e.g., 'double room' vs 'Double Room')");
        System.out.println("   - Reflects real-world conditions");
        System.out.println();
        System.out.println("7. Validation Rules Demonstrated:");
        System.out.println("   - Guest name: 2-100 characters");
        System.out.println("   - Room type: Must be exact case match from valid set");
        System.out.println("   - Number of nights: 1-365 days");
        System.out.println("   - Inventory: Cannot be negative");
        System.out.println();

        System.out.println("✓ Use Case 9 Completed Successfully");
        System.out.println("✓ System Stability Maintained Throughout Error Cases");
    }
}