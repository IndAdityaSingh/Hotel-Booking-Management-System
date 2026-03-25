import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a booking request with guest and room information.
 */
class BookingRequest {
    private String requestId;
    private String guestName;
    private String roomType;
    private int numberOfNights;
    private Thread requestingThread;
    private long requestTimestamp;

    public BookingRequest(String guestName, String roomType, int numberOfNights) {
        this.requestId = generateRequestId();
        this.guestName = guestName;
        this.roomType = roomType;
        this.numberOfNights = numberOfNights;
        this.requestingThread = Thread.currentThread();
        this.requestTimestamp = System.currentTimeMillis();
    }

    private String generateRequestId() {
        return "REQ" + System.nanoTime();
    }

    public String getRequestId() {
        return requestId;
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

    public Thread getRequestingThread() {
        return requestingThread;
    }

    public long getRequestTimestamp() {
        return requestTimestamp;
    }

    @Override
    public String toString() {
        return "BookingRequest{" +
                "ID='" + requestId + '\'' +
                ", Guest='" + guestName + '\'' +
                ", Room='" + roomType + '\'' +
                ", Nights=" + numberOfNights +
                ", Thread='" + requestingThread.getName() + '\'' +
                '}';
    }
}

/**
 * Thread-safe booking request queue with synchronized access.
 * Demonstrates the critical section concept for shared mutable state.
 */
class ThreadSafeBookingQueue {
    private Queue<BookingRequest> requestQueue;
    private AtomicInteger totalRequests;

    public ThreadSafeBookingQueue() {
        this.requestQueue = new LinkedList<>();
        this.totalRequests = new AtomicInteger(0);
    }

    /**
     * Add booking request to queue (synchronized to prevent race conditions).
     *
     * @param request The booking request to add
     */
    public synchronized void enqueueRequest(BookingRequest request) {
        requestQueue.add(request);
        totalRequests.incrementAndGet();
        System.out.println("[" + Thread.currentThread().getName() + "] " +
                "Enqueued: " + request.getGuestName() +
                " | Queue size: " + requestQueue.size());
    }

    /**
     * Retrieve next booking request from queue (synchronized to prevent race conditions).
     * Implements FIFO order for fair processing.
     *
     * @return Next booking request, or null if queue is empty
     */
    public synchronized BookingRequest dequeueRequest() {
        if (!requestQueue.isEmpty()) {
            BookingRequest request = requestQueue.poll();
            System.out.println("[" + Thread.currentThread().getName() + "] " +
                    "Dequeued: " + request.getGuestName() +
                    " | Remaining: " + requestQueue.size());
            return request;
        }
        return null;
    }

    /**
     * Check if queue has pending requests (synchronized).
     *
     * @return true if queue is not empty
     */
    public synchronized boolean hasPendingRequests() {
        return !requestQueue.isEmpty();
    }

    /**
     * Get current queue size (synchronized).
     *
     * @return Number of requests in queue
     */
    public synchronized int getQueueSize() {
        return requestQueue.size();
    }

    /**
     * Get total requests ever added to queue.
     * Uses AtomicInteger for thread-safe counter.
     *
     * @return Total request count
     */
    public int getTotalRequests() {
        return totalRequests.get();
    }
}

/**
 * Thread-safe inventory manager with synchronized room allocation.
 * Demonstrates protecting critical sections of code.
 */
class ThreadSafeInventoryManager {
    private Map<String, Integer> roomInventory;
    private AtomicInteger totalAllocations;
    private AtomicInteger totalReleases;

    public ThreadSafeInventoryManager() {
        this.roomInventory = new HashMap<>();
        this.totalAllocations = new AtomicInteger(0);
        this.totalReleases = new AtomicInteger(0);
    }

    /**
     * Register a room type with initial inventory (synchronized).
     *
     * @param roomType Type of room
     * @param initialCount Initial number of rooms
     */
    public synchronized void registerRoom(String roomType, int initialCount) {
        roomInventory.put(roomType, initialCount);
        System.out.println("[INVENTORY] Registered: " + roomType + " - " + initialCount + " rooms");
    }

