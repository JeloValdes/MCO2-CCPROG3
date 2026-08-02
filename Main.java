import java.util.Scanner;

/**
 * Entry point for the Vending Machine Factory simulator.
 *
 * <p>This phase (MCO1) simulates a factory that creates <b>Regular</b> vending
 * machines only. The program presents a menu that lets the user create a
 * vending machine, test the vending or maintenance features of the most
 * recently created machine, or exit the program.</p>
 */
public class Main {

    /**
     * Runs the top-level menu loop of the simulator.
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Input input = new Input(scanner);
        VendingMachine current = null; // the most recently created machine

        int running = 1;
        while (running == 1) {
            System.out.println();
            System.out.println("========== VENDING MACHINE FACTORY ==========");
            System.out.println("Create a Vending Machine [1]");
            System.out.println("Test a Vending Machine [2]");
            System.out.println("Exit [3]");
            System.out.println("Enter choice: ");
            int choice = input.receive(1, 3);

            if (choice == 1) {
                current = new VendingMachine(scanner);
                System.out.println("A new Regular Vending Machine has been created.");
            } else if (choice == 2) {
                if (current == null) {
                    System.out.println("No vending machine exists yet. Please create one first.");
                } else {
                    testMachine(current, input);
                }
            } else {
                running = 0;
                System.out.println("Program terminated. Goodbye!");
            }
        }

        scanner.close();
    }

    /**
     * Presents the "Test a Vending Machine" sub-menu for the given machine,
     * letting the user test the vending features or the maintenance features
     * until they choose to go back to the main menu.
     *
     * @param machine the vending machine to test (the most recently created)
     * @param input   the helper used to read validated menu choices
     */
    private static void testMachine(VendingMachine machine, Input input) {
        int back = 0;
        while (back != 1) {
            System.out.println();
            System.out.println("----- Test a Vending Machine -----");
            System.out.println("Test Vending Features [1]");
            System.out.println("Test Maintenance Features [2]");
            System.out.println("Back [3]");
            System.out.println("Enter choice: ");
            int choice = input.receive(1, 3);

            if (choice == 1) {
                machine.vending();
            } else if (choice == 2) {
                machine.maintenance();
            } else {
                back = 1;
            }
        }
    }
}
