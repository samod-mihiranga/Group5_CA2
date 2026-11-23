/*
 * Student Name: [W.M.M.N.K. WIJESINGHA]
 * Student ID: [24IT0547]
 * Group: 5
 */

/**
 * Abstract base class for shared vehicle identity and availability.
 * Subclasses supply their own daily rates and deposits. RentalAgency coordinates
 * vehicle availability with customer counts; this class does not read input.
 */
public abstract class Vehicle implements Rentable {
    private final String plate;
    private final String brand;
    private boolean available;

    /**
     * Initializes validated vehicle id and starts the vehicle as available.
     * @param plate non-blank registration plate
     * @param brand non-blank manufacturer or brand
     * @throws IllegalArgumentException if plate or brand is null or blank
     */
    protected Vehicle(String plate, String brand) {
        if (plate == null || plate.isBlank()) {
            throw new IllegalArgumentException("Plate must not be blank.");
        }
        if (brand == null || brand.isBlank()) {
            throw new IllegalArgumentException("Brand must not be blank.");
        }
        this.plate = plate.strip();
        this.brand = brand.strip();
        this.available = true;
    }

    /**
     * @return the registration plate, with surrounding whitespace removed
     */
    public String getPlate() {
        return plate; }
    /**
     * @return the vehicle brand
     */
    public String getBrand() {
        return brand; }
    /**
     * @return true when the vehicle is not currently rented
     */
    public boolean isAvailable() {
        return available; }

    /**
     * @return the standard rental charge for one day, in rupees
     */
    public abstract double getDailyRate();
    /**
     * @return the separate security deposit, in rupees
     */
    public abstract double getDeposit();
    
    /**
     * Enforces the inclusive 1-30 day range used throughout this implementation.
     * @param days rental duration to validate
     * @throws IllegalArgumentException if days is below 1 or above 30
     */
    public static void validateDays(int days) {
        if (days < 1 || days > 30) {
            throw new IllegalArgumentException("Rental days must be from 1 to 30.");
        }
    }

    /**
     * Calculates the standard rental charge, excluding deposit and late fees.
     * Calling getDailyRate() dispatches to the actual vehicle subclass.
     * @param days rental duration from 1 to 30
     * @return daily rate multiplied by days
     * @throws IllegalArgumentException if days is outside the allowed range
     */
    public double calculateRental(int days) {
        validateDays(days);
        return days * getDailyRate();
    }

    /**
     * Changes an available vehicle to rented; a second rental makes no change.
     * @return true on success, or false if already rented
     */
    @Override
    public boolean rent() {
        if (!available) {
            return false;
        }
        available = false;
        return true;
    }

    /**
     * Restores availability. Calling it again leaves availability true.
     * RentalAgency validates ownership and prevents duplicate customer-count updates.
     */
    @Override
    public void giveBack() {
        available = true;
    }
}
