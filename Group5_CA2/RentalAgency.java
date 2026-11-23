/*
 * Student Name: [D.M.T. SHEHAN]
 * Student ID: [24IT0530]
 * Group: 5
 */

/**
 * Coordinates vehicle registration, customer registration, rentals and billing.
 * Parallel arrays associate each vehicle with its current renter and agreed days.
 * Use this class to rent and return registered vehicles so all related state stays
 * consistent. Registration rejects duplicate identifiers using case-insensitive comparisons.
 */
public class RentalAgency {
    private final Vehicle[] vehicles;
    private final Customer[] customers;
    // Parallel arrays: index i refers to the rental of vehicles[i].
    private final Customer[] renters;
    private final int[] agreedDays;
    // Counters delimit occupied slots; unused array entries remain null.
    private int vehicleCount;
    private int customerCount;

    /**
     * Creates storage for up to 100 vehicles and 100 customers.
     */
    public RentalAgency() {
        this(100, 100);
    }

    /**
     * Creates fixed-size arrays; useful for both normal use and capacity tests.
     * @param vehicleCapacity maximum registered vehicles
     * @param customerCapacity maximum registered customers
     * @throws IllegalArgumentException if either capacity is not positive
     */
    public RentalAgency(int vehicleCapacity, int customerCapacity) {
        if (vehicleCapacity <= 0 || customerCapacity <= 0) {
            throw new IllegalArgumentException("Array capacities must be positive.");
        }
        vehicles = new Vehicle[vehicleCapacity];
        customers = new Customer[customerCapacity];
        renters = new Customer[vehicleCapacity];
        agreedDays = new int[vehicleCapacity];
    }

