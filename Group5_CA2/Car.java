/*
 * Student Name: [W.A.C. HESHAN]
 * Student ID: [24IT0473]
 * Group: 5
 */

/**
 * Represents a car with a positive seat count.
 * Inherits rental state from Vehicle and supplies the car rate and deposit.
 */
public class Car extends Vehicle {
    private final int seats;

    /**
     * Creates a car after validating shared identity and the seat count.
     * @param plate non-blank registration plate
     * @param brand non-blank brand
     * @param seats number of seats, greater than zero
     * @throws IllegalArgumentException if identity is invalid or seats is not positive
     */
    public Car(String plate, String brand, int seats) {
        super(plate, brand);
        if (seats <= 0) {
            throw new IllegalArgumentException("Seats must be positive.");
        }
        this.seats = seats;
    }

    /**
     * @return the number of seats
     */
    public int getSeats() {
        return seats; }

    /**
     * @return the car daily rate of Rs. 8,000
     */
    @Override
    public double getDailyRate() {
        return 8000.0; }

    /**
     * @return the car deposit of Rs. 20,000
     */
    @Override
    public double getDeposit() {
        return 20000.0; }
}
