/*
 * Student Name: [H.S.K.H. HEWASINGHA]
 * Student ID: [24IT0475]
 * Group: 5
 */

/**
 * Runs repeatable PASS/FAIL checks without external testing libraries.
 * validation, limits, rental ownership, billing and polymorphism.
 * Run with java RentalTest; any failed check causes an AssertionError at the end.
 */
public class RentalTest {
    private static int passed;
    private static int failed;

    /**
     * Prints and counts one assertion without stopping subsequent checks.
     * @param name readable description of the behaviour being checked
     * @param condition true when the actual result matches expectations
     */
    private static void check(String name, boolean condition) {
        System.out.println((condition ? "PASS: " : "FAIL: ") + name);
        if (condition) { passed++; } else { failed++; }
    }

    /**
     * Compares double results using a small tolerance for rounding differences.
     * @param actual calculated result
     * @param expected reference result
     * @return true if the absolute difference is less than 0.001
     */
    private static boolean close(double actual, double expected) {
        return Math.abs(actual - expected) < 0.001;
    }

    /**
     * Checks an invalid operation throws IllegalArgumentException.
     * Other unexpected exception types are not swallowed.
     * @param name description of the validation case
     * @param action operation expected to reject its input
     */
    private static void rejects(String name, Runnable action) {
        try {
            action.run();
            check(name, false);
        } catch (IllegalArgumentException expected) {
            check(name, true);
        }
    }

    /**
     * Runs test scenarios and prints their total results.
     * @throws AssertionError if one or more checks fail
     */
    public static void main(String[] args) {
        // Customer boundaries: empty state, two-rental limit and invalid identity.
        Customer customer = new Customer("C1", "Kamal", "DEMO1");
        check("New customer starts at zero", customer.getCurrentRentals() == 0);
        check("Cannot remove below zero", !customer.removeRental());
        check("First rental", customer.addRental());
        check("Second rental", customer.addRental());
        check("Third rental rejected without count change",
                !customer.addRental() && customer.getCurrentRentals() == 2);
        check("Return frees one place", customer.removeRental() && customer.canRent());
        rejects("Blank ID", () -> new Customer(" ", "Name", "L1"));
        rejects("Null name", () -> new Customer("C2", null, "L1"));
        rejects("Blank licence", () -> new Customer("C2", "Name", " "));

        // Vehicle prices and boundary days, including the van discount threshold.
        Car car = new Car("CAR1", "Toyota", 5);
        Motorcycle bike = new Motorcycle("BIKE1", "Honda", 125);
        Van van = new Van("VAN1", "Nissan", 1000, 15000); // Test-only deposit.
        check("Car rate and deposit", close(car.getDailyRate(), 8000)
                && close(car.getDeposit(), 20000));
        check("Motorcycle rate and deposit", close(bike.getDailyRate(), 3000)
                && close(bike.getDeposit(), 5000));
        check("Van six-day price", close(van.calculateRental(6), 72000));
        check("Van seven-day discount boundary", close(van.calculateRental(7), 75600));
        check("Thirty days accepted", close(car.calculateRental(30), 240000));
        rejects("Zero rental days", () -> car.calculateRental(0));
        rejects("Thirty-one rental days", () -> van.calculateRental(31));
        rejects("Negative seats", () -> new Car("X", "Brand", -1));
        rejects("Zero engine cc", () -> new Motorcycle("X", "Brand", 0));
        rejects("NaN load capacity", () -> new Van("X", "Brand", Double.NaN, 0));
        rejects("Negative deposit", () -> new Van("X", "Brand", 100, -1));
        check("Rent available vehicle", car.rent());
        check("Reject double rental", !car.rent());
        car.giveBack();
        check("Return restores availability", car.isAvailable());

        // Agency integration: registration, capacity, ownership and rental limits.
        RentalAgency agency = new RentalAgency(3, 2);
        Customer first = new Customer("A", "Amali", "L-A");
        Customer second = new Customer("B", "Nimal", "L-B");
        check("Add three vehicle types", agency.addVehicle(car)
                && agency.addVehicle(bike) && agency.addVehicle(van));
        check("Vehicle array capacity", !agency.addVehicle(new Car("NEW", "Brand", 4)));
        check("Add customers", agency.addCustomer(first) && agency.addCustomer(second));
        check("Reject duplicate plate", !agency.addVehicle(new Car("car1", "Other", 4)));
        check("Reject duplicate ID", !agency.addCustomer(new Customer("a", "Other", "L")));
        check("Customer capacity", !agency.addCustomer(new Customer("C", "Other", "L")));
        check("Missing customer rejected", !agency.rentVehicle("UNKNOWN", "CAR1", 2));
        check("Missing vehicle rejected", !agency.rentVehicle("A", "UNKNOWN", 2));
        check("Rent car", agency.rentVehicle("A", "CAR1", 2));
        check("Another customer cannot rent same car", !agency.rentVehicle("B", "CAR1", 2)
                && second.getCurrentRentals() == 0);
        check("Rent motorcycle", agency.rentVehicle("A", "BIKE1", 2));
        check("Agency enforces max two", !agency.rentVehicle("A", "VAN1", 7)
                && van.isAvailable());
        // Failed returns must preserve both availability and the customer count.
        rejects("Wrong customer return", () -> agency.returnVehicle("B", "CAR1", 2));
        rejects("Invalid days leave rental unchanged", () -> agency.returnVehicle("A", "CAR1", 0));
        rejects("Cannot change agreed days at return", () -> agency.returnVehicle("A", "CAR1", 3, 3));
        check("Rejected returns preserve state", !car.isAvailable() && first.getCurrentRentals() == 2);
        // Billing assertions document the current early/late-return assumptions.
        check("Late car bill: 2*8000 + 1*9600", close(agency.returnVehicle("A", "CAR1", 3), 25600));
        check("Return updates both objects", car.isAvailable() && first.getCurrentRentals() == 1);
        rejects("Double return rejected", () -> agency.returnVehicle("A", "CAR1", 3));
        check("Early motorcycle return bills actual days", close(agency.returnVehicle("A", "BIKE1", 1), 3000));
        check("Rent van for seven days", agency.rentVehicle("A", "VAN1", 7));
        check("Late van bill: discounted 7 days plus one late day",
                close(agency.returnVehicle("A", "VAN1", 8), 90000));
        check("Two-argument rent defaults to one day", agency.rentVehicle("B", "CAR1"));
        check("On-time bill", close(agency.returnVehicle("B", "CAR1", 1, 1), 8000));

        // Base-type array calls the overridden implementations without type checks.
        Vehicle[] mixed = {car, bike, van};
        double total = 0;
        for (Vehicle vehicle : mixed) { total += vehicle.calculateRental(7); }
        check("Polymorphic seven-day totals", close(total, 152600));
        // Spare capacity isolates duplicate validation from the full-array check.
        RentalAgency duplicateCheck = new RentalAgency(5, 5);
        duplicateCheck.addVehicle(car);
        duplicateCheck.addCustomer(new Customer("ID", "Name", "L"));
        check("Duplicate plate rejected while array has space",
                !duplicateCheck.addVehicle(new Car("car1", "Brand", 4)));
        check("Duplicate ID rejected while array has space",
                !duplicateCheck.addCustomer(new Customer("id", "Name", "L")));
        System.out.printf("%nResults: %d passed, %d failed.%n", passed, failed);
        if (failed > 0) { throw new AssertionError("Some tests failed."); }
    }
}
