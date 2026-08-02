package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * The opening screen of the simulator.
 *
 * <p>From here the user creates a machine of either type or moves on to test
 * the machine most recently created. The checkbox chooses between an empty
 * machine and one preloaded with sample items, which mirrors the two
 * constructors offered by the factory controller.</p>
 */
public class FactoryPanel extends JPanel {

    /** Version marker required of serializable Swing components. */
    private static final long serialVersionUID = 1L;

    private final JLabel statusLabel;
    private final JCheckBox defaultsBox;
    private final JButton regularButton;
    private final JButton specialButton;
    private final JButton testButton;
    private final JButton exitButton;

    /**
     * Builds the factory menu screen.
     */
    public FactoryPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UI.BACKGROUND);

        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setBackground(UI.BACKGROUND);

        this.statusLabel = new JLabel("No vending machine has been created yet.", SwingConstants.CENTER);
        this.statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.defaultsBox = new JCheckBox("Load the sample items when creating", true);
        this.defaultsBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        this.defaultsBox.setBackground(UI.BACKGROUND);

        this.regularButton = UI.menuButton("Create a Regular Vending Machine");
        this.specialButton = UI.menuButton("Create a Special Vending Machine");
        this.testButton = UI.menuButton("Test the Current Vending Machine");
        this.exitButton = UI.menuButton("Exit");

        column.add(UI.gap(40));
        column.add(UI.title("Vending Machine Factory"));
        column.add(UI.gap(10));
        column.add(this.statusLabel);
        column.add(UI.gap(20));
        column.add(this.defaultsBox);
        column.add(UI.gap(16));
        column.add(this.regularButton);
        column.add(UI.gap(10));
        column.add(this.specialButton);
        column.add(UI.gap(10));
        column.add(this.testButton);
        column.add(UI.gap(10));
        column.add(this.exitButton);

        add(column, BorderLayout.CENTER);
    }

    /**
     * Updates the line describing the machine held by the factory.
     *
     * @param text the status text to display
     */
    public void setStatus(String text) {
        this.statusLabel.setText(text);
    }

    /**
     * Enables or disables the button that opens the test menu.
     *
     * @param enabled true when a machine exists to be tested
     */
    public void setTestEnabled(boolean enabled) {
        this.testButton.setEnabled(enabled);
    }

    /**
     * Reports whether the user asked for the sample items to be loaded.
     *
     * @return true if the defaults checkbox is ticked
     */
    public boolean isLoadDefaultsSelected() {
        return this.defaultsBox.isSelected();
    }

    /**
     * Registers the handler for creating a regular machine.
     *
     * @param listener the handler to attach
     */
    public void addRegularListener(ActionListener listener) {
        this.regularButton.addActionListener(listener);
    }

    /**
     * Registers the handler for creating a special machine.
     *
     * @param listener the handler to attach
     */
    public void addSpecialListener(ActionListener listener) {
        this.specialButton.addActionListener(listener);
    }

    /**
     * Registers the handler for opening the test menu.
     *
     * @param listener the handler to attach
     */
    public void addTestListener(ActionListener listener) {
        this.testButton.addActionListener(listener);
    }

    /**
     * Registers the handler for exiting the program.
     *
     * @param listener the handler to attach
     */
    public void addExitListener(ActionListener listener) {
        this.exitButton.addActionListener(listener);
    }
}