    /**
     * Searches only occupied array slots, ignoring case and outer whitespace.
     * @param plate plate to locate; null is treated as not found
     * @return the array index, or -1 when no match exists
     */
    private int findVehicleIndex(String plate) {
        if (plate == null) { return -1; }
        for (int i = 0; i < vehicleCount; i++) {
            if (vehicles[i].getPlate().equalsIgnoreCase(plate.strip())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Looks up a vehicle without modifying agency state.
     * @param plate registration plate to locate
     * @return the matching vehicle, or null if not found
     */
    public Vehicle findVehicle(String plate) {
        int index = findVehicleIndex(plate);
        return index < 0 ? null : vehicles[index];
    }

    /**
     * Looks up a customer, ignoring case and outer whitespace.
     * @param id customer identifier to locate
     * @return the matching customer, or null if not found
     */
    public Customer findCustomer(String id) {
        if (id == null) { return null; }
        for (int i = 0; i < customerCount; i++) {
            if (customers[i].getId().equalsIgnoreCase(id.strip())) {
                return customers[i];
            }
        }
        return null;
    }

    /**
     * Registers an available vehicle if storage and a unique plate are available.
     * @param vehicle vehicle to register
     * @return false for null, full storage, a duplicate plate or a rented vehicle
     */
    public boolean addVehicle(Vehicle vehicle) {
        if (vehicle == null || vehicleCount == vehicles.length
                || findVehicle(vehicle.getPlate()) != null || !vehicle.isAvailable()) {
            return false;
        }
        vehicles[vehicleCount++] = vehicle;
        return true;
    }

    /**
     * Registers a customer who has no existing rentals.
     * @param customer customer to register
     * @return false for null, full storage, duplicate ID or a nonzero rental count
     */
    public boolean addCustomer(Customer customer) {
        if (customer == null || customerCount == customers.length
                || findCustomer(customer.getId()) != null
                || customer.getCurrentRentals() != 0) {
            return false;
        }
        customers[customerCount++] = customer;
        return true;
    }

    // two-argument method: creates a one-day agreement by default.
    /**
     * Convenience overload that assumes a one-day agreement.
     * The menu uses the three-argument overload to capture the actual agreement.
     * @param customerId registered customer identifier
     * @param plate registered vehicle plate
     * @return true if the rental succeeds; otherwise false
     */
    public boolean rentVehicle(String customerId, String plate) {
        return rentVehicle(customerId, plate, 1);
    }

    //menu uses this overload so agreed days are stored at rental time.
    /**
     * Starts a rental after validating the agreement and both objects.
     * A rejected request leaves customer and vehicle state unchanged.
     * @param customerId registered customer identifier
     * @param plate registered vehicle plate
     * @param daysAgreed agreed duration from 1 to 30
     * @return true if rented; false if an ID is missing, the limit is reached or unavailable
     * @throws IllegalArgumentException if agreed days is outside 1-30
     */
    public boolean rentVehicle(String customerId, String plate, int daysAgreed) {
        Vehicle.validateDays(daysAgreed);
        Customer customer = findCustomer(customerId);
        int index = findVehicleIndex(plate);
        //prevents indexing with -1 or dereferencing null.
        if (customer == null || index < 0 || !customer.canRent()
                || renters[index] != null || !vehicles[index].isAvailable()) {
            return false;
        }
        if (!vehicles[index].rent()) { return false; }
        if (!customer.addRental()) {
            vehicles[index].giveBack(); // Roll back if the count could not change.
            return false;
        }
        // Record ownership only after both state changes have succeeded.
        renters[index] = customer;
        agreedDays[index] = daysAgreed;
        return true;
    }

    /**
     * Verifies that a vehicle has an active rental for the specified customer.
     * @param customerId expected renter identifier
     * @param plate rented vehicle plate
     * @return the matching vehicle index
     * @throws IllegalArgumentException if there is no matching active rental
     */
    private int requireRental(String customerId, String plate) {
        int index = findVehicleIndex(plate);
        Customer customer = findCustomer(customerId);
        if (customer == null || index < 0 || renters[index] != customer) {
            throw new IllegalArgumentException("No matching active rental for this customer.");
        }
        return index;
    }

    /**
     * Returns a vehicle using the agreement stored when it was rented.
     * @param customerId identifier of the renter
     * @param plate vehicle being returned
     * @param daysUsed actual duration from 1 to 30
     * @return rental charge in rupees, excluding the deposit
     * @throws IllegalArgumentException for invalid duration or no matching rental
     * @throws IllegalStateException if the customer rental count is inconsistent
     */
    public double returnVehicle(String customerId, String plate, int daysUsed) {
        int index = requireRental(customerId, plate);
        return returnVehicle(customerId, plate, daysUsed, agreedDays[index]);
    }

    /**
     * Validates a return, calculates its bill and then releases the vehicle.
     * @param customerId identifier of the renter
     * @param plate vehicle being returned
     * @param daysUsed actual duration from 1 to 30
     * @param daysAgreed original agreed duration from 1 to 30
     * @return rental charge in rupees, excluding the deposit
     * @throws IllegalArgumentException for invalid days, a mismatched agreement or rental
     * @throws IllegalStateException if the customer rental count cannot be decreased
     */
    public double returnVehicle(String customerId, String plate,
                                int daysUsed, int daysAgreed) {
        Vehicle.validateDays(daysUsed);
        Vehicle.validateDays(daysAgreed);
        int index = requireRental(customerId, plate);
        // Prevent an agreement from being changed at return time to avoid late fees.
        if (agreedDays[index] != daysAgreed) {
            throw new IllegalArgumentException("Agreed days must match the stored agreement.");
        }
        Vehicle vehicle = vehicles[index];
        // Each late day costs 120% of the standard rate, NOT base rate plus 120%.
        // Van discount applies only to the on-time portion, not the late charge.
        // Deposit is shown separately; it is not treated as rental income here.
        int onTimeDays = Math.min(daysUsed, daysAgreed);
        int lateDays = Math.max(0, daysUsed - daysAgreed);
        double bill = vehicle.calculateRental(onTimeDays)
                + lateDays * vehicle.getDailyRate() * 1.20;

        // Calculate and validate before releasing the vehicle or clearing its rental.
        if (!renters[index].removeRental()) {
            throw new IllegalStateException("Customer rental count is inconsistent.");
        }
        vehicle.giveBack();
        renters[index] = null;
        agreedDays[index] = 0;
        return bill;
    }

    /**
     * Displays only vehicles currently available for rent.
     */
    public void listAvailable() {
        showVehicles(true); }
    /**
     * Displays all registered vehicles, including those currently rented.
     */
    public void showAllVehicles() {
        showVehicles(false); }

    /**
     * Prints rates and deposits through the common Vehicle interface.
     * @param availableOnly true to hide rented vehicles, false to show all vehicles
     */
    private void showVehicles(boolean availableOnly) {
        boolean found = false;
        // Polymorphism: different subclasses in one Vehicle[] array.
        for (int i = 0; i < vehicleCount; i++) {
            Vehicle vehicle = vehicles[i];
            if (!availableOnly || vehicle.isAvailable()) {
                System.out.printf("%s | %s | %s | Rs. %.2f/day | Deposit Rs. %.2f | %s%n",
                        vehicle.getPlate(), vehicle.getBrand(),
                        vehicle.getClass().getSimpleName(), vehicle.getDailyRate(),
                        vehicle.getDeposit(), vehicle.isAvailable() ? "Available" : "Rented");
                found = true;
            }
        }
        if (!found) { System.out.println("No matching vehicles."); }
    }
}
