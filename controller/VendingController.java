package controller;

import java.util.ArrayList;
import javax.swing.Timer;
import model.ItemSlot;
import model.PurchaseStatus;
import model.SpecialVendingMachine;
import model.VendingMachine;
import view.MainFrame;
import view.RamenPanel;
import view.UI;
import view.VendingPanel;

/** Controller for the customer-facing features. */
public class VendingController {

    /** Delay in milliseconds between two lines of preparation narration. */
    private static final int STEP_DELAY = 700;

    private final MainFrame frame;
    private final VendingPanel vendingPanel;
    private final RamenPanel ramenPanel;

    private VendingMachine machine;
    private int[] insertedCash;

    private ArrayList<ItemSlot> displayedSlots;
    private ArrayList<ItemSlot> ingredientSlots;
    private ArrayList<Integer> bowlSlotNumbers;
    private ArrayList<Integer> bowlQuantities;

    /**
     * Wires the vending screens to their handlers.
     *
     * @param frame the application window
     */
    public VendingController(MainFrame frame) {
        this.frame = frame;
        this.vendingPanel = frame.getVendingPanel();
        this.ramenPanel = frame.getRamenPanel();
        this.insertedCash = new int[VendingMachine.DENOMINATIONS.length];
        this.displayedSlots = new ArrayList<ItemSlot>();
        this.ingredientSlots = new ArrayList<ItemSlot>();
        this.bowlSlotNumbers = new ArrayList<Integer>();
        this.bowlQuantities = new ArrayList<Integer>();

        for (int i = 0; i < this.vendingPanel.getDenominationCount(); i++) {
            final int index = i;
            this.vendingPanel.addDenominationListener(i, event -> insertMoneyAt(index));
        }
        this.vendingPanel.addRefundListener(event -> handleRefund());
        this.vendingPanel.addBuyListener(event -> handleBuyItem());
        this.vendingPanel.addRamenListener(event -> openRamenBuilder());
        this.vendingPanel.addBackListener(event -> frame.showCard(MainFrame.TEST));

        this.ramenPanel.addAddListener(event -> addIngredient());
        this.ramenPanel.addRemoveListener(event -> removeIngredient());
        this.ramenPanel.addClearListener(event -> clearBowl());
        this.ramenPanel.addPrepareListener(event -> handlePrepareRamen());
        this.ramenPanel.addBackListener(event -> frame.showCard(MainFrame.VENDING));
    }

    /**
     * Points this controller at the machine currently under test and redraws the vending screen.
     *
     * @param machine the machine to operate
     */
    public void setMachine(VendingMachine machine) {
        this.machine = machine;
        this.insertedCash = new int[VendingMachine.DENOMINATIONS.length];
        clearBowlData();

        this.vendingPanel.setMachineLabel("Vending Features - " + machine.getMachineName());
        this.vendingPanel.setRamenButtonVisible(machine instanceof SpecialVendingMachine);
        this.vendingPanel.setOutput("Select an item, insert cash, then press Buy."
                + System.lineSeparator());
        refreshVending();
    }

    /**
     * Returns the machine currently being operated.
     *
     * @return the machine, or null if none has been set
     */
    public VendingMachine getMachine() {
        return this.machine;
    }

    /**
     * Accepts one piece of a face value into the current transaction.
     *
     * @param denomination the face value inserted
     * @return true if the machine accepts that face value
     */
    public boolean insertMoney(int denomination) {
        int index = this.machine.findDenominationIndex(denomination);
        if (index == -1) {
            return false;
        }
        this.insertedCash[index]++;
        return true;
    }

    /**
     * Returns the value the customer has inserted so far.
     *
     * @return the inserted total in pesos
     */
    public int getInsertedTotal() {
        return this.machine.getCashTotal(this.insertedCash);
    }

    /**
     * Hands the customer back everything they inserted.
     * post: the inserted-cash tally is empty.
     *
     * @return the pieces returned, indexed like the machine's denominations
     */
    public int[] refundMoney() {
        int[] refund = new int[this.insertedCash.length];
        for (int i = 0; i < this.insertedCash.length; i++) {
            refund[i] = this.insertedCash[i];
            this.insertedCash[i] = 0;
        }
        return refund;
    }

    /**
     * Attempts to buy one item on its own.
     *
     * @param slotNumber the one-based slot the customer selected
     * @return true if the item was dispensed
     */
    public boolean buyItem(int slotNumber) {
        boolean success = this.machine.purchaseItem(slotNumber, this.insertedCash);
        if (success) {
            clearInsertedCash();
        }
        return success;
    }

