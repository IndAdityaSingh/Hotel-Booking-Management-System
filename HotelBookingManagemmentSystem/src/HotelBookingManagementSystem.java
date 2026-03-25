import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a guest reservation with booking details.
 * Each reservation has guest information, room type, duration, and confirmation status.
 */
public class HotelBookingManagementSystem {
    private String reservationId;
    private String guestName;
    private String roomType;
    private int numberOfNights;
    private double roomCostPerNight;
    private LocalDateTime bookingDateTime;
    private boolean confirmed;
    private String allocatedRoomNumber;

    /**
     * Constructor for creating a new reservation.
     *
     * @param guestName Name of the guest
     * @param roomType Type of room requested
     * @param numberOfNights Number of nights for the stay
     */
    public HotelBookingManagementSystem(String guestName, String roomType, int numberOfNights) {
        this.reservationId = generateReservationId();
        this.guestName = guestName;
        this.roomType = roomType;
        this.numberOfNights = numberOfNights;
        this.bookingDateTime = LocalDateTime.now();
        this.confirmed = false;
        this.allocatedRoomNumber = null;

        // Set room cost based on room type
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

    /**
     * Generate a unique reservation ID.
     */
    private String generateReservationId() {
        return "RES" + System.currentTimeMillis();
    }

    // Getters
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

    public String getAllocatedRoomNumber() {
        return allocatedRoomNumber;
    }

    // Setters
    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public void setAllocatedRoomNumber(String roomNumber) {
        this.allocatedRoomNumber = roomNumber;
    }

    /**
     * Get booking date and time as formatted string.
     */
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
                ", Room#=" + allocatedRoomNumber +
                '}';
    }
}