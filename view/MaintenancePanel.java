package view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;

/** The maintenance features screen, grouped into three tabs. */
public class MaintenancePanel extends JPanel {

    /** Version marker required of serializable Swing components. */
    private static final long serialVersionUID = 1L;

    private final JLabel machineLabel;

    private final JTable stockTable;
    private final JTextField codeField;
    private final JTextField nameField;
    private final JTextField priceField;
    private final JTextField quantityField;
    private final JTextField caloriesField;
    private final JComboBox<String> typeBox;
    private final JCheckBox sellAloneBox;
    private final JButton addButton;
    private final JButton restockButton;
    private final JButton priceButton;
    private final JButton removeButton;
    private final JButton defaultsButton;

    private final JTable moneyTable;
    private final JComboBox<String> denominationBox;
    private final JSpinner moneySpinner;
    private final JButton replenishButton;
    private final JButton collectButton;
    private final JLabel fundLabel;

    private final JTable summaryTable;
    private final JTable alertTable;
    private final JLabel alertSummaryLabel;
    private final JLabel salesLabel;
    private final JButton resetButton;

    private final JTextArea outputArea;
    private final JButton backButton;

    /**
     * Builds the maintenance screen.
     *
     * @param denominationNames the labels for each denomination, smallest first
     * @param ingredientNames   the selectable ingredient roles
     */
    public MaintenancePanel(String[] denominationNames, String[] ingredientNames) {
        setLayout(new BorderLayout(10, 10));
        setBackground(UI.BACKGROUND);
        setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        this.machineLabel = new JLabel("Maintenance Features");
        this.machineLabel.setFont(UI.TITLE_FONT);
        this.machineLabel.setForeground(UI.ACCENT);
        add(this.machineLabel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        // ----- stock tab -----
        JPanel stockTab = UI.panel(8);
        this.stockTable = UI.table(
                new String[]{"Slot", "Item", "Price", "Calories", "Stock", "Role", "Sold alone?"});
        JScrollPane stockScroll = new JScrollPane(this.stockTable);
        stockScroll.setPreferredSize(new Dimension(660, 210));
        stockTab.add(stockScroll, BorderLayout.CENTER);

        JPanel form = new JPanel(new GridLayout(2, 7, 6, 6));
        form.setBackground(UI.BACKGROUND);
        this.codeField = new JTextField();
        this.nameField = new JTextField();
        this.priceField = new JTextField();
        this.quantityField = new JTextField();
        this.caloriesField = new JTextField();
        this.typeBox = new JComboBox<>(ingredientNames);
        this.sellAloneBox = new JCheckBox("", true);
        this.sellAloneBox.setBackground(UI.BACKGROUND);
        form.add(new JLabel("Slot code"));
        form.add(new JLabel("Item name"));
        form.add(new JLabel("Price"));
        form.add(new JLabel("Quantity"));
        form.add(new JLabel("Calories"));
        form.add(new JLabel("Role"));
        form.add(new JLabel("Sold alone?"));
        form.add(this.codeField);
        form.add(this.nameField);
        form.add(this.priceField);
        form.add(this.quantityField);
        form.add(this.caloriesField);
        form.add(this.typeBox);
        form.add(this.sellAloneBox);

        JPanel stockButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        stockButtons.setBackground(UI.BACKGROUND);
        this.addButton = UI.button("Add new item");
        this.restockButton = UI.button("Restock selected");
        this.priceButton = UI.button("Set price of selected");
        this.removeButton = UI.button("Remove selected");
        this.defaultsButton = UI.button("Load sample items");
        stockButtons.add(this.addButton);
        stockButtons.add(this.restockButton);
        stockButtons.add(this.priceButton);
        stockButtons.add(this.removeButton);
        stockButtons.add(this.defaultsButton);

        JPanel stockSouth = new JPanel(new BorderLayout(4, 4));
        stockSouth.setBackground(UI.BACKGROUND);
        stockSouth.add(form, BorderLayout.CENTER);
        stockSouth.add(stockButtons, BorderLayout.SOUTH);
        stockTab.add(stockSouth, BorderLayout.SOUTH);
        tabs.addTab("Stock", stockTab);

        // ----- money tab -----
        JPanel moneyTab = UI.panel(8);
        this.moneyTable = UI.table(new String[]{"Denomination", "Pieces held", "Value"});
        JScrollPane moneyScroll = new JScrollPane(this.moneyTable);
        moneyScroll.setPreferredSize(new Dimension(660, 240));
        moneyTab.add(moneyScroll, BorderLayout.CENTER);

        JPanel moneyControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 6));
        moneyControls.setBackground(UI.BACKGROUND);
        this.denominationBox = new JComboBox<>(denominationNames);
        this.moneySpinner = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
        this.moneySpinner.setPreferredSize(new Dimension(70, 26));
        this.replenishButton = UI.button("Replenish");
        this.collectButton = UI.button("Collect");
        this.fundLabel = UI.heading("Change fund: Php 0");
        moneyControls.add(new JLabel("Denomination"));
        moneyControls.add(this.denominationBox);
        moneyControls.add(new JLabel("Pieces"));
        moneyControls.add(this.moneySpinner);
        moneyControls.add(this.replenishButton);
        moneyControls.add(this.collectButton);
        moneyControls.add(this.fundLabel);
        moneyTab.add(moneyControls, BorderLayout.SOUTH);
        tabs.addTab("Money", moneyTab);

