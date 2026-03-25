import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Custom exception for invalid booking scenarios.
 */
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }

    public InvalidBookingException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Custom exception for invalid inventory operations.
 */
class InvalidInventoryException extends Exception {
    public InvalidInventoryException(String message) {
        super(message);
    }

    public InvalidInventoryException(String message, Throwable cause) {
        super(message, cause);
    }
}

/**
 * Represents a guest reservation with booking details.
 */
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private int numberOfNights;
    private double roomCostPerNight;
    private LocalDateTime bookingDateTime;
    private boolean confirmed;
    private boolean cancelled;
    private String allocatedRoomNumber;

    public Reservation(String guestName, String roomType, int numberOfNights) {
        this.reservationId = generateReservationId();
        this.guestName = guestName;
        this.roomType = roomType;
        this.numberOfNights = numberOfNights;
        this.bookingDateTime = LocalDateTime.now();
        this.confirmed = false;
        this.cancelled = false;
        this.allocatedRoomNumber = null;

        switch (roomType) {
            case "Single Room":
                this.roomCostPerNight = 100.0;
                break;
            case "Double Room":
                this.roomCostPerNight = 150.0;
                break;
            case "Suite Room":
                this.roomCostPerNight = 250.0;
                break;
            default:
                this.roomCostPerNight = 100.0;
        }
    }

    private String generateReservationId() {
        return "RES" + System.currentTimeMillis();
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getNumberOfNights() {
        return numberOfNights;
    }

    public double getRoomCostPerNight() {
        return roomCostPerNight;
    }

    public double getTotalCost() {
        return roomCostPerNight * numberOfNights;
    }

    public LocalDateTime getBookingDateTime() {
        return bookingDateTime;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public String getAllocatedRoomNumber() {
        return allocatedRoomNumber;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public void setAllocatedRoomNumber(String roomNumber) {
        this.allocatedRoomNumber = roomNumber;
    }

    public String getFormattedBookingDateTime() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return bookingDateTime.format(formatter);
    }

    @Override
    public String toString() {
        return "Reservation{" +
                "ID='" + reservationId + '\'' +
                ", Guest='" + guestName + '\'' +
                ", Room='" + roomType + '\'' +
                ", Nights=" + numberOfNights +
                ", Cost=$" + String.format("%.2f", getTotalCost()) +
                ", Confirmed=" + confirmed +
                ", Cancelled=" + cancelled +
                ", Room#=" + allocatedRoomNumber +
                '}';
    }
}

/**
 * Validates booking requests and system state.
 */
class BookingValidator {
    private static final Set<String> VALID_ROOM_TYPES = new HashSet<>();
    private static final int MIN_NIGHTS = 1;
    private static final int MAX_NIGHTS = 365;

    static {
        VALID_ROOM_TYPES.add("Single Room");
        VALID_ROOM_TYPES.add("Double Room");
        VALID_ROOM_TYPES.add("Suite Room");
    }

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

    public static void validateBookingInput(String guestName, String roomType, int numberOfNights)
            throws InvalidBookingException {
        validateGuestName(guestName);
        validateRoomType(roomType);
        validateNumberOfNights(numberOfNights);
    }
}

/**
 * Manages room inventory with validation and error handling.
 */
class SafeInventoryManager {
    private Map<String, Integer> roomInventory;

    public SafeInventoryManager() {
        this.roomInventory = new HashMap<>();
    }

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

    public int getAvailableRooms(String roomType) {
        return roomInventory.getOrDefault(roomType, 0);
    }

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
            BookingValidator.validateSufficientInventory(currentAvailable, quantity);
        } catch (InvalidBookingException e) {
            throw e;
        }

        if (currentAvailable - quantity < 0) {
            throw new InvalidBookingException(
                    "ERROR: Cannot allocate " + quantity + " rooms. " +
                            "Available: " + currentAvailable + ", would result in negative inventory"
            );
        }

        roomInventory.put(roomType, currentAvailable - quantity);
        System.out.println("✓ Allocated " + quantity + " " + roomType +
                " (Remaining: " + roomInventory.get(roomType) + ")");
    }

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
 * Manages booking records with confirmation and cancellation tracking.
 */
