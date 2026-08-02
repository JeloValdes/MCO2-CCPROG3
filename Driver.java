import controller.FactoryController;
import javax.swing.SwingUtilities;
import model.IngredientType;
import model.VendingMachine;
import view.MainFrame;
import view.UI;

/**
 * Entry point of the Vending Machine Factory simulator.
 *
 * <p>The driver does one job: it builds the window, builds the top-level
 * controller, and shows the result on the Swing event dispatch thread. It holds
 * no simulation logic, so the model, the view, and the controllers stay
 * independent of how the program happens to be started.</p>
 */
public class Driver {

    /**
     * Prevents instantiation of this launcher class.
     */
    private Driver() {
    }

    /**
     * Starts the simulator.
     *
     * @param args unused command-line arguments
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            int[] denominations = VendingMachine.DENOMINATIONS;

            String[] denominationNames = new String[denominations.length];
            for (int i = 0; i < denominations.length; i++) {
                denominationNames[i] = UI.denominationLabel(denominations[i]);
            }

            IngredientType[] types = IngredientType.values();
            String[] ingredientNames = new String[types.length];
            for (int i = 0; i < types.length; i++) {
                ingredientNames[i] = types[i].name();
            }

            MainFrame frame = new MainFrame(denominations, denominationNames, ingredientNames);
            new FactoryController(frame);
            frame.setVisible(true);
        });
    }
}