    /**
     * Get available rooms for a room type (synchronized read).
     *
     * @param roomType Type of room
     * @return Number of available rooms
     */
    public synchronized int getAvailableRooms(String roomType) {
        return roomInventory.getOrDefault(roomType, 0);
    }

    /**
     * CRITICAL SECTION: Allocate room with inventory check and update.
     * This synchronized method ensures atomicity of the check-and-allocate operation.
     * Without synchronization, race conditions could allow double-booking.
     *
     * @param roomType Type of room to allocate
     * @return true if allocation successful, false if insufficient inventory
     */
    public synchronized boolean allocateRoom(String roomType) {
        // Critical Section Start: Read and check availability
        int currentAvailable = roomInventory.getOrDefault(roomType, 0);

        // Check if room is available
        if (currentAvailable <= 0) {
            System.out.println("[" + Thread.currentThread().getName() + "] " +
                    "✗ ALLOCATION FAILED: No " + roomType + " available");
            return false;
        }

        // Allocate room (decrement inventory)
        // This operation is atomic because it's in a synchronized block
        int updatedCount = currentAvailable - 1;
        roomInventory.put(roomType, updatedCount);
        totalAllocations.incrementAndGet();

        System.out.println("[" + Thread.currentThread().getName() + "] " +
                "✓ ALLOCATED: " + roomType +
                " | Remaining: " + updatedCount);
        // Critical Section End

        return true;
    }

    /**
     * Release room back to inventory (synchronized).
     *
     * @param roomType Type of room to release
     */
    public synchronized void releaseRoom(String roomType) {
        int currentAvailable = roomInventory.getOrDefault(roomType, 0);
        roomInventory.put(roomType, currentAvailable + 1);
        totalReleases.incrementAndGet();

        System.out.println("[" + Thread.currentThread().getName() + "] " +
                "✓ RELEASED: " + roomType +
                " | Available: " + roomInventory.get(roomType));
    }

    /**
     * Display current inventory state (synchronized).
     */
    public synchronized void displayInventory() {
        System.out.println("\n--- Current Inventory Status ---");
        for (Map.Entry<String, Integer> entry : roomInventory.entrySet()) {
            System.out.println("  " + entry.getKey() + ": " + entry.getValue() + " available");
        }
    }

    /**
     * Get total allocations (uses AtomicInteger).
     *
     * @return Total allocation count
     */
    public int getTotalAllocations() {
        return totalAllocations.get();
    }

    /**
     * Get total releases.
     *
     * @return Total release count
     */
    public int getTotalReleases() {
        return totalReleases.get();
    }
}

/**
 * Represents the result of a booking attempt.
 */
class BookingResult {
    private String reservationId;
    private String guestName;
    private String roomType;
    private boolean successful;
    private String allocatedRoom;
    private long processingTimeMs;
    private Thread processingThread;

    public BookingResult(String guestName, String roomType, boolean successful,
                         String allocatedRoom, long processingTimeMs) {
        this.reservationId = generateReservationId();
        this.guestName = guestName;
        this.roomType = roomType;
        this.successful = successful;
        this.allocatedRoom = allocatedRoom;
        this.processingTimeMs = processingTimeMs;
        this.processingThread = Thread.currentThread();
    }

