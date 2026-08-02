package controller;

import java.util.ArrayList;
import model.Alert;
import model.AlertType;
import model.IngredientType;
import model.ItemSlot;
import model.Money;
import model.SpecialVendingMachine;
import model.VendingMachine;
import view.MainFrame;
import view.MaintenancePanel;
import view.UI;

/** Controller for the operator-facing features. */
public class MaintenanceController {

    private final MaintenancePanel panel;

    private VendingMachine machine;
    private ArrayList<ItemSlot> displayedSlots;

    /**
     * Wires the maintenance screen to its handlers.
     *
     * @param frame the application window
     */
    public MaintenanceController(MainFrame frame) {
        this.panel = frame.getMaintenancePanel();
        this.displayedSlots = new ArrayList<ItemSlot>();

        this.panel.addAddListener(event -> handleAddItem());
        this.panel.addRestockListener(event -> handleRestock());
        this.panel.addPriceListener(event -> handleSetPrice());
        this.panel.addRemoveListener(event -> handleRemove());
        this.panel.addDefaultsListener(event -> handleLoadDefaults());
        this.panel.addReplenishListener(event -> handleReplenish());
        this.panel.addCollectListener(event -> handleCollect());
        this.panel.addResetListener(event -> handleReset());
        this.panel.addBackListener(event -> frame.showCard(MainFrame.TEST));
    }

    /**
     * Points this controller at the machine under test and redraws every tab.
     *
     * @param machine the machine to maintain
     */
    public void setMachine(VendingMachine machine) {
        this.machine = machine;
        this.panel.setMachineLabel("Maintenance Features - " + machine.getMachineName());
        refresh();
    }

    /**
     * Adds stock to a slot.
     *
     * @param slotNumber the one-based slot to restock
     * @param quantity   the number of units to add
     * @return true if the slot exists and had room
     */
    public boolean restock(int slotNumber, int quantity) {
        return this.machine.restock(slotNumber, quantity);
    }

    /**
     * Sets a new price for a slot.
     *
     * @param slotNumber the one-based slot to reprice
     * @param newPrice   the new price in pesos
     * @return true if the slot exists and the price was accepted
     */
    public boolean setPrice(int slotNumber, int newPrice) {
        return this.machine.setItemPrice(slotNumber, newPrice);
    }

    /**
     * Adds pieces of a denomination to the change fund.
     *
     * @param denomination the face value to add
     * @param quantity     the number of pieces
     * @return true if the denomination is accepted
     */
    public boolean replenishMoney(int denomination, int quantity) {
        return this.machine.replenishMoney(denomination, quantity);
    }

    /**
     * Withdraws pieces of a denomination from the change fund.
     *
     * @param denomination the face value to withdraw
     * @param quantity     the number of pieces
     * @return true if the machine held that many pieces
     */
    public boolean collectMoney(int denomination, int quantity) {
        return this.machine.collectMoney(denomination, quantity);
    }

    /** Redraws the stock, money, summary, and alert tabs from the model. */
    public void refresh() {
        refreshStock();
        refreshMoney();
        refreshSummary();
        refreshAlerts();
    }

    /** Rebuilds the stock table. */
    private void refreshStock() {
        this.displayedSlots = this.machine.getSlots();
        Object[][] rows = new Object[this.displayedSlots.size()][7];

        for (int i = 0; i < this.displayedSlots.size(); i++) {
            ItemSlot slot = this.displayedSlots.get(i);
            rows[i][0] = slot.getSlotCode();
            rows[i][1] = slot.getItemName();
            rows[i][2] = "Php " + slot.getPrice();
            rows[i][3] = slot.getCalories();
            rows[i][4] = slot.getQuantity() + " / " + slot.getCapacity();
            rows[i][5] = slot.getIngredientType().getLabel();
            rows[i][6] = slot.canSellIndividually() ? "Yes" : "No";
        }
        this.panel.setStockRows(rows);
    }

    /** Rebuilds the money table and the change fund total. */
    private void refreshMoney() {
        Money[] trays = this.machine.getMoney();
        Object[][] rows = new Object[trays.length][3];

        for (int i = 0; i < trays.length; i++) {
            rows[i][0] = UI.denominationLabel(trays[i].getValue());
            rows[i][1] = trays[i].getQty();
            rows[i][2] = "Php " + trays[i].getTotalValue();
        }
        this.panel.setMoneyRows(rows);
        this.panel.setFundTotal(this.machine.getMoneyTotal());
    }

