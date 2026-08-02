package model;

import java.util.ArrayList;

/**
 * The behaviour shared by every vending machine the factory can produce.
 *
 * <p>A machine owns its slots, the money it can pay out as change, and the
 * record of what it has sold since the last stocking. Nothing here reads from
 * or writes to the console, and nothing here builds a sentence for the user:
 * a purchase attempt returns a {@link PurchaseStatus} and the controller
 * decides how to word it. That is what keeps the model reusable behind either
 * a text interface or the Swing interface this phase uses.</p>
 */
public abstract class VendingMachine {

    /** Maximum number of item slots any machine may have. */
    public static final int MAX_SLOTS = 20;

    /** Maximum number of units a single slot may hold. */
    public static final int SLOT_CAPACITY = 10;

    /** Minimum number of slots the specifications require. */
    public static final int MIN_SLOTS = 8;

    /** The face values every machine accepts, smallest first. */
    public static final int[] DENOMINATIONS = {1, 5, 10, 20, 50, 100, 200, 500, 1000};

    /** The slots of this machine, each mapped to one unique item type. */
    protected ArrayList<ItemSlot> slots;

    /** The money the machine owns and can pay out as change. */
    protected Money[] money;

    /** The outcome of the most recent purchase attempt. */
    protected PurchaseStatus lastStatus;

    /** The name of whatever the most recent successful purchase dispensed. */
    protected String lastItemName;

    /** The pieces paid back as change by the most recent successful purchase. */
    protected int[] lastChange;

    /** The calorie count of whatever the most recent purchase dispensed. */
    protected int lastCalories;

    /** The price charged by the most recent purchase attempt. */
    protected int lastPrice;

    /** The cash inserted for the most recent purchase attempt. */
    protected int lastPaid;

    /**
     * Creates an empty machine with no stock and no money.
     */
    public VendingMachine() {
        this.slots = new ArrayList<ItemSlot>();
        this.money = new Money[DENOMINATIONS.length];

        for (int i = 0; i < DENOMINATIONS.length; i++) {
            this.money[i] = new Money(DENOMINATIONS[i]);
        }

        // Set directly rather than calling the overridable clearLastTransaction(),
        // so no subclass method can run before the subclass is fully built.
        this.lastStatus = null;
        this.lastItemName = "";
        this.lastCalories = 0;
        this.lastPrice = 0;
        this.lastPaid = 0;
        this.lastChange = new int[DENOMINATIONS.length];
    }

    /**
     * Stocks the machine with the default items for its type.
     *
     * <p>post: the machine holds its default slots and every slot's summary
     * baseline has been recorded.</p>
     */
    public abstract void loadDefaultItems();

    /**
     * Returns the display name of this kind of machine.
     *
     * @return the machine name
     */
    public abstract String getMachineName();

    // ------------------------------------------------------------------
    // stock
    // ------------------------------------------------------------------

    /**
     * Adds a brand-new slot to the machine.
     *
     * @param code                the slot code, unique within this machine
     * @param name                the item name (pre: not null or blank)
     * @param calories            the calorie count of one unit (pre: &gt;= 0)
     * @param price               the price of one unit (pre: price &gt;= 0)
     * @param canSellIndividually whether the item may be bought on its own
     * @param ingredientType      the role the item plays inside a ramen
     * @param initialQuantity     the starting stock (pre: 0 to SLOT_CAPACITY)
     * @return true if the slot was created and stocked
     */
    public boolean addSlot(String code, String name, int calories, int price,
                           boolean canSellIndividually, IngredientType ingredientType,
                           int initialQuantity) {
        if (code == null || code.trim().isEmpty()) {
            return false;
        }
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (this.slots.size() >= MAX_SLOTS) {
            return false;
        }
        if (findSlotByCode(code) != null || findSlotByName(name) != null) {
            return false;
        }
        if (calories < 0 || price < 0
                || initialQuantity < 0 || initialQuantity > SLOT_CAPACITY) {
            return false;
        }

        ItemSlot slot = new ItemSlot(code.trim(), name.trim(), calories, price,
                canSellIndividually, ingredientType, SLOT_CAPACITY);

        if (initialQuantity > 0) {
            slot.stock(initialQuantity);
        }
        slot.resetSummary();
        this.slots.add(slot);
        return true;
    }