    /**
     * Attempts to assemble and buy a custom ramen.
     *
     * @param slotNumbers the one-based slots chosen
     * @param quantities  the orders of each chosen slot
     * @return true if the ramen was dispensed, false if the machine is not a special machine or the order could not be completed
     */
    public boolean buyCustomRamen(int[] slotNumbers, int[] quantities) {
        if (!(this.machine instanceof SpecialVendingMachine)) {
            return false;
        }
        SpecialVendingMachine special = (SpecialVendingMachine) this.machine;

        boolean success = special.prepareRamen(slotNumbers, quantities, this.insertedCash);
        if (success) {
            clearInsertedCash();
        }
        return success;
    }

    // ------------------------------------------------------------------
    // screen handlers
    // ------------------------------------------------------------------

    /**
     * Handles a denomination button press.
     *
     * @param index the denomination index of the button pressed
     */
    private void insertMoneyAt(int index) {
        int value = VendingMachine.DENOMINATIONS[index];
        insertMoney(value);

        this.vendingPanel.appendOutput("Accepted one " + UI.denominationLabel(value)
                + ". Inserted total is now Php " + getInsertedTotal() + ".");
        this.vendingPanel.setInsertedTotal(getInsertedTotal());
        this.ramenPanel.setInsertedTotal(getInsertedTotal());
    }

    /** Handles the return-cash button. */
    private void handleRefund() {
        int before = getInsertedTotal();
        int[] refund = refundMoney();

        if (before == 0) {
            this.vendingPanel.appendOutput("There is no inserted cash to return.");
        } else {
            this.vendingPanel.appendOutput("Returning Php " + before + ":");
            this.vendingPanel.appendOutput(describePieces(refund));
        }
        this.vendingPanel.setInsertedTotal(getInsertedTotal());
        this.ramenPanel.setInsertedTotal(getInsertedTotal());
    }

    /** Handles the buy button for an individual item. */
    private void handleBuyItem() {
        int row = this.vendingPanel.getSelectedRow();
        if (row < 0 || row >= this.displayedSlots.size()) {
            this.vendingPanel.appendOutput("Select an item from the table first.");
            return;
        }

        if (buyItem(row + 1)) {
            this.vendingPanel.appendOutput("Dispensing " + this.machine.getLastItemName()
                    + " (" + this.machine.getLastCalories() + " calories) for Php "
                    + this.machine.getLastPrice() + "...");
            this.vendingPanel.appendOutput(describeChange(this.machine.getLastChange()));
            this.vendingPanel.appendOutput("Thank you. Please take your item.");
        } else {
            this.vendingPanel.appendOutput(explain(this.machine.getLastStatus()));
        }
        refreshVending();
    }

    /**
     * Turns a failure code into a sentence the customer can act on.
     *
     * @param status the code returned by the model
     * @return the message to display
     */
    private String explain(PurchaseStatus status) {
        String name = this.machine.getLastItemName();

        if (status == PurchaseStatus.INVALID_SLOT) {
            return "That slot does not exist on this machine.";
        }
        if (status == PurchaseStatus.NOT_SOLD_INDIVIDUALLY) {
            return name + " is only available as part of a custom ramen.";
        }
        if (status == PurchaseStatus.OUT_OF_STOCK) {
            return "Sorry, there is not enough " + name + " left.";
        }
        if (status == PurchaseStatus.INSUFFICIENT_PAYMENT) {
            return "Not enough cash inserted. " + name + " costs Php "
                    + this.machine.getLastPrice() + " and you have inserted Php "
                    + this.machine.getLastPaid() + ". Please insert Php "
                    + (this.machine.getLastPrice() - this.machine.getLastPaid()) + " more.";
        }
        if (status == PurchaseStatus.NO_EXACT_CHANGE) {
            return "The machine cannot produce Php "
                    + (this.machine.getLastPaid() - this.machine.getLastPrice())
                    + " in change. Please insert the exact amount or take your cash back.";
        }
        if (status == PurchaseStatus.MISSING_NOODLES_OR_BROTH) {
            return "A ramen needs at least one noodle and one broth. Add the missing one.";
        }
        if (status == PurchaseStatus.NOT_A_RAMEN_INGREDIENT) {
            return name + " cannot be put into a ramen.";
        }
        return "That order could not be completed. Please check your selection.";
    }

    /**
     * Formats the change produced by a sale.
     *
     * @param change the pieces paid back, indexed like the denominations
     * @return the message to display
     */
    private String describeChange(int[] change) {
        int total = this.machine.getCashTotal(change);
        if (total == 0) {
            return "No change is due.";
        }
        return "Dispensing Php " + total + " in change:"
                + System.lineSeparator() + describePieces(change);
    }