    private String generateReservationId() {
        return "RES" + System.nanoTime();
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

    public boolean isSuccessful() {
        return successful;
    }

    public String getAllocatedRoom() {
        return allocatedRoom;
    }

    public long getProcessingTimeMs() {
        return processingTimeMs;
    }

    public Thread getProcessingThread() {
        return processingThread;
    }

    @Override
    public String toString() {
        String status = successful ? "SUCCESS" : "FAILED";
        return "BookingResult{" +
                "ID='" + reservationId + '\'' +
                ", Guest='" + guestName + '\'' +
                ", Status='" + status + '\'' +
                ", Room='" + allocatedRoom + '\'' +
                ", Time=" + processingTimeMs + "ms" +
                '}';
    }
}

/**
 * Booking worker thread that processes requests from the shared queue.
 * Demonstrates thread-safe access to shared resources and critical sections.
 */
class BookingWorkerThread extends Thread {
    private ThreadSafeBookingQueue bookingQueue;
    private ThreadSafeInventoryManager inventory;
    private java.util.List<BookingResult> results;
    private volatile boolean running;

    public BookingWorkerThread(String name, ThreadSafeBookingQueue bookingQueue,
                               ThreadSafeInventoryManager inventory,
                               java.util.List<BookingResult> results) {
        super(name);
        this.bookingQueue = bookingQueue;
        this.inventory = inventory;
        this.results = results;
        this.running = true;
    }

    @Override
    public void run() {
        System.out.println("[" + getName() + "] Worker thread started");

        while (running) {
            // Dequeue next request (synchronized operation)
            BookingRequest request = bookingQueue.dequeueRequest();

            if (request == null) {
                // No more requests
                break;
            }

            // Process booking request
            processBookingRequest(request);
        }

        System.out.println("[" + getName() + "] Worker thread terminated");
    }

    /**
     * Process a single booking request with thread-safe inventory access.
     *
     * @param request The booking request to process
     */
    private void processBookingRequest(BookingRequest request) {
        long startTime = System.currentTimeMillis();

        System.out.println("\n[" + getName() + "] Processing: " + request.getGuestName() +
                " | Room: " + request.getRoomType());

        // CRITICAL SECTION: Allocate room (synchronized method)
        // The allocateRoom method is synchronized, ensuring only one thread
        // can read inventory and allocate at the same time
        boolean allocationSuccess = inventory.allocateRoom(request.getRoomType());

        // Simulate some processing time (to increase likelihood of concurrent access)
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        long processingTime = System.currentTimeMillis() - startTime;

        // Record result
        String allocatedRoom = allocationSuccess ?
                ("Room-" + (100 + (int)(Math.random() * 50))) : null;

        BookingResult result = new BookingResult(
                request.getGuestName(),
                request.getRoomType(),
                allocationSuccess,
                allocatedRoom,
                processingTime
        );

        // Add result to shared list (synchronized list operation)
        synchronized (results) {
            results.add(result);
        }

        System.out.println("[" + getName() + "] Completed: " +
                (allocationSuccess ? "✓ SUCCESS" : "✗ FAILED") +
                " | Time: " + processingTime + "ms");
    }

    /**
     * Stop the worker thread.
     */
    public void stopWorker() {
        running = false;
    }
}

/**
 * Concurrent booking processor that manages multiple worker threads.
 * Demonstrates thread safety and concurrent access patterns.
 */
class ConcurrentBookingProcessor {
    private ThreadSafeBookingQueue bookingQueue;
    private ThreadSafeInventoryManager inventory;
    private java.util.List<BookingWorkerThread> workerThreads;
    private java.util.List<BookingResult> results;

    public ConcurrentBookingProcessor(ThreadSafeBookingQueue bookingQueue,
                                      ThreadSafeInventoryManager inventory,
                                      int numberOfWorkers) {
        this.bookingQueue = bookingQueue;
        this.inventory = inventory;
        this.workerThreads = new LinkedList<>();
        this.results = new LinkedList<>();

        // Create worker threads
        for (int i = 1; i <= numberOfWorkers; i++) {
            BookingWorkerThread worker = new BookingWorkerThread(
                    "Worker-" + i,
                    bookingQueue,
                    inventory,
                    results
            );
            workerThreads.add(worker);
        }
    }

