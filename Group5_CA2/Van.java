/*
 * Student Name: [D.M.I.M. DISANAYAKE]
 * Student ID: [24IT0466]
 * Group: 5
 */

/**
 * Represents a van with a load capacity and a configurable deposit.
 * Overrides rental calculation to apply a 10% discount for seven or more days.
 */
public class Van extends Vehicle {
    private final double loadCapacity;
    private final double deposit;

    // The brief does not specify a van deposit; supply a team-approved value.
    /**
     * Creates a van using a deposit chosen by the team or operator.
     * @param plate non-blank registration plate
     * @param brand non-blank brand
     * @param loadCapacity positive finite load capacity in kilograms
     * @param deposit finite, non-negative deposit in rupees
     * @throws IllegalArgumentException if identity, capacity or deposit is invalid
     */
    public Van(String plate, String brand, double loadCapacity, double deposit) {
        super(plate, brand);
        if (!Double.isFinite(loadCapacity) || loadCapacity <= 0) {
            throw new IllegalArgumentException("Load capacity must be positive.");
        }
        if (!Double.isFinite(deposit) || deposit < 0) {
            throw new IllegalArgumentException("Deposit must be finite and non-negative.");
        }
        this.loadCapacity = loadCapacity;
        this.deposit = deposit;
    }

    /**
     * @return the load capacity in kilograms
     */
    public double getLoadCapacity() { 
        return loadCapacity; }

    /**
     * @return the van daily rate of Rs. 12,000
     */
    @Override
    public double getDailyRate() { 
        return 12000.0; }

    /**
     * @return the configured deposit, separate from the rental charge
     */
    @Override
    public double getDeposit() { 
        return deposit; }

    /**
     * Reuses base validation and pricing, then applies the van discount.
     * @param days rental duration from 1 to 30
     * @return standard charge for 1-6 days, or 90% of that charge for 7-30 days
     * @throws IllegalArgumentException if days is outside the allowed range
     */
    @Override
    public double calculateRental(int days) {
        // Reuse the parent day-range check; avoid duplicating validation logic.
        double amount = super.calculateRental(days);
        return days >= 7 ? amount * 0.90 : amount;
    }
}
