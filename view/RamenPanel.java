package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SpinnerNumberModel;

/** The custom ramen builder, offered only by a special vending machine. */
public class RamenPanel extends JPanel {

    /** Version marker required of serializable Swing components. */
    private static final long serialVersionUID = 1L;

    private final JLabel machineLabel;
    private final JLabel totalsLabel;
    private final JLabel insertedLabel;
    private final JTable ingredientTable;
    private final JTable bowlTable;
    private final JSpinner quantitySpinner;
    private final JTextArea outputArea;
    private final JButton addButton;
    private final JButton removeButton;
    private final JButton clearButton;
    private final JButton prepareButton;
    private final JButton backButton;

    /** Builds the ramen builder screen. */
    public RamenPanel() {
        setLayout(new BorderLayout(10, 10));
        setBackground(UI.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        this.machineLabel = new JLabel("Build a Custom Ramen");
        this.machineLabel.setFont(UI.TITLE_FONT);
        this.machineLabel.setForeground(UI.ACCENT);
        add(this.machineLabel, BorderLayout.NORTH);

        JPanel middle = new JPanel(new GridLayout(1, 2, 10, 10));
        middle.setBackground(UI.BACKGROUND);

        this.ingredientTable = UI.table(
                new String[]{"Slot", "Item", "Role", "Price", "Calories", "Stock"});
        JScrollPane left = new JScrollPane(this.ingredientTable);
        left.setBorder(BorderFactory.createTitledBorder("Available ingredients"));
        left.setPreferredSize(new Dimension(400, 250));
        middle.add(left);

        this.bowlTable = UI.table(new String[]{"Ingredient", "Orders", "Subtotal"});
        JScrollPane right = new JScrollPane(this.bowlTable);
        right.setBorder(BorderFactory.createTitledBorder("Your bowl"));
        right.setPreferredSize(new Dimension(400, 250));
        middle.add(right);

        add(middle, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(6, 6));
        south.setBackground(UI.BACKGROUND);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        controls.setBackground(UI.BACKGROUND);
        this.quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        this.quantitySpinner.setPreferredSize(new Dimension(60, 26));
        this.addButton = UI.button("Add to bowl");
        this.removeButton = UI.button("Remove from bowl");
        this.clearButton = UI.button("Clear bowl");
        this.prepareButton = UI.button("Prepare and buy");
        this.backButton = UI.button("Back");
        controls.add(new JLabel("Orders:"));
        controls.add(this.quantitySpinner);
        controls.add(this.addButton);
        controls.add(this.removeButton);
        controls.add(this.clearButton);
        controls.add(this.prepareButton);
        controls.add(this.backButton);

        JPanel totals = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 2));
        totals.setBackground(UI.BACKGROUND);
        this.totalsLabel = UI.heading("Price: Php 0    Calories: 0");
        this.insertedLabel = UI.heading("Inserted: Php 0");
        totals.add(this.totalsLabel);
        totals.add(this.insertedLabel);

        south.add(controls, BorderLayout.NORTH);
        south.add(totals, BorderLayout.CENTER);

        this.outputArea = new JTextArea();
        south.add(UI.output(this.outputArea, 7), BorderLayout.SOUTH);

        add(south, BorderLayout.SOUTH);
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
     * Replaces the table of ingredients the machine can offer.
     *
     * @param rows the rows to display, in slot order
     */
    public void setIngredientRows(Object[][] rows) {
        UI.setRows(this.ingredientTable, rows);
    }

    /**
     * Replaces the table describing the bowl assembled so far.
     *
     * @param rows the rows to display, in the order ingredients were chosen
     */
    public void setBowlRows(Object[][] rows) {
        UI.setRows(this.bowlTable, rows);
    }

    /**
     * Returns the ingredient row currently selected on the left.
     *
     * @return the selected row index, or -1 if nothing is selected
     */
    public int getSelectedIngredientRow() {
        return this.ingredientTable.getSelectedRow();
    }

    /**
     * Returns the bowl row currently selected on the right.
     *
     * @return the selected row index, or -1 if nothing is selected
     */
    public int getSelectedBowlRow() {
        return this.bowlTable.getSelectedRow();
    }

    /**
     * Returns the number of orders chosen in the quantity spinner.
     *
     * @return the requested quantity
     */
    public int getRequestedQuantity() {
        return (Integer) this.quantitySpinner.getValue();
    }

    /**
     * Updates the running price and calorie count of the bowl.
     *
     * @param price    the current price in pesos
     * @param calories the current combined calorie count
     */
    public void setTotals(int price, int calories) {
        this.totalsLabel.setText("Price: Php " + price + "    Calories: " + calories);
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
     * Replaces everything shown in the machine output area.
     *
     * @param text the text to display
     */
    public void setOutput(String text) {
        this.outputArea.setText(text);
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
     * Enables or disables the controls that change or buy the bowl, so they cannot be used while a preparation is being narrated.
     *
     * @param enabled true to allow interaction
     */
    public void setControlsEnabled(boolean enabled) {
        this.addButton.setEnabled(enabled);
        this.removeButton.setEnabled(enabled);
        this.clearButton.setEnabled(enabled);
        this.prepareButton.setEnabled(enabled);
        this.backButton.setEnabled(enabled);
    }

    /**
     * Registers the handler for adding the selected ingredient.
     *
     * @param listener the handler to attach
     */
    public void addAddListener(ActionListener listener) {
        this.addButton.addActionListener(listener);
    }

    /**
     * Registers the handler for dropping an ingredient from the bowl.
     *
     * @param listener the handler to attach
     */
    public void addRemoveListener(ActionListener listener) {
        this.removeButton.addActionListener(listener);
    }

    /**
     * Registers the handler for emptying the bowl.
     *
     * @param listener the handler to attach
     */
    public void addClearListener(ActionListener listener) {
        this.clearButton.addActionListener(listener);
    }

    /**
     * Registers the handler for preparing and buying the bowl.
     *
     * @param listener the handler to attach
     */
    public void addPrepareListener(ActionListener listener) {
        this.prepareButton.addActionListener(listener);
    }

    /**
     * Registers the handler for returning to the vending screen.
     *
     * @param listener the handler to attach
     */
    public void addBackListener(ActionListener listener) {
        this.backButton.addActionListener(listener);
    }
}