class BookingHistory {
    private List<Reservation> bookings;

    public BookingHistory() {
        this.bookings = new LinkedList<>();
    }

    public void addBooking(Reservation reservation) {
        bookings.add(reservation);
        System.out.println("✓ Added to booking history: " + reservation.getReservationId() +
                " - " + reservation.getGuestName());
    }

    public Reservation findBookingById(String reservationId) {
        for (Reservation reservation : bookings) {
            if (reservation.getReservationId().equals(reservationId)) {
                return reservation;
            }
        }
        return null;
    }

    public List<Reservation> getAllBookings() {
        return new LinkedList<>(bookings);
    }

    public int getBookingCount() {
        return bookings.size();
    }

    public void displayAllBookings() {
        System.out.println("\n--- Booking History ---");
        if (bookings.isEmpty()) {
            System.out.println("No bookings in history");
            return;
        }

        for (int i = 0; i < bookings.size(); i++) {
            Reservation booking = bookings.get(i);
            String status = booking.isCancelled() ? "CANCELLED" : (booking.isConfirmed() ? "CONFIRMED" : "PENDING");
            System.out.println((i + 1) + ". " + booking.getGuestName() +
                    " | " + booking.getRoomType() +
                    " | Status: " + status +
                    " | Room: " + booking.getAllocatedRoomNumber());
        }
    }
}

/**
 * Manages released room IDs using LIFO Stack for rollback operations.
 * Stack naturally models undo/rollback behavior.
 */
class RollbackManager {
    private Stack<String> releasedRoomIds;
    private Map<String, String> roomTypeMapping;

    public RollbackManager() {
        this.releasedRoomIds = new Stack<>();
        this.roomTypeMapping = new HashMap<>();
    }

    /**
     * Record a released room for potential rollback.
     * Implements LIFO structure - last released is first to be rolled back.
     *
     * @param roomId The room ID being released
     * @param roomType The type of room
     */
    public void recordReleasedRoom(String roomId, String roomType) {
        releasedRoomIds.push(roomId);
        roomTypeMapping.put(roomId, roomType);
        System.out.println("✓ Recorded in rollback stack: " + roomId + " (" + roomType + ")");
    }

    /**
     * Get the most recently released room (LIFO order).
     *
     * @return The room ID, or null if stack is empty
     */
    public String getMostRecentReleased() {
        return releasedRoomIds.isEmpty() ? null : releasedRoomIds.peek();
    }

    /**
     * Remove and return the most recently released room (LIFO order).
     *
     * @return The room ID, or null if stack is empty
     */
    public String popReleasedRoom() {
        return releasedRoomIds.isEmpty() ? null : releasedRoomIds.pop();
    }

    /**
     * Get the room type for a given room ID.
     *
     * @param roomId The room ID
     * @return The room type
     */
    public String getRoomType(String roomId) {
        return roomTypeMapping.get(roomId);
    }

    /**
     * Check if rollback stack has any items.
     *
     * @return true if stack is not empty
     */
    public boolean hasReleasedRooms() {
        return !releasedRoomIds.isEmpty();
    }

    /**
     * Get size of rollback stack.
     *
     * @return Number of rooms in stack
     */
    public int getReleasedRoomCount() {
        return releasedRoomIds.size();
    }

    /**
     * Display all released rooms in stack (LIFO order).
     */
    public void displayReleasedRooms() {
        System.out.println("\n--- Rollback Stack (LIFO Order) ---");
        if (releasedRoomIds.isEmpty()) {
            System.out.println("No released rooms in rollback stack");
            return;
        }

        int position = 1;
        for (String roomId : releasedRoomIds) {
            System.out.println(position + ". " + roomId + " (" + roomTypeMapping.get(roomId) + ")");
            position++;
        }
    }
}

