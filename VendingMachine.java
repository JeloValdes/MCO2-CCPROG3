
import java.util.ArrayList;

/**
 * Parent class for all vending machines.
 * Contains the features shared by Regular and Special vending machines.
 */
public abstract class VendingMachine {
    public static final int MAX_SLOTS = 20;
    public static final int SLOT_CAPACITY = 10;
    public static final int[] DENOMINATIONS =
            {1, 5, 10, 20, 50, 100, 200, 500, 1000};

    protected ArrayList<ItemSlot> slots;
    protected Money[] money;

    // Information about the most recent transaction.
    // Controllers/GUI can read these after a purchase attempt.
    protected String lastMessage;
    protected int[] lastChange;
    protected int lastCalories;
    protected int lastPrice;

    public VendingMachine() {
        slots = new ArrayList<ItemSlot>();
        money = new Money[DENOMINATIONS.length];

        for (int i = 0; i < DENOMINATIONS.length; i++) {
            money[i] = new Money(DENOMINATIONS[i]);
        }

        lastMessage = "";
        lastChange = new int[DENOMINATIONS.length];
    }

    /** Child classes provide their own default stock. */
    public abstract void loadDefaultItems();

    /** Child classes provide their own name. This demonstrates overriding. */
    public abstract String getMachineName();

    public ArrayList<ItemSlot> getSlots() {
        return new ArrayList<ItemSlot>(slots);
    }

