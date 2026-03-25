/**
 * Hotel Booking Management System - Use Case 2: Basic Room Types & Static Availability
 *
 * This class serves as the application entry point for demonstrating room type initialization
 * and static availability management. It showcases object-oriented design principles including
 * abstract classes, inheritance, and polymorphism in the context of hotel room management.
 *
 * @author IndAdityaSingh
 * @version 2.0
 * @since 2026-03-25
 */
public class HotelBookingManagementSystem   {

    /**
     * Main method - Entry point for the Hotel Booking Management System.
     * Initializes room objects and displays their details with availability information.
     *
     * @param args Command-line arguments (not used in this use case)
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Hotel Booking Management System - Use Case 2");
        System.out.println("Room Types & Static Availability");
        System.out.println("========================================\n");

        // Create room objects representing different room types
        SingleRoom singleRoom = new SingleRoom("SR001", 1, 200.0);
        DoubleRoom doubleRoom = new DoubleRoom("DR001", 2, 350.0);
        SuiteRoom suiteRoom = new SuiteRoom("SU001", 2, 500.0);

        // Static availability variables for each room type
        int singleRoomAvailability = 5;
        int doubleRoomAvailability = 3;
        int suiteRoomAvailability = 2;

        // Display room details and availability
        System.out.println("Available Room Types:\n");

        System.out.println("1. " + singleRoom.getRoomType());
        System.out.println("   Room ID: " + singleRoom.getRoomId());
        System.out.println("   Number of Beds: " + singleRoom.getNumberOfBeds());
        System.out.println("   Price per Night: $" + singleRoom.getPricePerNight());
        System.out.println("   Availability: " + singleRoomAvailability + " rooms available");
        System.out.println();

        System.out.println("2. " + doubleRoom.getRoomType());
        System.out.println("   Room ID: " + doubleRoom.getRoomId());
        System.out.println("   Number of Beds: " + doubleRoom.getNumberOfBeds());
        System.out.println("   Price per Night: $" + doubleRoom.getPricePerNight());
        System.out.println("   Availability: " + doubleRoomAvailability + " rooms available");
        System.out.println();

        System.out.println("3. " + suiteRoom.getRoomType());
        System.out.println("   Room ID: " + suiteRoom.getRoomId());
        System.out.println("   Number of Beds: " + suiteRoom.getNumberOfBeds());
        System.out.println("   Price per Night: $" + suiteRoom.getPricePerNight());
        System.out.println("   Availability: " + suiteRoomAvailability + " rooms available");
        System.out.println();

        System.out.println("========================================");
        System.out.println("Application terminated successfully!");
    }
}

/**
 * Abstract Room class representing a generalized room concept.
 * Defines common attributes and behavior shared by all room types.
 * This class cannot be instantiated directly and serves as a blueprint for concrete room implementations.
 *
 * @author IndAdityaSingh
 * @version 2.0
 * @since 2026-03-25
 */
abstract class Room {
    protected String roomId;
    protected int numberOfBeds;
    protected double pricePerNight;

    /**
     * Constructor for Room class.
     *
     * @param roomId Unique identifier for the room
     * @param numberOfBeds Number of beds in the room
     * @param pricePerNight Price per night for the room
     */
    public Room(String roomId, int numberOfBeds, double pricePerNight) {
        this.roomId = roomId;
        this.numberOfBeds = numberOfBeds;
        this.pricePerNight = pricePerNight;
    }

    /**
     * Abstract method to be implemented by concrete room classes.
     * Returns the type of room.
     *
     * @return The type of room as a String
     */
    public abstract String getRoomType();

    /**
     * Gets the room ID.
     *
     * @return The room ID
     */
    public String getRoomId() {
        return roomId;
    }

    /**
     * Gets the number of beds in the room.
     *
     * @return The number of beds
     */
    public int getNumberOfBeds() {
        return numberOfBeds;
    }

    /**
     * Gets the price per night for the room.
     *
     * @return The price per night
     */
    public double getPricePerNight() {
        return pricePerNight;
    }
}

/**
 * SingleRoom class representing a single bed room.
 * Extends the abstract Room class and specializes it for single occupancy.
 *
 * @author IndAdityaSingh
 * @version 2.0
 * @since 2026-03-25
 */
class SingleRoom extends Room {

    /**
     * Constructor for SingleRoom class.
     *
     * @param roomId Unique identifier for the single room
     * @param numberOfBeds Number of beds (should be 1 for single room)
     * @param pricePerNight Price per night for the single room
     */
    public SingleRoom(String roomId, int numberOfBeds, double pricePerNight) {
        super(roomId, numberOfBeds, pricePerNight);
    }

    /**
     * Returns the type of room.
     *
     * @return "Single Room" as the room type
     */
    @Override
    public String getRoomType() {
        return "Single Room";
    }
}

/**
 * DoubleRoom class representing a double bed room.
 * Extends the abstract Room class and specializes it for double occupancy.
 *
 * @author IndAdityaSingh
 * @version 2.0
 * @since 2026-03-25
 */
class DoubleRoom extends Room {

    /**
     * Constructor for DoubleRoom class.
     *
     * @param roomId Unique identifier for the double room
     * @param numberOfBeds Number of beds (should be 2 for double room)
     * @param pricePerNight Price per night for the double room
     */
    public DoubleRoom(String roomId, int numberOfBeds, double pricePerNight) {
        super(roomId, numberOfBeds, pricePerNight);
    }

    /**
     * Returns the type of room.
     *
     * @return "Double Room" as the room type
     */
    @Override
    public String getRoomType() {
        return "Double Room";
    }
}

/**
 * SuiteRoom class representing a luxury suite room.
 * Extends the abstract Room class and specializes it for premium accommodations.
 *
 * @author IndAdityaSingh
 * @version 2.0
 * @since 2026-03-25
 */
class SuiteRoom extends Room {

    /**
     * Constructor for SuiteRoom class.
     *
     * @param roomId Unique identifier for the suite room
     * @param numberOfBeds Number of beds in the suite
     * @param pricePerNight Price per night for the suite room
     */
    public SuiteRoom(String roomId, int numberOfBeds, double pricePerNight) {
        super(roomId, numberOfBeds, pricePerNight);
    }

    /**
     * Returns the type of room.
     *
     * @return "Suite Room" as the room type
     */
    @Override
    public String getRoomType() {
        return "Suite Room";
    }
}