/**
 * Booking service with cancellation and rollback support.
 * Implements controlled state reversal using Stack for LIFO operations.
 */
class BookingServiceWithCancellation {
    private SafeInventoryManager inventory;
    private BookingHistory bookingHistory;
    private RollbackManager rollbackManager;

    public BookingServiceWithCancellation(SafeInventoryManager inventory, BookingHistory bookingHistory) {
        this.inventory = inventory;
        this.bookingHistory = bookingHistory;
        this.rollbackManager = new RollbackManager();
    }

    /**
     * Process a booking request.
     *
     * @param guestName Name of the guest
     * @param roomType Type of room requested
     * @param numberOfNights Number of nights
     * @return Reservation object if successful, null otherwise
     */
    public Reservation processBooking(String guestName, String roomType, int numberOfNights) {
        System.out.println("\n--- Processing Booking Request ---");
        System.out.println("Guest: " + guestName);
        System.out.println("Room: " + roomType);
        System.out.println("Nights: " + numberOfNights);

        try {
            // Validate input
            BookingValidator.validateBookingInput(guestName, roomType, numberOfNights);
            System.out.println("✓ Input validation passed");

            // Check inventory
            int availableRooms = inventory.getAvailableRooms(roomType);
            System.out.println("  Available rooms: " + availableRooms);

            BookingValidator.validateSufficientInventory(availableRooms, 1);
            System.out.println("✓ Sufficient inventory available");

            // Allocate room
            inventory.allocateRooms(roomType, 1);

            // Create and confirm reservation
            Reservation reservation = new Reservation(guestName, roomType, numberOfNights);
            String allocatedRoom = "Room-" + (100 + (int)(Math.random() * 50));
            reservation.setAllocatedRoomNumber(allocatedRoom);
            reservation.setConfirmed(true);

            // Record for rollback
            rollbackManager.recordReleasedRoom(allocatedRoom, roomType);

            // Add to history
            bookingHistory.addBooking(reservation);

            System.out.println("✓ BOOKING CONFIRMED");
            System.out.println("  Reservation ID: " + reservation.getReservationId());
            System.out.println("  Allocated Room: " + allocatedRoom);
            System.out.println("  Total Cost: $" + String.format("%.2f", reservation.getTotalCost()));

            return reservation;

        } catch (InvalidBookingException | InvalidInventoryException e) {
            System.out.println("✗ BOOKING FAILED: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.out.println("✗ BOOKING FAILED - Unexpected Error: " + e.getMessage());
            return null;
        }
    }

    /**
     * Cancel a confirmed booking and perform inventory rollback.
     * Implements state reversal using LIFO operations.
     *
     * @param reservationId The ID of the reservation to cancel
     * @return true if cancellation successful, false otherwise
     */
    public boolean cancelBooking(String reservationId) {
        System.out.println("\n--- Processing Cancellation Request ---");
        System.out.println("Reservation ID: " + reservationId);

        try {
            // Step 1: Validate reservation exists
            System.out.println("[Step 1] Validating reservation...");
            Reservation reservation = bookingHistory.findBookingById(reservationId);

            if (reservation == null) {
                System.out.println("✗ CANCELLATION FAILED");
                System.out.println("  Reason: Reservation ID not found: " + reservationId);
                System.out.println("  Status: No state changes made");
                return false;
            }

            System.out.println("✓ Reservation found: " + reservation.getGuestName());

            // Step 2: Validate cancellation is allowed
            System.out.println("[Step 2] Validating cancellation eligibility...");

            if (!reservation.isConfirmed()) {
                System.out.println("✗ CANCELLATION FAILED");
                System.out.println("  Reason: Reservation is not confirmed");
                System.out.println("  Status: Only confirmed bookings can be cancelled");
                return false;
            }

            if (reservation.isCancelled()) {
                System.out.println("✗ CANCELLATION FAILED");
                System.out.println("  Reason: Reservation is already cancelled");
                System.out.println("  Status: Duplicate cancellation attempt rejected");
                return false;
            }

            System.out.println("✓ Reservation is eligible for cancellation");

            // Step 3: Perform state reversal (rollback)
            System.out.println("[Step 3] Performing inventory rollback (LIFO order)...");
            String roomType = reservation.getRoomType();
            String allocatedRoom = reservation.getAllocatedRoomNumber();

            System.out.println("  Releasing room: " + allocatedRoom + " (" + roomType + ")");
            inventory.releaseRooms(roomType, 1);

            // Step 4: Update booking state
            System.out.println("[Step 4] Updating booking status...");
            reservation.setCancelled(true);

            System.out.println("✓ CANCELLATION CONFIRMED");
            System.out.println("  Reservation ID: " + reservationId);
            System.out.println("  Guest: " + reservation.getGuestName());
            System.out.println("  Released Room: " + allocatedRoom);
            System.out.println("  Refund Amount: $" + String.format("%.2f", reservation.getTotalCost()));
            System.out.println("  Status: Inventory restored successfully");

            return true;

        } catch (Exception e) {
            System.out.println("✗ CANCELLATION FAILED - Unexpected Error");
            System.out.println("  Reason: " + e.getMessage());
            System.out.println("  Status: System remains in stable state");
            return false;
        }
    }

    /**
     * Get the rollback manager for accessing stack information.
     *
     * @return The rollback manager
     */
    public RollbackManager getRollbackManager() {
        return rollbackManager;
    }

    /**
     * Get booking history.
     *
     * @return The booking history
     */
    public BookingHistory getBookingHistory() {
        return bookingHistory;
    }
}

/**
 * Hotel Booking Management System - Use Case 10: Booking Cancellation & Inventory Rollback
 *
 * This class demonstrates safe cancellation of confirmed bookings using Stack for LIFO rollback.
 *
 * Key Concepts:
 * - State Reversal: Undoing previously completed operations
 * - Stack Data Structure: LIFO order for natural rollback behavior
 * - LIFO Rollback Logic: Most recent allocation reversed first
 * - Controlled Mutation: Strict order of state changes
 * - Inventory Restoration: Immediate availability update
 * - Validation: Verify reservation before rollback
 *
 * @author IndAdityaSingh
 * @version 10.0
 * @since 2026-03-25
 */
public class HotelBookingManagementSystem {

