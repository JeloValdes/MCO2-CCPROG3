package model;

import java.util.ArrayList;

/** One slot of a vending machine, mapped to exactly one item type. */
public class ItemSlot {

    private final String slotCode;
    private final String itemName;
    private final int calories;
    private final boolean canSellIndividually;
    private final IngredientType ingredientType;
    private final int capacity;

    private int price;
    private final ArrayList<Item> stock;

    private int startingStock;
    private int quantitySold;
    private int quantityUsedAsIngredient;
    private int salesRevenue;

    /**
     * Creates an empty slot bound to one item type.
     *
     * @param slotCode            the code shown on the machine, such as "S01"
     * @param itemName            the unique item name (pre: not null or blank)
     * @param calories            the calorie count of one unit (pre: &gt;= 0)
     * @param price               the selling price of one unit (pre: &gt;= 0)
     * @param canSellIndividually whether the item may be bought on its own
     * @param ingredientType      the role the item plays inside a ramen
     * @param capacity            the maximum units this slot may hold (pre: &gt; 0)
     */
    public ItemSlot(String slotCode, String itemName, int calories, int price,
                    boolean canSellIndividually, IngredientType ingredientType,
                    int capacity) {
        this.slotCode = slotCode;
        this.itemName = itemName;
        this.calories = calories;
        this.price = price;
        this.canSellIndividually = canSellIndividually;
        this.ingredientType = ingredientType;
        this.capacity = capacity;
        this.stock = new ArrayList<Item>();
        this.startingStock = 0;
        this.quantitySold = 0;
        this.quantityUsedAsIngredient = 0;
        this.salesRevenue = 0;
    }

    /**
     * Places one already-created unit into the slot.
     *
     * @param item the unit to store (pre: it belongs to this item type)
     * @return true if the unit fit, false if the slot is already full
     */
    public boolean stock(Item item) {
        if (this.stock.size() >= this.capacity) {
            return false;
        }
        this.stock.add(item);
        return true;
    }

    /**
     * Creates and stores the given number of units of this slot's item type.
     *
     * @param quantity the number of units to create (pre: quantity &gt; 0)
     * @return true if every unit fit, false if capacity would be exceeded
     */
    public boolean stock(int quantity) {
        if (quantity <= 0 || this.stock.size() + quantity > this.capacity) {
            return false;
        }
        for (int i = 0; i < quantity; i++) {
            this.stock.add(new Item(this.itemName, this.calories));
        }
        return true;
    }

    /**
     * Removes one unit from the slot as an individual sale.
     * post: the slot holds one unit fewer, the sold counter is one higher, and the price has been added to this slot's revenue.
     *
     * @return the unit removed, or null if the slot is empty
     */
    public Item dispense() {
        if (this.stock.isEmpty()) {
            return null;
        }
        Item item = this.stock.remove(this.stock.size() - 1);
        this.quantitySold++;
        this.salesRevenue += this.price;
        return item;
    }

    /**
     * Removes units from the slot for use inside a ramen.
     * post: the slot holds {@code quantity} units fewer and the ingredient counter is {@code quantity} higher.
     *
     * @param quantity the number of units required (pre: quantity &gt; 0)
     * @return the units removed, or null if the slot held too few
     */
    public ArrayList<Item> consume(int quantity) {
        if (quantity <= 0 || quantity > this.stock.size()) {
            return null;
        }
        ArrayList<Item> used = new ArrayList<Item>();
        for (int i = 0; i < quantity; i++) {
            used.add(this.stock.remove(this.stock.size() - 1));
        }
        this.quantityUsedAsIngredient += quantity;
        return used;
    }

    /**
     * Records the current stock as the new baseline for the summary.
     * post: the starting stock equals the current stock and both usage counters and the revenue are zero.
     */
    public void resetSummary() {
        this.startingStock = this.stock.size();
        this.quantitySold = 0;
        this.quantityUsedAsIngredient = 0;
        this.salesRevenue = 0;
    }

    /**
     * Sets a new selling price for one unit.
     *
     * @param newPrice the new price in pesos (pre: newPrice &gt;= 0)
     */
    public void setPrice(int newPrice) {
        this.price = newPrice;
    }

    /**
     * Returns the code shown on the machine for this slot.
     *
     * @return the slot code, such as "S01"
     */
    public String getSlotCode() {
        return this.slotCode;
    }

    /**
     * Returns the name of the item type held here.
     *
     * @return the item name
     */
    public String getItemName() {
        return this.itemName;
    }

    /**
     * Returns the calorie count of one unit.
     *
     * @return the calorie count per unit
     */
    public int getCalories() {
        return this.calories;
    }

    /**
     * Returns the selling price of one unit.
     *
     * @return the price in pesos
     */
    public int getPrice() {
        return this.price;
    }

    /**
     * Reports whether this item may be bought on its own.
     *
     * @return true if the item is sellable individually
     */
    public boolean canSellIndividually() {
        return this.canSellIndividually;
    }

    /**
     * Returns the role this item plays inside a ramen.
     *
     * @return the ingredient type
     */
    public IngredientType getIngredientType() {
        return this.ingredientType;
    }

    /**
     * Returns the number of units currently stored.
     *
     * @return the current stock level
     */
    public int getQuantity() {
        return this.stock.size();
    }

    /**
     * Returns the maximum number of units this slot may hold.
     *
     * @return the slot capacity
     */
    public int getCapacity() {
        return this.capacity;
    }

    /**
     * Reports whether the slot has run out of stock.
     *
     * @return true if no units remain
     */
    public boolean isEmpty() {
        return this.stock.isEmpty();
    }

    /**
     * Reports whether the slot has reached its capacity.
     *
     * @return true if no further units can be stored
     */
    public boolean isFull() {
        return this.stock.size() >= this.capacity;
    }

    /**
     * Returns the stock level recorded at the last stocking.
     *
     * @return the starting stock for the current summary period
     */
    public int getStartingStock() {
        return this.startingStock;
    }

    /**
     * Returns the units sold on their own since the last stocking.
     *
     * @return the individual sales count
     */
    public int getQuantitySold() {
        return this.quantitySold;
    }

    /**
     * Returns the units eaten by ramen orders since the last stocking.
     *
     * @return the ingredient usage count
     */
    public int getQuantityUsedAsIngredient() {
        return this.quantityUsedAsIngredient;
    }

    /**
     * Returns the revenue earned from individual sales of this item.
     *
     * @return the revenue in pesos
     */
    public int getSalesRevenue() {
        return this.salesRevenue;
    }

    /**
     * Returns the units currently held.
     *
     * @return a copy of the stock list, so callers cannot alter the slot
     */
    public ArrayList<Item> getStock() {
        return new ArrayList<Item>(this.stock);
    }
}
