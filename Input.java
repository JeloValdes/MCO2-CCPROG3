import java.util.Scanner;

/**
 * Helper class that reads and validates integer input from the console.
 *
 * <p>All read methods keep prompting until valid input is provided, so the
 * rest of the program can assume the values it receives are already within
 * the required bounds. A single shared {@link Scanner} is used so that the
 * input stream stays consistent across the whole program.</p>
 */
public class Input {

    private final Scanner scanner;

    /**
     * Creates an input helper that reads from the given scanner.
     * @param scanner the shared scanner reading from standard input
     */
    public Input(Scanner scanner) {
        this.scanner = scanner;
    }

    /**
     * Reads an integer that lies within the inclusive range
     * [{@code minimum}, {@code maximum}], re-prompting on invalid input.
     *
     * @param minimum the smallest accepted value
     * @param maximum the largest accepted value (pre: minimum &lt;= maximum)
     * @return a valid integer within the given range
     */
    public int receive(int minimum, int maximum) {
        while (true) {
            if (scanner.hasNextInt()) {
                int typed = scanner.nextInt();
                scanner.nextLine(); // consumes leftover Enter

                if (typed >= minimum && typed <= maximum) {
                    return typed;
                }

                System.out.println(
                        "Error. Please enter a number between "
                                + minimum + " and " + maximum + ".");
            } else {
                String invalidInput = scanner.nextLine();

                System.out.println(
                        "Error. \"" + invalidInput
                                + "\" is not a valid integer.");
            }
        }
    }

    /**
     * Reads any integer, re-prompting on non-integer input.
     * @return the integer that was entered
     */
    public int receiveInt() {
        while (true) {
            if (scanner.hasNextInt()) {
                int typed = scanner.nextInt();
                scanner.nextLine(); // consumes leftover Enter
                return typed;
            }

            String invalidInput = scanner.nextLine();

            System.out.println(
                    "Error. \"" + invalidInput
                            + "\" is not a valid integer.");
        }
    }

    /**
     * Reads a non-negative integer (zero or greater), re-prompting otherwise.
     * @return a valid integer that is &gt;= 0
     */
    public int receiveNonNegative() {
        while (true) {
            int typed = receiveInt();

            if (typed >= 0) {
                return typed;
            }

            System.out.println(
                    "Error. Please enter zero or a positive integer.");
        }
    }

    /**
     * Reads a strictly positive integer (greater than zero), re-prompting
     * otherwise.
     * @return a valid integer that is &gt; 0
     */
    public int receivePositive() {
        while (true) {
            int typed = receiveInt();

            if (typed > 0) {
                return typed;
            }

            System.out.println(
                    "Error. Please enter a positive integer.");
        }
    }
}