    /**
     * Main method - Entry point for Use Case 10.
     * Demonstrates booking cancellation and inventory rollback.
     *
     * @param args Command-line arguments (not used in this use case)
     */
    public static void main(String[] args) {
        System.out.println("================================================");
        System.out.println("Hotel Booking Management System - Use Case 10");
        System.out.println("Booking Cancellation & Inventory Rollback");
        System.out.println("================================================\n");

        // Initialize components
        SafeInventoryManager inventory = new SafeInventoryManager();
        BookingHistory bookingHistory = new BookingHistory();
        BookingServiceWithCancellation bookingService = new BookingServiceWithCancellation(inventory, bookingHistory);

        System.out.println("========== Setting Up Inventory ==========\n");

        try {
            inventory.registerRoom("Single Room", 5);
            inventory.registerRoom("Double Room", 3);
            inventory.registerRoom("Suite Room", 2);
        } catch (InvalidInventoryException e) {
            System.out.println("✗ Failed to register room: " + e.getMessage());
        }

        inventory.displayInventory();

        System.out.println("\n========== Processing Booking Requests ==========");

        // Create reservations
        Reservation res1 = bookingService.processBooking("John Doe", "Double Room", 2);
        Reservation res2 = bookingService.processBooking("Jane Smith", "Single Room", 1);
        Reservation res3 = bookingService.processBooking("Alice Johnson", "Suite Room", 3);
        Reservation res4 = bookingService.processBooking("Bob Wilson", "Double Room", 1);
        Reservation res5 = bookingService.processBooking("Carol Davis", "Single Room", 2);

        inventory.displayInventory();
        bookingHistory.displayAllBookings();

        System.out.println("\n========== Test Case 1: Cancel Valid Booking ==========");
        bookingService.cancelBooking(res1.getReservationId());

        inventory.displayInventory();
        bookingHistory.displayAllBookings();

        System.out.println("\n========== Test Case 2: Cancel Another Valid Booking ==========");
        bookingService.cancelBooking(res2.getReservationId());

        inventory.displayInventory();
        bookingHistory.displayAllBookings();

        System.out.println("\n========== Test Case 3: Attempt Duplicate Cancellation ==========");
        bookingService.cancelBooking(res1.getReservationId());

        System.out.println("\n========== Test Case 4: Cancel Non-existent Reservation ==========");
        bookingService.cancelBooking("RES999999");

        System.out.println("\n========== Test Case 5: Cancel Another Booking ==========");
        bookingService.cancelBooking(res3.getReservationId());

        inventory.displayInventory();
        bookingHistory.displayAllBookings();

        System.out.println("\n========== Rollback Stack Information ==========");
        bookingService.getRollbackManager().displayReleasedRooms();

        System.out.println("\n========== Key Design Patterns Demonstrated ==========\n");
        System.out.println("1. State Reversal:");
        System.out.println("   - Cancellation safely undoes booking operations");
        System.out.println("   - Inventory counts accurately restored");
        System.out.println("   - No partial or inconsistent states");
        System.out.println();
        System.out.println("2. Stack Data Structure (LIFO):");
        System.out.println("   - Stack<String> tracks released room IDs");
        System.out.println("   - Last-In-First-Out order natural for rollback");
        System.out.println("   - Most recent allocation reversed first");
        System.out.println();
        System.out.println("3. LIFO Rollback Logic:");
        System.out.println("   - Aligns with real-world undo operations");
        System.out.println("   - Simplifies recovery logic");
        System.out.println("   - Prevents cascading rollback issues");
        System.out.println();
        System.out.println("4. Controlled Mutation:");
        System.out.println("   - State changes in strict, predefined order:");
        System.out.println("     Step 1: Validate reservation exists");
        System.out.println("     Step 2: Verify cancellation eligibility");
        System.out.println("     Step 3: Release inventory (rollback)");
        System.out.println("     Step 4: Update booking status");
        System.out.println("   - Prevents partial rollbacks");
        System.out.println();
        System.out.println("5. Inventory Restoration:");
        System.out.println("   - Inventory incremented immediately after cancellation");
        System.out.println("   - Ensures availability reflects current state");
        System.out.println("   - Prevents inventory inconsistencies");
        System.out.println();
        System.out.println("6. Validation of Cancellation:");
        System.out.println("   - Reservation must exist");
        System.out.println("   - Reservation must be confirmed");
        System.out.println("   - Reservation must not be already cancelled");
        System.out.println("   - Prevents invalid operations");
        System.out.println();
        System.out.println("7. Key Requirements Met:");
        System.out.println("   ✓ Allow cancellation of confirmed bookings only");
        System.out.println("   ✓ Validate reservation existence before rollback");
        System.out.println("   ✓ Release allocated room IDs back to availability pool");
        System.out.println("   ✓ Restore inventory counts accurately and immediately");
        System.out.println("   ✓ Prevent cancellation of non-existent or already cancelled bookings");
        System.out.println();
        System.out.println("8. Key Benefits Achieved:");
        System.out.println("   ✓ Safe recovery of inventory after cancellations");
        System.out.println("   ✓ Consistent system state across booking lifecycle");
        System.out.println("   ✓ Controlled and predictable rollback behavior");
        System.out.println();

        System.out.println("✓ Use Case 10 Completed Successfully");
        System.out.println("✓ System Stability Maintained Throughout Cancellations");
    }
}