        // ----- summary tab -----
        JPanel summaryTab = UI.panel(8);
        this.summaryTable = UI.table(
                new String[]{"Slot", "Item", "Starting", "Ending", "Sold alone",
                             "Used in ramen", "Item revenue"});
        JScrollPane summaryScroll = new JScrollPane(this.summaryTable);
        summaryScroll.setPreferredSize(new Dimension(660, 240));
        summaryTab.add(summaryScroll, BorderLayout.CENTER);

        JPanel summaryControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        summaryControls.setBackground(UI.BACKGROUND);
        this.salesLabel = UI.heading("Total collected: Php 0");
        this.resetButton = UI.button("Start a new summary period");
        summaryControls.add(this.salesLabel);
        summaryControls.add(this.resetButton);
        summaryTab.add(summaryControls, BorderLayout.SOUTH);
        tabs.addTab("Summary", summaryTab);

        // ----- alerts tab (bonus feature: operator dashboard) -----
        JPanel alertTab = UI.panel(8);
        this.alertTable = UI.table(new String[]{"Severity", "Concerns", "What to do"});
        JScrollPane alertScroll = new JScrollPane(this.alertTable);
        alertScroll.setPreferredSize(new Dimension(660, 240));
        alertTab.add(alertScroll, BorderLayout.CENTER);

        JPanel alertControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        alertControls.setBackground(UI.BACKGROUND);
        this.alertSummaryLabel = UI.heading("No issues.");
        alertControls.add(this.alertSummaryLabel);
        alertTab.add(alertControls, BorderLayout.SOUTH);
        tabs.addTab("Alerts", alertTab);

        add(tabs, BorderLayout.CENTER);