    /**
     * Lists a set of pieces, largest denomination first.
     *
     * @param pieces the pieces to describe, indexed like the denominations
     * @return an indented, one-line-per-denomination listing
     */
    private String describePieces(int[] pieces) {
        String text = "";
        for (int i = pieces.length - 1; i >= 0; i--) {
            if (pieces[i] > 0) {
                text += "    " + UI.denominationLabel(VendingMachine.DENOMINATIONS[i])
                        + " x " + pieces[i] + System.lineSeparator();
            }
        }
        return text.stripTrailing();
    }

    /** Rebuilds the item table and the inserted-cash total. */
    private void refreshVending() {
        this.displayedSlots = this.machine.getSlots();
        Object[][] rows = new Object[this.displayedSlots.size()][6];

        for (int i = 0; i < this.displayedSlots.size(); i++) {
            ItemSlot slot = this.displayedSlots.get(i);
            rows[i][0] = slot.getSlotCode();
            rows[i][1] = slot.getItemName();
            rows[i][2] = "Php " + slot.getPrice();
            rows[i][3] = slot.getCalories();
            rows[i][4] = slot.getQuantity() + " / " + slot.getCapacity();
            rows[i][5] = availabilityOf(slot);
        }

        this.vendingPanel.setItemRows(rows);
        this.vendingPanel.setInsertedTotal(getInsertedTotal());
    }

    /**
     * Describes whether an item can be bought right now, and if not, why.
     *
     * @param slot the slot to describe
     * @return a short availability phrase for the table
     */
    private String availabilityOf(ItemSlot slot) {
        if (!slot.canSellIndividually()) {
            return "Ramen ingredient only";
        }
        return slot.isEmpty() ? "Out of stock" : "Available";
    }

    // ------------------------------------------------------------------
    // custom ramen
    // ------------------------------------------------------------------

    /** Opens the ramen builder with an empty bowl. */
    private void openRamenBuilder() {
        clearBowlData();
        this.ramenPanel.setMachineLabel("Build a " + SpecialVendingMachine.PRODUCT_NAME);
        this.ramenPanel.setOutput("A ramen needs at least one noodle and one broth."
                + System.lineSeparator());
        this.ramenPanel.setControlsEnabled(true);
        refreshRamen();
        this.frame.showCard(MainFrame.RAMEN);
    }

    /** Rebuilds both ramen tables and the running totals. */
    private void refreshRamen() {
        SpecialVendingMachine special = (SpecialVendingMachine) this.machine;
        this.ingredientSlots = special.getIngredientSlots();

        Object[][] available = new Object[this.ingredientSlots.size()][6];
        for (int i = 0; i < this.ingredientSlots.size(); i++) {
            ItemSlot slot = this.ingredientSlots.get(i);
            available[i][0] = slot.getSlotCode();
            available[i][1] = slot.getItemName();
            available[i][2] = slot.getIngredientType().getLabel();
            available[i][3] = "Php " + slot.getPrice();
            available[i][4] = slot.getCalories();
            available[i][5] = slot.getQuantity();
        }
        this.ramenPanel.setIngredientRows(available);

        Object[][] bowl = new Object[this.bowlSlotNumbers.size()][3];
        for (int i = 0; i < this.bowlSlotNumbers.size(); i++) {
            ItemSlot slot = this.machine.getSlot(this.bowlSlotNumbers.get(i));
            int quantity = this.bowlQuantities.get(i);
            bowl[i][0] = slot.getItemName();
            bowl[i][1] = quantity;
            bowl[i][2] = "Php " + (slot.getPrice() * quantity);
        }
        this.ramenPanel.setBowlRows(bowl);

        int[] slotNumbers = toArray(this.bowlSlotNumbers);
        int[] quantities = toArray(this.bowlQuantities);
        this.ramenPanel.setTotals(special.computeRamenPrice(slotNumbers, quantities),
                special.computeRamenCalories(slotNumbers, quantities));
        this.ramenPanel.setInsertedTotal(getInsertedTotal());
    }