    /** Rebuilds the summary table and the collected-sales line. */
    private void refreshSummary() {
        ArrayList<ItemSlot> slots = this.machine.getSlots();
        Object[][] rows = new Object[slots.size()][7];

        for (int i = 0; i < slots.size(); i++) {
            ItemSlot slot = slots.get(i);
            rows[i][0] = slot.getSlotCode();
            rows[i][1] = slot.getItemName();
            rows[i][2] = slot.getStartingStock();
            rows[i][3] = slot.getQuantity();
            rows[i][4] = slot.getQuantitySold();
            rows[i][5] = slot.getQuantityUsedAsIngredient();
            rows[i][6] = "Php " + slot.getSalesRevenue();
        }
        this.panel.setSummaryRows(rows);

        String text = "Total collected: Php " + this.machine.getTotalSales();
        if (this.machine instanceof SpecialVendingMachine) {
            SpecialVendingMachine special = (SpecialVendingMachine) this.machine;
            text += "   (individual items Php "
                    + (special.getTotalSales() - special.getRamenRevenue())
                    + ", " + special.getRamenCount() + " ramen Php "
                    + special.getRamenRevenue() + ")";
        }
        this.panel.setSalesText(text);
    }

    /** Rebuilds the operator alert table. */
    private void refreshAlerts() {
        ArrayList<Alert> alerts = this.machine.getAlerts();
        Object[][] rows = new Object[alerts.size()][3];
        int critical = 0;

        for (int i = 0; i < alerts.size(); i++) {
            Alert alert = alerts.get(i);
            rows[i][0] = alert.isCritical() ? "CRITICAL" : "Warning";
            rows[i][1] = subjectOf(alert);
            rows[i][2] = adviceFor(alert);

            if (alert.isCritical()) {
                critical++;
            }
        }
        this.panel.setAlertRows(rows);

        if (alerts.isEmpty()) {
            this.panel.setAlertSummary("No issues. The machine is ready to trade.");
        } else {
            this.panel.setAlertSummary(critical + " critical, "
                    + (alerts.size() - critical) + " warning(s).");
        }
    }

    /**
     * Names what an alert concerns, in terms the operator will recognise.
     *
     * @param alert the alert to describe
     * @return the subject as it should appear in the table
     */
    private String subjectOf(Alert alert) {
        AlertType type = alert.getType();

        if (type == AlertType.DENOMINATION_EMPTY || type == AlertType.DENOMINATION_LOW) {
            return UI.denominationLabel(Integer.parseInt(alert.getSubject()));
        }
        return alert.getSubject();
    }

    /**
     * Turns an alert into the action the operator should take.
     *
     * @param alert the alert to describe
     * @return the advice as it should appear in the table
     */
    private String adviceFor(Alert alert) {
        AlertType type = alert.getType();

        if (type == AlertType.SLOT_EMPTY) {
            return "Sold out. Restock on the Stock tab.";
        }
        if (type == AlertType.SLOT_LOW) {
            return "Only " + alert.getValue() + " left. Restock soon.";
        }
        if (type == AlertType.DENOMINATION_EMPTY) {
            return "None left. Replenish, or sales needing change may be refused.";
        }
        if (type == AlertType.DENOMINATION_LOW) {
            return "Only " + alert.getValue() + " piece(s) left. Replenish on the Money tab.";
        }
        if (type == AlertType.CHANGE_FUND_LOW) {
            return "Fund is Php " + alert.getValue() + "; Php " + alert.getThreshold()
                    + " or more is recommended before trading.";
        }
        return "Only " + alert.getValue() + " slot(s); the specifications require at least "
                + alert.getThreshold() + ".";
    }

    /** Adds a brand new item type from the values typed into the form. */
    private void handleAddItem() {
        String code = this.panel.getCodeInput();
        String name = this.panel.getNameInput();
        int price = parse(this.panel.getPriceInput(), -1);
        int quantity = parse(this.panel.getQuantityInput(), -1);
        int calories = parse(this.panel.getCaloriesInput(), -1);

        if (code.isEmpty() || name.isEmpty() || price < 0 || quantity < 0 || calories < 0) {
            this.panel.showMessage("Fill in a slot code and item name, plus whole numbers "
                    + "for price, quantity, and calories.");
            return;
        }

        IngredientType type = IngredientType.values()[this.panel.getSelectedIngredientType()];
        boolean sellAlone = this.panel.isSellAloneSelected();

        if (this.machine.addSlot(code, name, calories, price, sellAlone, type, quantity)) {
            this.panel.showMessage("Added " + name + " to slot " + code.toUpperCase()
                    + " (" + quantity + " pieces at Php " + price + ").");
            this.panel.clearForm();
            refresh();
        } else if (this.machine.findSlotByCode(code) != null) {
            this.panel.showMessage("Slot code " + code.toUpperCase() + " is already in use.");
        } else if (this.machine.findSlotByName(name) != null) {
            this.panel.showMessage(name + " is already stocked. Use restock instead.");
        } else if (this.machine.getSlots().size() >= VendingMachine.MAX_SLOTS) {
            this.panel.showMessage("The machine is full. It cannot hold more than "
                    + VendingMachine.MAX_SLOTS + " item types.");
        } else {
            this.panel.showMessage("Quantity must be between 0 and "
                    + VendingMachine.SLOT_CAPACITY + ".");
        }
    }

