/*
 * Student Name: [R.L.H.C. SILVA]
 * Student ID: [24IT0531]
 * Group: 5
 */

/**
 * Stores customer identity and the number of current rentals.
 * Validated methods keep the count between zero and two. This class does not
 * track which vehicles are rented; RentalAgency owns that relationship.
 */
public class Customer {
   
    private final String id;
    private final String name;
    private final String licenceNumber;
    private int currentRentals;

    /**
     * Creates a customer with no active rentals and trims identity values.
     * Licence validation checks only null/blank input, not a legal licence format.
     * @param id non-blank customer id
     * @param name non-blank customer name
     * @param licenceNumber non-blank driving licence text
     * @throws IllegalArgumentException if any supplied value is null or blank
     */
    public Customer(String id, String name, String licenceNumber) {
        if (id == null || id.isBlank()) {
            throw new IllegalArgumentException("ID must not be blank.");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Name must not be blank.");
        }
        
        if (licenceNumber == null || licenceNumber.isBlank()) {
            throw new IllegalArgumentException("Licence number must not be blank.");
        }
        this.id = id.strip();
        this.name = name.strip();
        this.licenceNumber = licenceNumber.strip();
        this.currentRentals = 0;
    }

    /**
     * @return the customer id
     */
    public String getId() { 
        return id; }
    /**
     * @return the customer name
     */
    public String getName() { 
        return name; }
    /**
     * @return the stored licence text
     */
    public String getLicenceNumber() { 
        return licenceNumber; }
    /**
     * @return the active rental count, from zero to two
     */
    public int getCurrentRentals() { 
        return currentRentals; }
    /**
     * Checks the limit without changing the rental count.
     * @return true if fewer than two rentals are active
     */
    public boolean canRent() { 
        return currentRentals < 2; }

    /**
     * Increments the count.
     * RentalAgency calls during a successful rental, not for a rejected request.
     * @return true if incremented, or false if already at the two-rental limit
     */
    public boolean addRental() {
        if (!canRent()) {
            return false;
        }
        currentRentals++;
        return true;
    }

    /**
     * Decrements the count without allowing it to become negative.
     * RentalAgency must first verify which vehicle belongs to this customer.
     * @return true if decremented, or false if the count is already zero
     */
    
    public boolean removeRental() {
        if (currentRentals <= 0) {
            return false;
        }
        currentRentals--;
        return true;
    }
}