    /**
     * Start all worker threads and process booking requests concurrently.
     */
    public void startProcessing() {
        System.out.println("\n========== Starting Concurrent Processing ==========\n");
        System.out.println("Number of worker threads: " + workerThreads.size());
        System.out.println("Pending booking requests: " + bookingQueue.getQueueSize());
        System.out.println();

        // Start all worker threads
        for (BookingWorkerThread worker : workerThreads) {
            worker.start();
        }
    }

    /**
     * Wait for all workers to complete and return results.
     */
    public java.util.List<BookingResult> waitForCompletion() {
        try {
            for (BookingWorkerThread worker : workerThreads) {
                worker.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return results;
    }

    /**
     * Get booking results.
     *
     * @return List of booking results
     */
    public java.util.List<BookingResult> getResults() {
        return results;
    }
}

/**
 * Hotel Booking Management System - Use Case 11: Concurrent Booking Simulation
 *
 * This class demonstrates thread safety and concurrent access to shared resources.
 *
 * Key Concepts:
 * - Race Conditions: Multiple threads accessing shared data simultaneously
 * - Thread Safety: Ensuring correctness under concurrent access
 * - Shared Mutable State: Booking queue and inventory shared across threads
 * - Critical Sections: Synchronized blocks protecting shared state
 * - Synchronized Access: Using synchronized methods for thread safety
 * - AtomicInteger: Lock-free thread-safe counters
 *
 * @author IndAdityaSingh
 * @version 11.0
 * @since 2026-03-25
 */
public class HotelBookingManagementSystem {

    /**
     * Main method - Entry point for Use Case 11.
     * Simulates concurrent booking requests and demonstrates thread safety.
     *
     * @param args Command-line arguments (not used in this use case)
     */
    public static void main(String[] args) {
        System.out.println("================================================");
        System.out.println("Hotel Booking Management System - Use Case 11");
        System.out.println("Concurrent Booking Simulation (Thread Safety)");
        System.out.println("================================================\n");

        // Initialize components
        ThreadSafeBookingQueue bookingQueue = new ThreadSafeBookingQueue();
        ThreadSafeInventoryManager inventory = new ThreadSafeInventoryManager();

        System.out.println("========== Setting Up Inventory ==========\n");

        // Register rooms with limited inventory
        inventory.registerRoom("Single Room", 3);
        inventory.registerRoom("Double Room", 2);
        inventory.registerRoom("Suite Room", 1);

        inventory.displayInventory();

        System.out.println("\n========== Creating Concurrent Booking Requests ==========\n");

        // Create booking requests simulating concurrent guests
        // These will be added to the queue from this main thread
        BookingRequest[] requests = {
                new BookingRequest("Guest 1 - John", "Double Room", 2),
                new BookingRequest("Guest 2 - Jane", "Single Room", 1),
                new BookingRequest("Guest 3 - Alice", "Suite Room", 3),
                new BookingRequest("Guest 4 - Bob", "Double Room", 1),
                new BookingRequest("Guest 5 - Carol", "Single Room", 2),
                new BookingRequest("Guest 6 - David", "Single Room", 1),
                new BookingRequest("Guest 7 - Emma", "Double Room", 2),
                new BookingRequest("Guest 8 - Frank", "Suite Room", 1),
                new BookingRequest("Guest 9 - Grace", "Single Room", 4),
                new BookingRequest("Guest 10 - Henry", "Double Room", 3)
        };

        // Enqueue all requests to shared booking queue
        System.out.println("Enqueuing booking requests...\n");
        for (BookingRequest request : requests) {
            bookingQueue.enqueueRequest(request);

            // Small delay to simulate requests arriving at slightly different times
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("\nTotal requests in queue: " + bookingQueue.getQueueSize());

        // Create concurrent booking processor with multiple worker threads
        int numberOfWorkers = 3;
        ConcurrentBookingProcessor processor = new ConcurrentBookingProcessor(
                bookingQueue,
                inventory,
                numberOfWorkers
        );

        // Start concurrent processing
        long startTime = System.currentTimeMillis();
        processor.startProcessing();

        // Wait for all workers to complete
        java.util.List<BookingResult> results = processor.waitForCompletion();
        long endTime = System.currentTimeMillis();

        System.out.println("\n========== Concurrent Processing Completed ==========");
        System.out.println("Total processing time: " + (endTime - startTime) + " ms\n");

        // Display final inventory state
        inventory.displayInventory();

        // Display booking results
        System.out.println("\n========== Booking Results ==========\n");

        int successCount = 0;
        int failureCount = 0;

        for (int i = 0; i < results.size(); i++) {
            BookingResult result = results.get(i);
            String status = result.isSuccessful() ? "✓ SUCCESS" : "✗ FAILED";
            System.out.println((i + 1) + ". " + result.getGuestName() +
                    " | " + result.getRoomType() +
                    " | " + status +
                    " | Room: " + (result.getAllocatedRoom() != null ? result.getAllocatedRoom() : "N/A"));

            if (result.isSuccessful()) {
                successCount++;
            } else {
                failureCount++;
            }
        }

        System.out.println("\n========== Statistics ==========\n");
        System.out.println("Total Booking Requests: " + bookingQueue.getTotalRequests());
        System.out.println("Successful Allocations: " + successCount);
        System.out.println("Failed Allocations: " + failureCount);
        System.out.println("Total Allocations: " + inventory.getTotalAllocations());
        System.out.println("Success Rate: " + String.format("%.2f%%",
                (successCount * 100.0 / results.size())));

        System.out.println("\n========== Key Concepts Demonstrated ==========\n");
        System.out.println("1. Race Conditions:");
        System.out.println("   - Multiple threads access shared booking queue");
        System.out.println("   - Concurrent threads access shared inventory");
        System.out.println("   - Without synchronization, double-booking would occur");
        System.out.println();
        System.out.println("2. Thread Safety:");
        System.out.println("   - ThreadSafeBookingQueue uses synchronized methods");
        System.out.println("   - ThreadSafeInventoryManager protects critical sections");
        System.out.println("   - AtomicInteger used for thread-safe counters");
        System.out.println();
        System.out.println("3. Shared Mutable State:");
        System.out.println("   - Booking queue shared across all worker threads");
        System.out.println("   - Inventory map accessed by multiple threads");
        System.out.println("   - Results list updated concurrently");
        System.out.println();
        System.out.println("4. Critical Sections:");
        System.out.println("   - allocateRoom() is a critical section");
        System.out.println("   - Check-and-allocate performed atomically");
        System.out.println("   - No interleaving of operations possible");
        System.out.println();
        System.out.println("5. Synchronized Access:");
        System.out.println("   - Synchronized methods ensure exclusive access");
        System.out.println("   - Only one thread can execute synchronized block at a time");
        System.out.println("   - Prevents inconsistent state transitions");
        System.out.println();
        System.out.println("6. Concurrency vs. Parallelism:");
        System.out.println("   - Multiple threads execute concurrently");
        System.out.println("   - Focus is on correctness, not performance");
        System.out.println("   - Demonstrates safe multi-threaded access patterns");
        System.out.println();
        System.out.println("7. Key Requirements Met:");
        System.out.println("   ✓ Multiple booking requests submitted concurrently");
        System.out.println("   ✓ Shared data structures used safely");
        System.out.println("   ✓ Inventory updates performed in critical sections");
        System.out.println("   ✓ No double allocation under concurrent execution");
        System.out.println("   ✓ Consistent system state maintained");
        System.out.println();
        System.out.println("8. Key Benefits Achieved:");
        System.out.println("   ✓ Safe multi-user booking simulation");
        System.out.println("   ✓ Correct room allocations under concurrent load");
        System.out.println("   ✓ Foundation for scalable multi-user systems");
        System.out.println();

        System.out.println("✓ Use Case 11 Completed Successfully");
        System.out.println("✓ System Integrity Maintained Under Concurrent Load");
    }
}