    /** Adds stock to the selected item, using the quantity field. */
    private void handleRestock() {
        int row = selectedRow();
        if (row < 0) {
            return;
        }

        ItemSlot slot = this.displayedSlots.get(row);
        int quantity = parse(this.panel.getQuantityInput(), -1);

        if (quantity <= 0) {
            this.panel.showMessage("Type the number of pieces to add into the Quantity field first.");
            return;
        }

        if (restock(row + 1, quantity)) {
            this.panel.showMessage("Restocked " + slot.getItemName() + " with " + quantity
                    + " piece(s). The summary period starts again.");
            this.panel.clearForm();
            refresh();
        } else {
            this.panel.showMessage("A slot cannot hold more than " + slot.getCapacity()
                    + " pieces. " + slot.getItemName() + " currently holds "
                    + slot.getQuantity() + ".");
        }
    }

    /** Sets a new price for the selected item, using the price field. */
    private void handleSetPrice() {
        int row = selectedRow();
        if (row < 0) {
            return;
        }

        ItemSlot slot = this.displayedSlots.get(row);
        int price = parse(this.panel.getPriceInput(), -1);

        if (price < 0) {
            this.panel.showMessage("Type the new price into the Price field first.");
            return;
        }

        setPrice(row + 1, price);
        this.panel.showMessage(slot.getItemName() + " is now priced at Php " + price + ".");
        this.panel.clearForm();
        refresh();
    }

    /** Removes the selected item type from the machine. */
    private void handleRemove() {
        int row = selectedRow();
        if (row < 0) {
            return;
        }

        ItemSlot slot = this.displayedSlots.get(row);
        this.machine.removeSlot(row + 1);
        this.panel.showMessage(slot.getItemName() + " has been removed from the machine.");
        refresh();
    }

    /** Replaces the machine's stock with the sample items for its type. */
    private void handleLoadDefaults() {
        this.machine.loadDefaultItems();
        this.panel.showMessage("Loaded the sample items for this machine ("
                + this.machine.getSlots().size() + " item types).");
        refresh();
    }

    /** Adds pieces of the chosen denomination to the change fund. */
    private void handleReplenish() {
        int value = VendingMachine.DENOMINATIONS[this.panel.getSelectedDenomination()];
        int quantity = this.panel.getMoneyQuantity();

        replenishMoney(value, quantity);
        this.panel.showMessage("Added " + quantity + " x " + UI.denominationLabel(value) + ".");
        refresh();
    }

    /** Withdraws pieces of the chosen denomination from the change fund. */
    private void handleCollect() {
        int index = this.panel.getSelectedDenomination();
        int value = VendingMachine.DENOMINATIONS[index];
        int quantity = this.panel.getMoneyQuantity();

        if (collectMoney(value, quantity)) {
            this.panel.showMessage("Collected " + quantity + " x " + UI.denominationLabel(value) + ".");
            refresh();
        } else {
            this.panel.showMessage("The machine only holds "
                    + this.machine.getMoney()[index].getQty() + " x "
                    + UI.denominationLabel(value) + ".");
        }
    }

    /** Starts a fresh summary period for every item. */
    private void handleReset() {
        this.machine.resetTransactionSummary();
        this.panel.showMessage("A new summary period has started. "
                + "Starting inventory now matches current stock.");
        refresh();
    }

    /**
     * Returns the selected stock row, complaining if there is none.
     *
     * @return the selected row index, or -1 if the operator selected nothing
     */
    private int selectedRow() {
        int row = this.panel.getSelectedStockRow();
        if (row < 0 || row >= this.displayedSlots.size()) {
            this.panel.showMessage("Select an item in the table first.");
            return -1;
        }
        return row;
    }

    /**
     * Reads a whole number out of a text field.
     *
     * @param text     the text typed by the operator
     * @param fallback the value to use when the text is not a whole number
     * @return the parsed value, or the fallback
     */
    private int parse(String text, int fallback) {
        try {
            return Integer.parseInt(text);
        } catch (NumberFormatException error) {
            return fallback;
        }
    }
}
