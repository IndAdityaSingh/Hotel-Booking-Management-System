import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * Represents a booking with persistence capability.
 * Implements Serializable for file-based persistence.
 */
class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

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
                ", Status=" + (cancelled ? "CANCELLED" : (confirmed ? "CONFIRMED" : "PENDING")) +
                '}';
    }
}

/**
 * Represents persisted system state snapshot.
 * Contains inventory and booking history for serialization.
 */
class SystemSnapshot implements Serializable {
    private static final long serialVersionUID = 1L;

    private Map<String, Integer> inventoryState;
    private List<Reservation> bookingHistory;
    private LocalDateTime snapshotTime;
    private long totalBookings;
    private long totalCancellations;

    public SystemSnapshot(Map<String, Integer> inventoryState,
                          List<Reservation> bookingHistory,
                          long totalBookings,
                          long totalCancellations) {
        this.inventoryState = new HashMap<>(inventoryState);
        this.bookingHistory = new ArrayList<>(bookingHistory);
        this.snapshotTime = LocalDateTime.now();
        this.totalBookings = totalBookings;
        this.totalCancellations = totalCancellations;
    }

    public Map<String, Integer> getInventoryState() {
        return inventoryState;
    }

    public List<Reservation> getBookingHistory() {
        return bookingHistory;
    }

    public LocalDateTime getSnapshotTime() {
        return snapshotTime;
    }

    public long getTotalBookings() {
        return totalBookings;
    }

    public long getTotalCancellations() {
        return totalCancellations;
    }

    @Override
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return "SystemSnapshot{" +
                "Time='" + snapshotTime.format(formatter) + '\'' +
                ", Bookings=" + totalBookings +
                ", Cancellations=" + totalCancellations +
                ", Inventory=" + inventoryState +
                '}';
    }
}

/**
 * Manages inventory state with persistence capability.
 */
class InventoryManager {
    private Map<String, Integer> roomInventory;
    private long totalAllocations;
    private long totalReleases;

    public InventoryManager() {
        this.roomInventory = new HashMap<>();
        this.totalAllocations = 0;
        this.totalReleases = 0;
    }

    public void registerRoom(String roomType, int initialCount) {
        roomInventory.put(roomType, initialCount);
        System.out.println("✓ Registered: " + roomType + " - " + initialCount + " rooms");
    }

    public int getAvailableRooms(String roomType) {
        return roomInventory.getOrDefault(roomType, 0);
    }

    public synchronized boolean allocateRoom(String roomType) {
        int currentAvailable = roomInventory.getOrDefault(roomType, 0);

        if (currentAvailable <= 0) {
            System.out.println("✗ No " + roomType + " available");
            return false;
        }

        roomInventory.put(roomType, currentAvailable - 1);
        totalAllocations++;
        System.out.println("✓ Allocated: " + roomType + " | Remaining: " +
                roomInventory.get(roomType));
        return true;
    }

    public void releaseRoom(String roomType) {
        int currentAvailable = roomInventory.getOrDefault(roomType, 0);
        roomInventory.put(roomType, currentAvailable + 1);
        totalReleases++;
        System.out.println("✓ Released: " + roomType + " | Available: " +
                roomInventory.get(roomType));
    }

    public void displayInventory() {
        System.out.println("\n--- Current Inventory Status ---");
        for (Map.Entry<String, Integer> entry : roomInventory.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " available");
        }
    }

    public Map<String, Integer> getInventorySnapshot() {
        return new HashMap<>(roomInventory);
    }

    public void restoreInventoryFromSnapshot(Map<String, Integer> snapshot) {
        this.roomInventory = new HashMap<>(snapshot);
        System.out.println("✓ Inventory restored from snapshot");
    }

    public long getTotalAllocations() {
        return totalAllocations;
    }

    public long getTotalReleases() {
        return totalReleases;
    }
}

/**
 * Manages booking history with persistence capability.
 */
class BookingHistory {
    private List<Reservation> bookings;
    private long totalCancellations;

    public BookingHistory() {
        this.bookings = new LinkedList<>();
        this.totalCancellations = 0;
    }

    public void addBooking(Reservation reservation) {
        bookings.add(reservation);
        System.out.println("✓ Booking added: " + reservation.getGuestName() +
                " | ID: " + reservation.getReservationId());
    }

