/*
 * Student Name: [R.D.S. MIHIRANGA]
 * Student ID: [24IT0489]
 * Group: 5
 */

import java.util.Scanner;

/**
 * Input helpers retry invalid entries; RentalAgency performs business operations.
 * All data is in memory and is discarded when the application exits.
 */
public class RentalApp {
    private final Scanner scanner;
    private final RentalAgency agency;

    public RentalApp() {
        scanner = new Scanner(System.in);
        agency = new RentalAgency();
    }

    public static void main(String[] args) {
        new RentalApp().run();
    }

    /**
     * Repeats the menu until Exit or end-of-input.
     * Expected operation errors are displayed without terminating the menu.
     */
    public void run() {
        try {
            boolean running = true;
            while (running) {
                System.out.println("\n--- VEHICLE RENTAL SYSTEM ---");
                System.out.println("1. Add vehicle");
                System.out.println("2. Add customer");
                System.out.println("3. Rent vehicle");
                System.out.println("4. Return vehicle and show bill");
                System.out.println("5. List available vehicles");
                System.out.println("6. Show all vehicles");
                System.out.println("0. Exit");
                int choice = readInt("Choose: ", 0, 6);
                try {
                    switch (choice) {
                        case 1 -> addVehicle();
                        case 2 -> addCustomer();
                        case 3 -> rentVehicle();
                        case 4 -> returnVehicle();
                        case 5 -> agency.listAvailable();
                        case 6 -> agency.showAllVehicles();
                        case 0 -> running = false;
                        default -> System.out.println("Invalid option.");
                    }
                } catch (IllegalArgumentException | IllegalStateException error) {
                    System.out.println("Cannot complete operation: " + error.getMessage());
                }
            }
        } catch (java.util.NoSuchElementException error) {
            System.out.println("\nInput ended. Closing application.");
        } finally {
            scanner.close();
        }
        System.out.println("Goodbye.");
    }

    /**
     * Retries blank input and removes surrounding whitespace.
     * @param prompt message displayed before reading a line
     * @return a non-blank trimmed value
     * @throws java.util.NoSuchElementException when input ends; handled by run()
     */
    private String readText(String prompt) {
        while (true) {
            System.out.print(prompt);
            // Reading whole lines consistently avoids leftover-newline problems.
            String value = scanner.nextLine().strip();
            if (!value.isEmpty()) { return value; }
            System.out.println("Please enter a non-blank value.");
        }
    }

    /**
     * Retries letters, out-of-range values and integer overflow.
     * @param prompt message displayed before reading
     * @param min inclusive lower limit
     * @param max inclusive upper limit
     * @return a whole number within the requested range
     */
    private int readInt(String prompt, int min, int max) {
        while (true) {
            try {
                int value = Integer.parseInt(readText(prompt));
                if (value >= min && value <= max) { return value; }
            } catch (NumberFormatException error) {
                // Retry rather than allowing invalid numeric input to crash the app.
            }
            System.out.printf("Enter a whole number from %d to %d.%n", min, max);
        }
    }

    /**
     * Retries invalid text, NaN, infinity and values outside the allowed sign range.
     * @param prompt message displayed before reading
     * @param zeroAllowed whether zero is accepted
     * @return a finite positive value, or a non-negative value when zeroAllowed is true
     */
    private double readDouble(String prompt, boolean zeroAllowed) {
        while (true) {
            try {
                double value = Double.parseDouble(readText(prompt));
                if (Double.isFinite(value) && (zeroAllowed ? value >= 0 : value > 0)) {
                    return value;
                }
            } catch (NumberFormatException error) {
                // Invalid text is handled by the message below.
            }
            System.out.println(zeroAllowed
                    ? "Enter a finite number of zero or more."
                    : "Enter a finite positive number.");
        }
    }

    /**
     * Reads type-specific details and registers the selected vehicle subclass.
     * The van deposit is entered explicitly because the brief leaves it unspecified.
     */
    private void addVehicle() {
        int type = readInt("1 Car / 2 Motorcycle / 3 Van: ", 1, 3);
        String plate = readText("Plate: ");
        String brand = readText("Brand: ");
        Vehicle vehicle;
        if (type == 1) {
            vehicle = new Car(plate, brand, readInt("Seats: ", 1, Integer.MAX_VALUE));
        } else if (type == 2) {
            vehicle = new Motorcycle(plate, brand,
                    readInt("Engine cc: ", 1, Integer.MAX_VALUE));
        } else {
            double capacity = readDouble("Load capacity (kg): ", false);
            double deposit = readDouble("Team-approved van deposit (Rs.): ", true);
            vehicle = new Van(plate, brand, capacity, deposit);
        }
        System.out.println(agency.addVehicle(vehicle)
                ? "Vehicle added." : "Vehicle not added: duplicate plate or capacity reached.");
    }

    /**
     * Reads customer details, creates a validated object and reports registration status.
     */
    private void addCustomer() {
        Customer customer = new Customer(readText("Customer ID: "),
                readText("Name: "), readText("Licence number: "));
        System.out.println(agency.addCustomer(customer)
                ? "Customer added." : "Customer not added: duplicate ID or capacity reached.");
    }

    /**
     * Reads an agreement, requests a rental and prints an estimate on success.
     * The displayed deposit is separate from the estimated rental charge.
     */
    private void rentVehicle() {
        String id = readText("Customer ID: ");
        String plate = readText("Vehicle plate: ");
        int days = readInt("Agreed days (1-30): ", 1, 30);
        if (agency.rentVehicle(id, plate, days)) {
            Vehicle vehicle = agency.findVehicle(plate);
            System.out.printf("Rental successful. Estimate Rs. %.2f; separate deposit Rs. %.2f.%n",
                    vehicle.calculateRental(days), vehicle.getDeposit());
        } else {
            System.out.println("Rental rejected: check IDs, availability and two-vehicle limit.");
        }
    }

    /**
     * Reads the renter, plate and actual days; displays the agency-calculated bill.
     */
    private void returnVehicle() {
        String id = readText("Customer ID: ");
        String plate = readText("Vehicle plate: ");
        int days = readInt("Actual days used (1-30): ", 1, 30);
        double bill = agency.returnVehicle(id, plate, days);
        System.out.printf("Vehicle returned. Rental charge: Rs. %.2f (deposit excluded).%n", bill);
    }
}
