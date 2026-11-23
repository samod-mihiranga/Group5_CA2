/*
 * Student Name: [P.N.N.DE SILVA]
 * Student ID: [24IT0462]
 * Group: 5
 */

/**
 * Represents a motorcycle with a positive engine capacity in cubic centimetres.
 * Inherits rental state from Vehicle and supplies its own rate and deposit.
 */
public class Motorcycle extends Vehicle {
    private final int engineCc;

    /**
     * Creates a motorcycle after validating identity and engine capacity.
     * @param plate non-blank registration plate
     * @param brand non-blank brand
     * @param engineCc positive engine capacity in cubic centimetres
     * @throws IllegalArgumentException if identity or engine capacity is invalid
     */
    public Motorcycle(String plate, String brand, int engineCc) {
        super(plate, brand);
        if (engineCc <= 0) {
            throw new IllegalArgumentException("Engine cc must be positive.");
        }
        this.engineCc = engineCc;
    }

    /**
     * @return engine capacity in cubic centimetres
     */
    public int getEngineCc() {
        return engineCc; }

    /**
     * @return the motorcycle daily rate of Rs. 3,000
     */
    @Override
    public double getDailyRate() {
        return 3000.0; }

    /**
     * @return the motorcycle deposit of Rs. 5,000
     */
    @Override
    public double getDeposit() {
        return 5000.0; }
}