    public Reservation findBookingById(String reservationId) {
        for (Reservation reservation : bookings) {
            if (reservation.getReservationId().equals(reservationId)) {
                return reservation;
            }
        }
        return null;
    }

    public void displayAllBookings() {
        System.out.println("\n--- Booking History ---");
        if (bookings.isEmpty()) {
            System.out.println("No bookings recorded");
            return;
        }

        for (int i = 0; i < bookings.size(); i++) {
            Reservation booking = bookings.get(i);
            String status = booking.isCancelled() ? "CANCELLED" :
                    (booking.isConfirmed() ? "CONFIRMED" : "PENDING");
            System.out.println((i + 1) + ". " + booking.getGuestName() +
                    " | " + booking.getRoomType() +
                    " | " + status);
        }
    }

    public List<Reservation> getAllBookings() {
        return new LinkedList<>(bookings);
    }

    public void restoreBookingsFromSnapshot(List<Reservation> snapshot) {
        this.bookings = new LinkedList<>(snapshot);
        System.out.println("✓ Booking history restored from snapshot (" +
                bookings.size() + " bookings)");
    }

    public int getBookingCount() {
        return bookings.size();
    }

    public long getTotalCancellations() {
        return totalCancellations;
    }

    public void incrementCancellationCount() {
        totalCancellations++;
    }
}

/**
 * Persistence Service: Handles serialization and deserialization of system state.
 * Manages save and restore operations for durability.
 */
class PersistenceService {
    private String persistenceFilePath;

    public PersistenceService(String filePath) {
        this.persistenceFilePath = filePath;
    }