    /**
     * Adds the selected ingredient to the bowl, merging repeated choices of the same slot rather than listing it twice.
     */
    private void addIngredient() {
        int row = this.ramenPanel.getSelectedIngredientRow();
        if (row < 0 || row >= this.ingredientSlots.size()) {
            this.ramenPanel.appendOutput("Select an ingredient on the left first.");
            return;
        }

        ItemSlot slot = this.ingredientSlots.get(row);
        int quantity = this.ramenPanel.getRequestedQuantity();
        int slotNumber = slotNumberOf(slot);

        int alreadyChosen = 0;
        int existingIndex = this.bowlSlotNumbers.indexOf(slotNumber);
        if (existingIndex >= 0) {
            alreadyChosen = this.bowlQuantities.get(existingIndex);
        }

        if (slot.getQuantity() < alreadyChosen + quantity) {
            this.ramenPanel.appendOutput("Only " + slot.getQuantity() + " order(s) of "
                    + slot.getItemName() + " are left in the machine.");
            return;
        }

        if (existingIndex >= 0) {
            this.bowlQuantities.set(existingIndex, alreadyChosen + quantity);
        } else {
            this.bowlSlotNumbers.add(slotNumber);
            this.bowlQuantities.add(quantity);
        }

        this.ramenPanel.appendOutput("Added " + quantity + " order(s) of " + slot.getItemName() + ".");
        refreshRamen();
    }

    /** Drops the selected ingredient from the bowl. */
    private void removeIngredient() {
        int row = this.ramenPanel.getSelectedBowlRow();
        if (row < 0 || row >= this.bowlSlotNumbers.size()) {
            this.ramenPanel.appendOutput("Select an ingredient in your bowl first.");
            return;
        }

        ItemSlot slot = this.machine.getSlot(this.bowlSlotNumbers.get(row));
        this.bowlSlotNumbers.remove(row);
        this.bowlQuantities.remove(row);

        this.ramenPanel.appendOutput("Removed " + slot.getItemName() + " from your bowl.");
        refreshRamen();
    }

    /** Empties the bowl and starts again. */
    private void clearBowl() {
        clearBowlData();
        this.ramenPanel.appendOutput("Your bowl has been emptied.");
        refreshRamen();
    }

    /**
     * Buys the assembled bowl and, if the sale goes through, narrates the preparation one step at a time.
     */
    private void handlePrepareRamen() {
        if (this.bowlSlotNumbers.isEmpty()) {
            this.ramenPanel.appendOutput("Add at least one noodle and one broth before buying.");
            return;
        }

        SpecialVendingMachine special = (SpecialVendingMachine) this.machine;
        int[] slotNumbers = toArray(this.bowlSlotNumbers);
        int[] quantities = toArray(this.bowlQuantities);

        if (!buyCustomRamen(slotNumbers, quantities)) {
            this.ramenPanel.appendOutput(explain(this.machine.getLastStatus()));
            refreshRamen();
            return;
        }

        this.ramenPanel.setControlsEnabled(false);
        this.ramenPanel.appendOutput("Preparing your order...");
        narrate(special.getLastPreparation(), 0);
    }

    /**
     * Prints one preparation step, then schedules the next so the user can watch the machine work.
     *
     * @param steps the full list of preparation steps
     * @param index the step to print now
     */
    private void narrate(ArrayList<String> steps, int index) {
        if (index >= steps.size()) {
            this.ramenPanel.appendOutput("Dispensing " + this.machine.getLastItemName()
                    + " (" + this.machine.getLastCalories() + " calories) for Php "
                    + this.machine.getLastPrice() + "...");
            this.ramenPanel.appendOutput(describeChange(this.machine.getLastChange()));

            clearBowlData();
            this.ramenPanel.setControlsEnabled(true);
            refreshRamen();
            refreshVending();
            return;
        }

        this.ramenPanel.appendOutput("    " + steps.get(index));

        Timer timer = new Timer(STEP_DELAY, event -> narrate(steps, index + 1));
        timer.setRepeats(false);
        timer.start();
    }

    // ------------------------------------------------------------------
    // helpers
    // ------------------------------------------------------------------

    /**
     * Finds the one-based position of a slot within the machine.
     *
     * @param target the slot to locate
     * @return the position counting from one, or -1 if not found
     */
    private int slotNumberOf(ItemSlot target) {
        ArrayList<ItemSlot> all = this.machine.getSlots();
        for (int i = 0; i < all.size(); i++) {
            if (all.get(i) == target) {
                return i + 1;
            }
        }
        return -1;
    }

    /**
     * Converts a list of numbers into the plain array the model expects.
     *
     * @param list the list to convert
     * @return an array holding the same values in the same order
     */
    private int[] toArray(ArrayList<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }
        return array;
    }

    /** Empties the bowl being assembled. */
    private void clearBowlData() {
        this.bowlSlotNumbers = new ArrayList<Integer>();
        this.bowlQuantities = new ArrayList<Integer>();
    }

    /** Zeroes the inserted-cash tally after a completed sale. */
    private void clearInsertedCash() {
        for (int i = 0; i < this.insertedCash.length; i++) {
            this.insertedCash[i] = 0;
        }
    }
}