    /**
     * Adds stock to an existing slot and restarts the summary period.
     *
     * @param slotNumber the one-based position of the slot
     * @param quantity   the number of units to add (pre: quantity &gt; 0)
     * @return true if the slot exists and had room for the units
     */
    public boolean restock(int slotNumber, int quantity) {
        ItemSlot slot = getSlot(slotNumber);
        if (slot == null) {
            return false;
        }
        if (!slot.stock(quantity)) {
            return false;
        }
        resetTransactionSummary();
        return true;
    }

    /**
     * Sets a new selling price for an existing item.
     *
     * @param slotNumber the one-based position of the slot
     * @param newPrice   the new price in pesos (pre: newPrice &gt;= 0)
     * @return true if the slot exists and the price was accepted
     */
    public boolean setItemPrice(int slotNumber, int newPrice) {
        ItemSlot slot = getSlot(slotNumber);
        if (slot == null || newPrice < 0) {
            return false;
        }
        slot.setPrice(newPrice);
        return true;
    }

    /**
     * Removes a slot and its remaining stock from the machine.
     *
     * @param slotNumber the one-based position of the slot
     * @return true if a matching slot was found and removed
     */
    public boolean removeSlot(int slotNumber) {
        ItemSlot slot = getSlot(slotNumber);
        if (slot == null) {
            return false;
        }
        this.slots.remove(slot);
        return true;
    }

    /**
     * Finds a slot by the code printed on the machine.
     *
     * @param slotCode the code to look for, ignoring capitalisation
     * @return the matching slot, or null if there is none
     */
    public ItemSlot findSlotByCode(String slotCode) {
        if (slotCode == null) {
            return null;
        }
        for (int i = 0; i < this.slots.size(); i++) {
            if (this.slots.get(i).getSlotCode().equalsIgnoreCase(slotCode.trim())) {
                return this.slots.get(i);
            }
        }
        return null;
    }

    /**
     * Finds a slot by the name of the item it holds.
     *
     * @param itemName the item name to look for, ignoring capitalisation
     * @return the matching slot, or null if there is none
     */
    public ItemSlot findSlotByName(String itemName) {
        if (itemName == null) {
            return null;
        }
        for (int i = 0; i < this.slots.size(); i++) {
            if (this.slots.get(i).getItemName().equalsIgnoreCase(itemName.trim())) {
                return this.slots.get(i);
            }
        }
        return null;
    }

    /**
     * Returns the slot at a one-based position, as the customer sees it.
     *
     * @param slotNumber the position, counting from one
     * @return the slot, or null if the position is out of range
     */
    public ItemSlot getSlot(int slotNumber) {
        if (slotNumber < 1 || slotNumber > this.slots.size()) {
            return null;
        }
        return this.slots.get(slotNumber - 1);
    }

    /**
     * Returns the slots of this machine.
     *
     * @return a copy of the slot list, so callers cannot add or remove slots
     */
    public ArrayList<ItemSlot> getSlots() {
        return new ArrayList<ItemSlot>(this.slots);
    }

    /**
     * Reports whether the machine meets the minimum slot count required by the
     * specifications.
     *
     * @return true if the machine has at least {@link #MIN_SLOTS} slots
     */
    public boolean meetsMinimumSlots() {
        return this.slots.size() >= MIN_SLOTS;
    }

    // ------------------------------------------------------------------
    // vending
    // ------------------------------------------------------------------

    /**
     * Attempts to buy one individually-sellable item.
     *
     * <p>The change is planned against the machine's own money together with
     * the cash the customer just inserted, and nothing is altered until a plan
     * is found. A failed attempt therefore leaves the machine exactly as it
     * was, and a note inserted a moment ago can be broken up to pay the change
     * on the very same sale.</p>
     *
     * @param slotNumber   the one-based slot the customer selected
     * @param insertedCash the pieces inserted, indexed like {@link #DENOMINATIONS}
     * @return true if the item was dispensed; call {@link #getLastStatus()}
     *         for the reason when it was not
     */
    public boolean purchaseItem(int slotNumber, int[] insertedCash) {
        clearLastTransaction();

        ItemSlot slot = getSlot(slotNumber);
        if (slot == null) {
            this.lastStatus = PurchaseStatus.INVALID_SLOT;
            return false;
        }
        if (!slot.canSellIndividually()) {
            this.lastStatus = PurchaseStatus.NOT_SOLD_INDIVIDUALLY;
            this.lastItemName = slot.getItemName();
            return false;
        }
        if (slot.isEmpty()) {
            this.lastStatus = PurchaseStatus.OUT_OF_STOCK;
            this.lastItemName = slot.getItemName();
            return false;
        }

        int paid = getCashTotal(insertedCash);
        int price = slot.getPrice();
        this.lastPrice = price;
        this.lastPaid = paid;
        this.lastItemName = slot.getItemName();

        if (paid < price) {
            this.lastStatus = PurchaseStatus.INSUFFICIENT_PAYMENT;
            return false;
        }

        int[] changePlan = makeChangePlan(paid - price, insertedCash);
        if (changePlan == null) {
            this.lastStatus = PurchaseStatus.NO_EXACT_CHANGE;
            return false;
        }

        Item dispensed = slot.dispense();
        addInsertedCash(insertedCash);
        removeChange(changePlan);

        this.lastChange = changePlan;
        this.lastCalories = dispensed.getCalories();
        this.lastStatus = PurchaseStatus.SUCCESS;
        return true;
    }