    /**
     * Save system state to persistent storage.
     * Implements serialization concept.
     *
     * @param inventoryManager Current inventory state
     * @param bookingHistory Current booking history
     * @return true if save successful, false otherwise
     */
    public boolean saveSystemState(InventoryManager inventoryManager,
                                   BookingHistory bookingHistory) {
        try {
            System.out.println("\n[PERSISTENCE] Saving system state...");

            // Create system snapshot
            SystemSnapshot snapshot = new SystemSnapshot(
                    inventoryManager.getInventorySnapshot(),
                    bookingHistory.getAllBookings(),
                    inventoryManager.getTotalAllocations(),
                    bookingHistory.getTotalCancellations()
            );

            // Serialize to file using ObjectOutputStream
            FileOutputStream fileOut = new FileOutputStream(persistenceFilePath);
            ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);

            objectOut.writeObject(snapshot);
            objectOut.close();
            fileOut.close();

            System.out.println("✓ System state saved successfully");
            System.out.println("  File: " + persistenceFilePath);
            System.out.println("  Snapshot: " + snapshot);

            return true;

        } catch (IOException e) {
            System.out.println("✗ Failed to save system state");
            System.out.println("  Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Restore system state from persistent storage.
     * Implements deserialization concept.
     *
     * @param inventoryManager Inventory to restore into
     * @param bookingHistory Booking history to restore into
     * @return true if restore successful, false otherwise
     */
    public boolean restoreSystemState(InventoryManager inventoryManager,
                                      BookingHistory bookingHistory) {
        try {
            System.out.println("\n[PERSISTENCE] Restoring system state...");

            File persistenceFile = new File(persistenceFilePath);
            if (!persistenceFile.exists()) {
                System.out.println("✗ Persistence file not found");
                System.out.println("  File: " + persistenceFilePath);
                System.out.println("  Status: Starting with fresh state");
                return false;
            }

            // Deserialize from file using ObjectInputStream
            FileInputStream fileIn = new FileInputStream(persistenceFilePath);
            ObjectInputStream objectIn = new ObjectInputStream(fileIn);

            SystemSnapshot snapshot = (SystemSnapshot) objectIn.readObject();
            objectIn.close();
            fileIn.close();

            // Restore inventory
            inventoryManager.restoreInventoryFromSnapshot(snapshot.getInventoryState());

            // Restore booking history
            bookingHistory.restoreBookingsFromSnapshot(snapshot.getBookingHistory());

            System.out.println("✓ System state restored successfully");
            System.out.println("  Snapshot: " + snapshot);

            return true;

        } catch (FileNotFoundException e) {
            System.out.println("✗ Persistence file not found");
            System.out.println("  Status: Starting with fresh state");
            return false;

        } catch (IOException e) {
            System.out.println("✗ Error reading persistence file");
            System.out.println("  Error: " + e.getMessage());
            System.out.println("  Status: Corrupted or invalid file - starting fresh");
            return false;

        } catch (ClassNotFoundException e) {
            System.out.println("✗ Failed to deserialize persisted data");
            System.out.println("  Error: " + e.getMessage());
            return false;
        }
    }

    /**
     * Check if persistence file exists.
     *
     * @return true if file exists, false otherwise
     */
    public boolean persistenceFileExists() {
        return new File(persistenceFilePath).exists();
    }

    /**
     * Get persistence file path.
     *
     * @return File path
     */
    public String getPersistenceFilePath() {
        return persistenceFilePath;
    }

    /**
     * Delete persistence file for testing purposes.
     */
    public void deletePersistenceFile() {
        File file = new File(persistenceFilePath);
        if (file.exists()) {
            if (file.delete()) {
                System.out.println("✓ Persistence file deleted");
            }
        }
    }
}

/**
 * Booking service with persistence support.
 */
class BookingService {
    private InventoryManager inventoryManager;
    private BookingHistory bookingHistory;

    public BookingService(InventoryManager inventoryManager, BookingHistory bookingHistory) {
        this.inventoryManager = inventoryManager;
        this.bookingHistory = bookingHistory;
    }

    public boolean processBooking(String guestName, String roomType, int numberOfNights) {
        System.out.println("\n--- Processing Booking ---");
        System.out.println("Guest: " + guestName + " | Room: " + roomType + " | Nights: " + numberOfNights);

        int available = inventoryManager.getAvailableRooms(roomType);
        System.out.println("Available: " + available);

        if (available <= 0) {
            System.out.println("✗ BOOKING FAILED: No " + roomType + " available");
            return false;
        }

        // Allocate room
        boolean allocationSuccess = inventoryManager.allocateRoom(roomType);

        if (allocationSuccess) {
            // Create and confirm reservation
            Reservation reservation = new Reservation(guestName, roomType, numberOfNights);
            String allocatedRoom = "Room-" + (100 + (int)(Math.random() * 50));
            reservation.setAllocatedRoomNumber(allocatedRoom);
            reservation.setConfirmed(true);

            // Add to history
            bookingHistory.addBooking(reservation);

            System.out.println("✓ BOOKING CONFIRMED");
            System.out.println("  Reservation ID: " + reservation.getReservationId());
            System.out.println("  Room: " + allocatedRoom);
            System.out.println("  Total Cost: $" + String.format("%.2f", reservation.getTotalCost()));

            return true;
        }

        return false;
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public BookingHistory getBookingHistory() {
        return bookingHistory;
    }
}

/**
 * Hotel Booking Management System - Use Case 12: Data Persistence & System Recovery
 *
 * This class demonstrates data persistence and recovery concepts.
 *
 * Key Concepts:
 * - Stateful Applications: Maintains data beyond execution cycle
 * - Persistence: Storing state in durable medium
 * - Serialization: Converting objects to persistent format
 * - Deserialization: Restoring objects from persistent data
 * - Inventory Snapshot: Capturing state at a point in time
 * - Failure Tolerance: Handling missing/corrupted files gracefully
 * - Preparation for Database Integration: Conceptual foundation
 *
 * @author IndAdityaSingh
 * @version 12.0
 * @since 2026-03-25
 */
public class HotelBookingManagementSystem {

    private static final String PERSISTENCE_FILE = "hotel_booking_system.dat";

    /**
     * Main method - Entry point for Use Case 12.
     * Demonstrates data persistence and recovery.
     *
     * @param args Command-line arguments (not used)
     */
    public static void main(String[] args) {
        System.out.println("================================================");
        System.out.println("Hotel Booking Management System - Use Case 12");
        System.out.println("Data Persistence & System Recovery");
        System.out.println("================================================\n");

        // Initialize persistence service
        PersistenceService persistenceService = new PersistenceService(PERSISTENCE_FILE);

        // Check if recovering from previous session
        boolean isRecoveryMode = persistenceService.persistenceFileExists();

        if (isRecoveryMode) {
            System.out.println("========== RECOVERY MODE ==========");
            System.out.println("Previous session state found");
        } else {
            System.out.println("========== FRESH START ==========");
            System.out.println("No previous session state found");
        }

        // Initialize components
        InventoryManager inventoryManager = new InventoryManager();
        BookingHistory bookingHistory = new BookingHistory();

        // Attempt to restore from persistence
        if (isRecoveryMode) {
            persistenceService.restoreSystemState(inventoryManager, bookingHistory);
        } else {
            // Fresh start: Set up initial inventory
            System.out.println("\n========== Setting Up Initial Inventory ==========\n");
            inventoryManager.registerRoom("Single Room", 5);
            inventoryManager.registerRoom("Double Room", 3);
            inventoryManager.registerRoom("Suite Room", 2);
        }

        inventoryManager.displayInventory();

        // Initialize booking service
        BookingService bookingService = new BookingService(inventoryManager, bookingHistory);

        System.out.println("\n========== Processing New Booking Requests ==========");

        // Process bookings
        bookingService.processBooking("John Doe", "Double Room", 2);
        bookingService.processBooking("Jane Smith", "Single Room", 1);
        bookingService.processBooking("Alice Johnson", "Suite Room", 3);
        bookingService.processBooking("Bob Wilson", "Double Room", 1);
        bookingService.processBooking("Carol Davis", "Single Room", 2);

        inventoryManager.displayInventory();
        bookingHistory.displayAllBookings();

        System.out.println("\n========== System Statistics ==========\n");
        System.out.println("Total Bookings: " + bookingHistory.getBookingCount());
        System.out.println("Total Allocations: " + inventoryManager.getTotalAllocations());
        System.out.println("Total Releases: " + inventoryManager.getTotalReleases());
        System.out.println("Recovery Mode: " + (isRecoveryMode ? "Yes" : "No"));

        // Simulate application shutdown and save state
        System.out.println("\n========== Preparing for Shutdown ==========");
        System.out.println("Capturing system state...");

        boolean saveSuccess = persistenceService.saveSystemState(inventoryManager, bookingHistory);

        System.out.println("\n========== Key Concepts Demonstrated ==========\n");
        System.out.println("1. Stateful Applications:");
        System.out.println("   - System maintains booking and inventory data");
        System.out.println("   - State persists across application restarts");
        System.out.println("   - Data is not lost on shutdown");
        System.out.println();
        System.out.println("2. Persistence:");
        System.out.println("   - Application state stored in: " + PERSISTENCE_FILE);
        System.out.println("   - Data survives application termination");
        System.out.println("   - Ensures business continuity");
        System.out.println();
        System.out.println("3. Serialization:");
        System.out.println("   - In-memory objects converted to file format");
        System.out.println("   - SystemSnapshot encapsulates state for storage");
        System.out.println("   - ObjectOutputStream used for serialization");
        System.out.println();
        System.out.println("4. Deserialization:");
        System.out.println("   - Persisted data reconstructed into memory");
        System.out.println("   - ObjectInputStream used for deserialization");
        System.out.println("   - System resumes with accurate recovered state");
        System.out.println();
        System.out.println("5. Inventory Snapshot:");
        System.out.println("   - Point-in-time capture of inventory state");
        System.out.println("   - Restores all room availability counts");
        System.out.println("   - Ensures consistency after recovery");
        System.out.println();
        System.out.println("6. Failure Tolerance:");
        System.out.println("   - Handles missing persistence file gracefully");
        System.out.println("   - Handles corrupted or invalid files safely");
        System.out.println("   - System starts in known valid state");
        System.out.println();
        System.out.println("7. Preparation for Database Integration:");
        System.out.println("   - File-based persistence introduces durability concepts");
        System.out.println("   - No database complexity at this stage");
        System.out.println("   - Conceptual foundation for future database systems");
        System.out.println();
        System.out.println("8. Key Requirements Met:");
        System.out.println("   ✓ Booking history persisted to file");
        System.out.println("   ✓ Inventory state persisted to file");
        System.out.println("   ✓ Data restored during startup");
        System.out.println("   ✓ Handles missing/corrupted files gracefully");
        System.out.println("   ✓ System operates safely after recovery");
        System.out.println();
        System.out.println("9. Key Benefits Achieved:");
        System.out.println("   ✓ No data loss across application restarts");
        System.out.println("   ✓ More realistic, production-aligned behavior");
        System.out.println("   ✓ Smooth transition toward database systems");
        System.out.println();

        if (saveSuccess) {
            System.out.println("✓ Use Case 12 Completed Successfully");
            System.out.println("✓ System State Persisted for Future Recovery");
            System.out.println("\n[Note] To simulate recovery, run the program again without deleting " + PERSISTENCE_FILE);
        } else {
            System.out.println("✗ Failed to persist system state");
        }
    }
}