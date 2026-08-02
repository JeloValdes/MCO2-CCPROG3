package view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionListener;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * The screen that chooses between the vending features and the maintenance features of the machine currently under test.
 */
public class TestPanel extends JPanel {

    /** Version marker required of serializable Swing components. */
    private static final long serialVersionUID = 1L;

    private final JLabel machineLabel;
    private final JButton vendingButton;
    private final JButton maintenanceButton;
    private final JButton backButton;

    /** Builds the test menu screen. */
    public TestPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UI.BACKGROUND);

        JPanel column = new JPanel();
        column.setLayout(new BoxLayout(column, BoxLayout.Y_AXIS));
        column.setBackground(UI.BACKGROUND);

        this.machineLabel = new JLabel("", SwingConstants.CENTER);
        this.machineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        this.vendingButton = UI.menuButton("Test Vending Features");
        this.maintenanceButton = UI.menuButton("Test Maintenance Features");
        this.backButton = UI.menuButton("Back to Factory");

        column.add(UI.gap(60));
        column.add(UI.title("Test a Vending Machine"));
        column.add(UI.gap(10));
        column.add(this.machineLabel);
        column.add(UI.gap(30));
        column.add(this.vendingButton);
        column.add(UI.gap(10));
        column.add(this.maintenanceButton);
        column.add(UI.gap(10));
        column.add(this.backButton);

        add(column, BorderLayout.CENTER);
    }

    /**
     * Updates the line describing the machine under test.
     *
     * @param text the description to display
     */
    public void setMachineLabel(String text) {
        this.machineLabel.setText(text);
    }

    /**
     * Registers the handler for opening the vending features.
     *
     * @param listener the handler to attach
     */
    public void addVendingListener(ActionListener listener) {
        this.vendingButton.addActionListener(listener);
    }

    /**
     * Registers the handler for opening the maintenance features.
     *
     * @param listener the handler to attach
     */
    public void addMaintenanceListener(ActionListener listener) {
        this.maintenanceButton.addActionListener(listener);
    }

    /**
     * Registers the handler for returning to the factory menu.
     *
     * @param listener the handler to attach
     */
    public void addBackListener(ActionListener listener) {
        this.backButton.addActionListener(listener);
    }
}