        JPanel south = new JPanel(new BorderLayout(6, 6));
        south.setBackground(UI.BACKGROUND);
        this.outputArea = new JTextArea();
        south.add(UI.output(this.outputArea, 5), BorderLayout.CENTER);
        this.backButton = UI.button("Back");
        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        backRow.setBackground(UI.BACKGROUND);
        backRow.add(this.backButton);
        south.add(backRow, BorderLayout.SOUTH);
        add(south, BorderLayout.SOUTH);
    }

    /**
     * Sets the heading describing the machine being maintained.
     *
     * @param text the heading text
     */
    public void setMachineLabel(String text) {
        this.machineLabel.setText(text);
    }

    /**
     * Replaces the stock table.
     *
     * @param rows the rows to display, in slot order
     */
    public void setStockRows(Object[][] rows) {
        UI.setRows(this.stockTable, rows);
    }

    /**
     * Replaces the money table.
     *
     * @param rows the rows to display, smallest denomination first
     */
    public void setMoneyRows(Object[][] rows) {
        UI.setRows(this.moneyTable, rows);
    }

    /**
     * Replaces the summary table.
     *
     * @param rows the rows to display, in slot order
     */
    public void setSummaryRows(Object[][] rows) {
        UI.setRows(this.summaryTable, rows);
    }

    /**
     * Replaces the operator alert table.
     *
     * @param rows the rows to display, critical problems first
     */
    public void setAlertRows(Object[][] rows) {
        UI.setRows(this.alertTable, rows);
    }

    /**
     * Updates the line summarising how many problems were found.
     *
     * @param text the fully formatted summary text
     */
    public void setAlertSummary(String text) {
        this.alertSummaryLabel.setText(text);
    }

    /**
     * Returns the stock row currently selected.
     *
     * @return the selected row index, or -1 if nothing is selected
     */
    public int getSelectedStockRow() {
        return this.stockTable.getSelectedRow();
    }

    /**
     * Returns the denomination chosen on the money tab.
     *
     * @return the denomination index
     */
    public int getSelectedDenomination() {
        return this.denominationBox.getSelectedIndex();
    }

    /**
     * Returns the number of pieces chosen on the money tab.
     *
     * @return the requested piece count
     */
    public int getMoneyQuantity() {
        return (Integer) this.moneySpinner.getValue();
    }

    /**
     * Returns the slot code typed into the form.
     *
     * @return the trimmed contents of the slot code field
     */
    public String getCodeInput() {
        return this.codeField.getText().trim();
    }

    /**
     * Returns the item name typed into the form.
     *
     * @return the trimmed contents of the name field
     */
    public String getNameInput() {
        return this.nameField.getText().trim();
    }

    /**
     * Returns the price typed into the form.
     *
     * @return the trimmed contents of the price field
     */
    public String getPriceInput() {
        return this.priceField.getText().trim();
    }

    /**
     * Returns the quantity typed into the form.
     *
     * @return the trimmed contents of the quantity field
     */
    public String getQuantityInput() {
        return this.quantityField.getText().trim();
    }

    /**
     * Returns the calorie count typed into the form.
     *
     * @return the trimmed contents of the calories field
     */
    public String getCaloriesInput() {
        return this.caloriesField.getText().trim();
    }

    /**
     * Returns the ingredient role chosen in the form.
     *
     * @return the selected role index
     */
    public int getSelectedIngredientType() {
        return this.typeBox.getSelectedIndex();
    }

    /**
     * Reports whether the new item may be sold on its own.
     *
     * @return true if the sold-alone checkbox is ticked
     */
    public boolean isSellAloneSelected() {
        return this.sellAloneBox.isSelected();
    }

    /** Clears every field of the new-item form. */
    public void clearForm() {
        this.codeField.setText("");
        this.nameField.setText("");
        this.priceField.setText("");
        this.quantityField.setText("");
        this.caloriesField.setText("");
    }

    /**
     * Updates the label showing the value of the change fund.
     *
     * @param total the fund total in pesos
     */
    public void setFundTotal(int total) {
        this.fundLabel.setText("Change fund: Php " + total);
    }

    /**
     * Updates the label showing the money collected from sales.
     *
     * @param text the fully formatted sales text
     */
    public void setSalesText(String text) {
        this.salesLabel.setText(text);
    }

    /**
     * Writes a line of feedback to the maintenance output area.
     *
     * @param line the line to display
     */
    public void showMessage(String line) {
        this.outputArea.append(line + System.lineSeparator());
        this.outputArea.setCaretPosition(this.outputArea.getDocument().getLength());
    }

    /**
     * Returns everything written to the maintenance output area.
     *
     * @return the accumulated feedback text
     */
    public String getMessageText() {
        return this.outputArea.getText();
    }

    /**
     * Returns how many rows the summary table is showing.
     *
     * @return the summary row count
     */
    public int getSummaryRowCount() {
        return this.summaryTable.getRowCount();
    }

    /**
     * Returns one cell of the summary table.
     *
     * @param row    the row index, counting from zero
     * @param column the column index, counting from zero
     * @return the value displayed in that cell
     */
    public Object getSummaryValue(int row, int column) {
        return this.summaryTable.getValueAt(row, column);
    }

    /**
     * Returns how many rows the alert table is showing.
     *
     * @return the alert row count
     */
    public int getAlertRowCount() {
        return this.alertTable.getRowCount();
    }

    /**
     * Returns one cell of the alert table.
     *
     * @param row    the row index, counting from zero
     * @param column the column index, counting from zero
     * @return the value displayed in that cell
     */
    public Object getAlertValue(int row, int column) {
        return this.alertTable.getValueAt(row, column);
    }

    /**
     * Registers the handler for adding a new item type.
     *
     * @param listener the handler to attach
     */
    public void addAddListener(ActionListener listener) {
        this.addButton.addActionListener(listener);
    }

    /**
     * Registers the handler for restocking the selected item.
     *
     * @param listener the handler to attach
     */
    public void addRestockListener(ActionListener listener) {
        this.restockButton.addActionListener(listener);
    }

    /**
     * Registers the handler for repricing the selected item.
     *
     * @param listener the handler to attach
     */
    public void addPriceListener(ActionListener listener) {
        this.priceButton.addActionListener(listener);
    }

    /**
     * Registers the handler for removing the selected item.
     *
     * @param listener the handler to attach
     */
    public void addRemoveListener(ActionListener listener) {
        this.removeButton.addActionListener(listener);
    }

    /**
     * Registers the handler for loading the sample items.
     *
     * @param listener the handler to attach
     */
    public void addDefaultsListener(ActionListener listener) {
        this.defaultsButton.addActionListener(listener);
    }

    /**
     * Registers the handler for replenishing money.
     *
     * @param listener the handler to attach
     */
    public void addReplenishListener(ActionListener listener) {
        this.replenishButton.addActionListener(listener);
    }

    /**
     * Registers the handler for collecting money.
     *
     * @param listener the handler to attach
     */
    public void addCollectListener(ActionListener listener) {
        this.collectButton.addActionListener(listener);
    }

    /**
     * Registers the handler for starting a new summary period.
     *
     * @param listener the handler to attach
     */
    public void addResetListener(ActionListener listener) {
        this.resetButton.addActionListener(listener);
    }

    /**
     * Registers the handler for leaving the maintenance features.
     *
     * @param listener the handler to attach
     */
    public void addBackListener(ActionListener listener) {
        this.backButton.addActionListener(listener);
    }
}