    // ------------------------------------------------------------------
    // money
    // ------------------------------------------------------------------

    /**
     * Adds a single piece of a denomination to the change fund.
     *
     * <p>This overload exists for the common case of dropping in one coin.</p>
     *
     * @param denomination the face value to add
     * @return true if the denomination is one the machine accepts
     */
    public boolean replenishMoney(int denomination) {
        return replenishMoney(denomination, 1);
    }

    /**
     * Adds several pieces of a denomination to the change fund.
     *
     * @param denomination the face value to add
     * @param quantity     the number of pieces (pre: quantity &gt; 0)
     * @return true if the denomination is accepted and the quantity is positive
     */
    public boolean replenishMoney(int denomination, int quantity) {
        int index = findDenominationIndex(denomination);
        if (index == -1 || quantity <= 0) {
            return false;
        }
        this.money[index].addQty(quantity);
        return true;
    }

    /**
     * Lets the operator withdraw money from the change fund.
     *
     * @param denomination the face value to withdraw
     * @param quantity     the number of pieces (pre: quantity &gt; 0)
     * @return true if the machine held that many pieces
     */
    public boolean collectMoney(int denomination, int quantity) {
        int index = findDenominationIndex(denomination);
        if (index == -1 || quantity <= 0) {
            return false;
        }
        return this.money[index].removeQty(quantity);
    }

    /**
     * Returns the machine's money trays.
     *
     * @return a copy of the array, so callers cannot swap trays in or out
     */
    public Money[] getMoney() {
        Money[] copy = new Money[this.money.length];
        for (int i = 0; i < this.money.length; i++) {
            copy[i] = this.money[i];
        }
        return copy;
    }

    /**
     * Returns the total value of the change fund.
     *
     * @return the fund total in pesos
     */
    public int getMoneyTotal() {
        int total = 0;
        for (int i = 0; i < this.money.length; i++) {
            total += this.money[i].getTotalValue();
        }
        return total;
    }

    /**
     * Returns the peso value of a set of inserted pieces.
     *
     * @param cash the pieces, indexed like {@link #DENOMINATIONS}
     * @return the total value in pesos
     */
    public int getCashTotal(int[] cash) {
        int total = 0;
        for (int i = 0; i < DENOMINATIONS.length; i++) {
            total += cash[i] * DENOMINATIONS[i];
        }
        return total;
    }

