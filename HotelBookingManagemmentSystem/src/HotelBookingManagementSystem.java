import java.util.LinkedList;
import java.util.Queue;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Hotel Booking Management System - Use Case 6: Reservation Confirmation & Room Allocation
 *
 * This class serves as the application entry point for demonstrating safe room allocation
 * and reservation confirmation. It showcases how Set and HashMap data structures work together
 * to prevent double-booking by enforcing uniqueness of room assignments while maintaining
 * inventory consistency.
 *
 * @author IndAdityaSingh
 * @version 6.0
 * @since 2026-03-25
 */
public class HotelBookingManagementSystem {

    /**
     * Main method - Entry point for the Hotel Booking Management System.
     * Demonstrates booking request processing and room allocation.
     *
     * @param args Command-line arguments (not used in this use case)
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Hotel Booking Management System - Use Case 6");
        System.out.println("Reservation Confirmation & Room Allocation");
        System.out.println("========================================\n");

        // Initialize inventory
        RoomInventory inventory = new RoomInventory();
        inventory.registerRoom("Single Room", 5);
        inventory.registerRoom("Double Room", 3);
        inventory.registerRoom("Suite Room", 2);

        System.out.println("Initial Inventory Status:");
        inventory.displayInventory();
        System.out.println();

        // Initialize booking request queue
        BookingRequestQueue requestQueue = new BookingRequestQueue();

        // Submit booking requests
        System.out.println("Submitting booking requests...\n");
        Reservation guest1 = new Reservation("Guest 1 (John Doe)", "Double Room", 1);
        Reservation guest2 = new Reservation("Guest 2 (Jane Smith)", "Single Room", 1);
        Reservation guest3 = new Reservation("Guest 3 (Alice Johnson)", "Suite Room", 1);
        Reservation guest4 = new Reservation("Guest 4 (Bob Wilson)", "Double Room", 1);
        Reservation guest5 = new Reservation("Guest 5 (Carol Davis)", "Single Room", 2);

        requestQueue.submitBookingRequest(guest1);
        requestQueue.submitBookingRequest(guest2);
        requestQueue.submitBookingRequest(guest3);
        requestQueue.submitBookingRequest(guest4);
        requestQueue.submitBookingRequest(guest5);

        System.out.println();
        System.out.println("Total Requests in Queue: " + requestQueue.getQueueSize());
        System.out.println();

        // Initialize booking service with allocation capability
        BookingService bookingService = new BookingService(inventory, requestQueue);

        System.out.println("========== Processing Booking Requests ==========\n");

        // Process all booking requests
        bookingService.processAllBookingRequests();

        System.out.println();
        System.out.println("========== Final System State ==========\n");

        // Display updated inventory
        System.out.println("Final Inventory Status:");
        inventory.displayInventory();
        System.out.println();

        // Display allocation summary
        System.out.println("Room Allocation Summary:");
        System.out.println("------------------------");
        bookingService.displayAllocationSummary();
        System.out.println();

        // Verify consistency
        System.out.println("========== System Consistency Verification ==========");
        System.out.println("*
