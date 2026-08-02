package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;

/** The vending features screen. */
public class VendingPanel extends JPanel {

    /** Version marker required of serializable Swing components. */
    private static final long serialVersionUID = 1L;

    private final JLabel machineLabel;
    private final JLabel insertedLabel;
    private final JTable itemTable;
    private final JTextArea outputArea;
    private final JButton[] denominationButtons;
    private final JButton refundButton;
    private final JButton buyButton;
    private final JButton ramenButton;
    private final JButton backButton;

    /**
     * Builds the vending screen.
     *
     * @param denominations the face values the machine accepts, smallest first
     */
    public VendingPanel(int[] denominations) {
        setLayout(new BorderLayout(10, 10));
        setBackground(UI.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        this.machineLabel = new JLabel("Vending Features");
        this.machineLabel.setFont(UI.TITLE_FONT);
        this.machineLabel.setForeground(UI.ACCENT);
        add(this.machineLabel, BorderLayout.NORTH);

        this.itemTable = UI.table(
                new String[]{"Slot", "Item", "Price", "Calories", "Stock", "Availability"});
        JScrollPane tableScroll = new JScrollPane(this.itemTable);
        tableScroll.setBorder(BorderFactory.createTitledBorder("Item slots"));
        tableScroll.setPreferredSize(new Dimension(560, 300));
        add(tableScroll, BorderLayout.CENTER);

        JPanel side = new JPanel(new BorderLayout(6, 6));
        side.setBackground(UI.BACKGROUND);
        side.setPreferredSize(new Dimension(270, 300));

        JPanel cashGrid = new JPanel(new GridLayout(0, 3, 4, 4));
        cashGrid.setBackground(UI.BACKGROUND);
        cashGrid.setBorder(BorderFactory.createTitledBorder("Insert cash"));
        this.denominationButtons = new JButton[denominations.length];
        for (int i = 0; i < denominations.length; i++) {
            this.denominationButtons[i] = UI.button("P" + denominations[i]);
            cashGrid.add(this.denominationButtons[i]);
        }
        side.add(cashGrid, BorderLayout.NORTH);

        JPanel actions = new JPanel(new GridLayout(0, 1, 4, 4));
        actions.setBackground(UI.BACKGROUND);
        this.insertedLabel = UI.heading("Inserted: Php 0");
        this.refundButton = UI.button("Return inserted cash");
        this.buyButton = UI.button("Buy selected item");
        this.ramenButton = UI.button("Build a custom ramen");
        this.backButton = UI.button("Back");
        actions.add(this.insertedLabel);
        actions.add(this.refundButton);
        actions.add(this.buyButton);
        actions.add(this.ramenButton);
        actions.add(this.backButton);
        side.add(actions, BorderLayout.CENTER);

        add(side, BorderLayout.EAST);

        this.outputArea = new JTextArea();
        add(UI.output(this.outputArea, 8), BorderLayout.SOUTH);
    }

    /**
     * Sets the heading describing the machine being tested.
     *
     * @param text the heading text
     */
    public void setMachineLabel(String text) {
        this.machineLabel.setText(text);
    }

    /**
     * Replaces the contents of the item table.
     *
     * @param rows the rows to display, in slot order
     */
    public void setItemRows(Object[][] rows) {
        UI.setRows(this.itemTable, rows);
    }

    /**
     * Returns the row the customer has selected.
     *
     * @return the selected row index, or -1 if nothing is selected
     */
    public int getSelectedRow() {
        return this.itemTable.getSelectedRow();
    }

    /**
     * Updates the running total of cash inserted.
     *
     * @param total the inserted total in pesos
     */
    public void setInsertedTotal(int total) {
        this.insertedLabel.setText("Inserted: Php " + total);
    }

    /**
     * Shows or hides the button that opens the ramen builder.
     *
     * @param visible true only for a special machine
     */
    public void setRamenButtonVisible(boolean visible) {
        this.ramenButton.setVisible(visible);
    }

    /**
     * Replaces everything shown in the machine output area.
     *
     * @param text the text to display
     */
    public void setOutput(String text) {
        this.outputArea.setText(text);
        this.outputArea.setCaretPosition(0);
    }

    /**
     * Appends a line to the machine output area.
     *
     * @param line the line to append
     */
    public void appendOutput(String line) {
        this.outputArea.append(line + System.lineSeparator());
        this.outputArea.setCaretPosition(this.outputArea.getDocument().getLength());
    }

    /**
     * Returns everything currently shown in the machine output area.
     *
     * @return the accumulated output text
     */
    public String getOutputText() {
        return this.outputArea.getText();
    }

    /**
     * Reports whether the ramen builder button is on screen.
     *
     * @return true if the button is visible
     */
    public boolean isRamenButtonVisible() {
        return this.ramenButton.isVisible();
    }

    /**
     * Returns how many denomination buttons the panel shows.
     *
     * @return the button count
     */
    public int getDenominationCount() {
        return this.denominationButtons.length;
    }

    /**
     * Registers the handler for one denomination button.
     *
     * @param index    the denomination index the handler belongs to
     * @param listener the handler to attach
     */
    public void addDenominationListener(int index, ActionListener listener) {
        this.denominationButtons[index].addActionListener(listener);
    }

    /**
     * Registers the handler for returning the inserted cash.
     *
     * @param listener the handler to attach
     */
    public void addRefundListener(ActionListener listener) {
        this.refundButton.addActionListener(listener);
    }

    /**
     * Registers the handler for buying the selected item.
     *
     * @param listener the handler to attach
     */
    public void addBuyListener(ActionListener listener) {
        this.buyButton.addActionListener(listener);
    }

    /**
     * Registers the handler for opening the ramen builder.
     *
     * @param listener the handler to attach
     */
    public void addRamenListener(ActionListener listener) {
        this.ramenButton.addActionListener(listener);
    }

    /**
     * Registers the handler for leaving the vending features.
     *
     * @param listener the handler to attach
     */
    public void addBackListener(ActionListener listener) {
        this.backButton.addActionListener(listener);
    }
}