    /**
     * Finds the array index of a face value.
     *
     * @param denomination the face value to look for
     * @return the index, or -1 if the machine does not accept that value
     */
    public int findDenominationIndex(int denomination) {
        for (int i = 0; i < DENOMINATIONS.length; i++) {
            if (DENOMINATIONS[i] == denomination) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Works out which pieces would make up an exact amount of change.
     *
     * <p>The search runs from the largest denomination downwards and backtracks
     * whenever a choice leaves a remainder that cannot be completed, so it finds
     * a plan whenever one exists rather than only when a greedy choice happens
     * to work. Owing 75 with a 50, a 20 and a 5 available succeeds; owing 76
     * correctly reports that it cannot be made.</p>
     *
     * @param amount       the amount of change owed (pre: amount &gt;= 0)
     * @param insertedCash the pieces the customer just inserted, which the
     *                     machine may also use to pay the change
     * @return the pieces to pay out, or null if exact change is impossible
     */
    protected int[] makeChangePlan(int amount, int[] insertedCash) {
        if (amount < 0) {
            return null;
        }

        int[] available = new int[DENOMINATIONS.length];
        for (int i = 0; i < DENOMINATIONS.length; i++) {
            available[i] = this.money[i].getQty() + insertedCash[i];
        }

        int[] plan = new int[DENOMINATIONS.length];
        if (findChange(amount, DENOMINATIONS.length - 1, available, plan)) {
            return plan;
        }
        return null;
    }

    /**
     * Recursive backtracking helper for {@link #makeChangePlan(int, int[])}.
     *
     * @param amount    the amount still to be covered
     * @param index     the denomination currently being considered
     * @param available the pieces still free to use, per denomination
     * @param plan      the plan being filled in, per denomination
     * @return true if the remaining amount can be made exactly
     */
    private boolean findChange(int amount, int index, int[] available, int[] plan) {
        if (amount == 0) {
            return true;
        }
        if (index < 0) {
            return false;
        }

        int value = DENOMINATIONS[index];
        int maximumUse = Math.min(amount / value, available[index]);

        for (int quantity = maximumUse; quantity >= 0; quantity--) {
            plan[index] = quantity;
            if (findChange(amount - (quantity * value), index - 1, available, plan)) {
                return true;
            }
        }

        plan[index] = 0;
        return false;
    }

    /**
     * Moves the customer's payment into the machine.
     *
     * @param insertedCash the pieces inserted, indexed like {@link #DENOMINATIONS}
     */
    protected void addInsertedCash(int[] insertedCash) {
        for (int i = 0; i < insertedCash.length; i++) {
            this.money[i].addQty(insertedCash[i]);
        }
    }

    /**
     * Takes the change out of the machine.
     *
     * @param changePlan the pieces being paid back to the customer
     */
    protected void removeChange(int[] changePlan) {
        for (int i = 0; i < changePlan.length; i++) {
            if (changePlan[i] > 0) {
                this.money[i].removeQty(changePlan[i]);
            }
        }
    }

    // ------------------------------------------------------------------
    // summary
    // ------------------------------------------------------------------

    /**
     * Starts a fresh summary period for every slot.
     *
     * <p>post: every slot's starting stock equals its current stock and every
     * usage counter is zero.</p>
     */
    public void resetTransactionSummary() {
        for (int i = 0; i < this.slots.size(); i++) {
            this.slots.get(i).resetSummary();
        }
    }

    /**
     * Returns the money collected from items sold on their own.
     *
     * @return the individual-sale revenue in pesos
     */
    public int getTotalSales() {
        int total = 0;
        for (int i = 0; i < this.slots.size(); i++) {
            total += this.slots.get(i).getSalesRevenue();
        }
        return total;
    }

    // ------------------------------------------------------------------
    // last transaction
    // ------------------------------------------------------------------

    /**
     * Resets everything recorded about the previous purchase attempt.
     */
    protected void clearLastTransaction() {
        this.lastStatus = null;
        this.lastItemName = "";
        this.lastCalories = 0;
        this.lastPrice = 0;
        this.lastPaid = 0;
        this.lastChange = new int[DENOMINATIONS.length];
    }

    /**
     * Returns the outcome of the most recent purchase attempt.
     *
     * @return the status, or null if no attempt has been made yet
     */
    public PurchaseStatus getLastStatus() {
        return this.lastStatus;
    }

    /**
     * Returns the name of whatever the most recent attempt concerned.
     *
     * @return the item or ramen name
     */
    public String getLastItemName() {
        return this.lastItemName;
    }

    /**
     * Returns the change produced by the most recent successful purchase.
     *
     * @return a copy of the change plan, indexed like {@link #DENOMINATIONS}
     */
    public int[] getLastChange() {
        int[] copy = new int[this.lastChange.length];
        for (int i = 0; i < this.lastChange.length; i++) {
            copy[i] = this.lastChange[i];
        }
        return copy;
    }

    /**
     * Returns the calorie count of whatever was most recently dispensed.
     *
     * @return the calorie count
     */
    public int getLastCalories() {
        return this.lastCalories;
    }

    /**
     * Returns the price charged by the most recent attempt.
     *
     * @return the price in pesos
     */
    public int getLastPrice() {
        return this.lastPrice;
    }

    /**
     * Returns the cash inserted for the most recent attempt.
     *
     * @return the amount paid in pesos
     */
    public int getLastPaid() {
        return this.lastPaid;
    }
}