    public Money[] getMoney() {
        Money[] copy = new Money[money.length];
        for (int i = 0; i < money.length; i++) {
            copy[i] = money[i];
        }
        return copy;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public int[] getLastChange() {
        int[] copy = new int[lastChange.length];
        for (int i = 0; i < lastChange.length; i++) {
            copy[i] = lastChange[i];
        }
        return copy;
    }

    public int getLastCalories() {
        return lastCalories;
    }

    public int getLastPrice() {
        return lastPrice;
    }

    protected void clearLastTransaction() {
        lastMessage = "";
        lastCalories = 0;
        lastPrice = 0;
        lastChange = new int[DENOMINATIONS.length];
    }

    /** Adds a brand-new slot to the machine. */
    public boolean addSlot(String code, String name, int calories, int price,
                           boolean canSellIndividually, String ingredientType,
                           int initialQuantity) {
        if (slots.size() >= MAX_SLOTS) {
            return false;
        }
        if (findSlotByCode(code) != null) {
            return false;
        }
        if (initialQuantity < 0 || initialQuantity > SLOT_CAPACITY) {
            return false;
        }

        ItemSlot slot = new ItemSlot(code, name, calories, price,
                canSellIndividually, ingredientType, SLOT_CAPACITY);

        if (initialQuantity > 0) {
            slot.stock(initialQuantity);
        }
        slot.resetSummary();
        slots.add(slot);
        return true;
    }

    public ItemSlot findSlotByCode(String slotCode) {
        for (int i = 0; i < slots.size(); i++) {
            if (slots.get(i).getSlotCode().equalsIgnoreCase(slotCode)) {
                return slots.get(i);
            }
        }
        return null;
    }

    public ItemSlot getSlot(int slotNumber) {
        if (slotNumber < 1 || slotNumber > slots.size()) {
            return null;
        }
        return slots.get(slotNumber - 1);
    }

    /**
     * Attempts to buy one individually-sellable item.
     * insertedCash uses the same indexes as DENOMINATIONS.
     */
    public boolean purchaseItem(int slotNumber, int[] insertedCash) {
        clearLastTransaction();

        ItemSlot slot = getSlot(slotNumber);
        if (slot == null) {
            lastMessage = "Invalid slot.";
            return false;
        }
        if (!slot.canSellIndividually()) {
            lastMessage = slot.getItemName()
                    + " cannot be bought by itself.";
            return false;
        }
        if (slot.getQuantity() <= 0) {
            lastMessage = "Item is out of stock.";
            return false;
        }

        int insertedTotal = getCashTotal(insertedCash);
        int price = slot.getPrice();

        if (insertedTotal < price) {
            lastMessage = "Not enough money inserted.";
            return false;
        }

        int changeAmount = insertedTotal - price;
        int[] changePlan = makeChangePlan(changeAmount);

        if (changePlan == null) {
            lastMessage = "The machine cannot produce exact change.";
            return false;
        }

        // Transaction is safe to complete now.
        Item dispensed = slot.dispense();
        addInsertedCash(insertedCash);
        removeChange(changePlan);

        lastChange = changePlan;
        lastCalories = dispensed.getCalories();
        lastPrice = price;
        lastMessage = "Dispensing " + dispensed.getName() + ".";
        return true;
    }

    public boolean restock(int slotNumber, int quantity) {
        ItemSlot slot = getSlot(slotNumber);
        if (slot == null) {
            return false;
        }

        boolean success = slot.stock(quantity);
        if (success) {
            resetTransactionSummary();
        }
        return success;
    }

    public boolean setItemPrice(int slotNumber, int newPrice) {
        ItemSlot slot = getSlot(slotNumber);
        if (slot == null || newPrice < 0) {
            return false;
        }

        slot.setPrice(newPrice);
        return true;
    }

    /** Method overloading: replenish one piece. */
    public boolean replenishMoney(int denomination) {
        return replenishMoney(denomination, 1);
    }

    /** Method overloading: replenish several pieces. */
    public boolean replenishMoney(int denomination, int quantity) {
        int index = findDenominationIndex(denomination);
        if (index == -1 || quantity <= 0) {
            return false;
        }

        money[index].addQuantity(quantity);
        return true;
    }

    public boolean collectMoney(int denomination, int quantity) {
        int index = findDenominationIndex(denomination);
        if (index == -1 || quantity <= 0) {
            return false;
        }

        return money[index].removeQuantity(quantity);
    }

    public void resetTransactionSummary() {
        for (int i = 0; i < slots.size(); i++) {
            slots.get(i).resetSummary();
        }
    }

    public String getTransactionSummary() {
        String summary = "TRANSACTION SUMMARY\n";
        int totalSales = 0;

        for (int i = 0; i < slots.size(); i++) {
            ItemSlot slot = slots.get(i);
            summary += slot.getItemName()
                    + " | Start: " + slot.getStartingStock()
                    + " | End: " + slot.getQuantity()
                    + " | Sold/Used: " + slot.getQuantitySold()
                    + " | Revenue: Php " + slot.getSalesRevenue()
                    + "\n";

            totalSales += slot.getSalesRevenue();
        }

        summary += "Total Sales: Php " + totalSales;
        return summary;
    }

    protected int getCashTotal(int[] cash) {
        int total = 0;
        for (int i = 0; i < DENOMINATIONS.length; i++) {
            total += cash[i] * DENOMINATIONS[i];
        }
        return total;
    }

    protected int findDenominationIndex(int denomination) {
        for (int i = 0; i < DENOMINATIONS.length; i++) {
            if (DENOMINATIONS[i] == denomination) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Finds exact change using the machine's current money.
     * This helper uses recursion only because limited denominations can make
     * a simple greedy solution fail in some cases.
     */
    protected int[] makeChangePlan(int amount) {
        int[] plan = new int[DENOMINATIONS.length];

        if (findChange(amount, DENOMINATIONS.length - 1, plan)) {
            return plan;
        }
        return null;
    }

    private boolean findChange(int amount, int index, int[] plan) {
        if (amount == 0) {
            return true;
        }
        if (index < 0) {
            return false;
        }

        int value = DENOMINATIONS[index];
        int maximumNeeded = amount / value;
        int maximumAvailable = money[index].getQuantity();
        int maximumUse = Math.min(maximumNeeded, maximumAvailable);

        for (int quantity = maximumUse; quantity >= 0; quantity--) {
            plan[index] = quantity;
            int remaining = amount - (quantity * value);

            if (findChange(remaining, index - 1, plan)) {
                return true;
            }
        }

        plan[index] = 0;
        return false;
    }

    protected void addInsertedCash(int[] insertedCash) {
        for (int i = 0; i < insertedCash.length; i++) {
            money[i].addQuantity(insertedCash[i]);
        }
    }

    protected void removeChange(int[] changePlan) {
        for (int i = 0; i < changePlan.length; i++) {
            if (changePlan[i] > 0) {
                money[i].removeQuantity(changePlan[i]);
            }
        }
    }
}
