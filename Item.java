/**
 * Represents a single item type stored in a slot of the vending machine.
 *
 * <p>Each {@code Item} keeps track of its current stock quantity, calorie
 * count, selling price, and unique name. In addition, it maintains two
 * bookkeeping values used to build the machine's transaction summary:
 * the starting stock recorded at the last (re)stocking and the number of
 * units sold since that stocking.</p>
 */
public class Item {
    private int quantity;
    private int calories;
    private int price;
    private String name;
    private int startingStock; // stock level captured at the last (re)stocking
    private int qtySold;       // units sold since the last (re)stocking

    /**
     * Creates a new item with the given attributes. The starting stock used
     * by the transaction summary is initialized to the given quantity and the
     * sold counter is set to zero.
     *
     * @param quantity the initial number of units in the slot (pre: quantity &gt;= 0)
     * @param calories the calorie count of one unit
     * @param price    the selling price of one unit (pre: price &gt;= 0)
     * @param name      the unique display name of the item
     */
    public Item(int quantity, int calories, int price, String name) {
        this.quantity = quantity;
        this.calories = calories;
        this.price = price;
        this.name = name;
        this.startingStock = quantity;
        this.qtySold = 0;
    }

    /**
     * Returns the current stock quantity of this item.
     * @return the number of units currently in the slot
     */
    public int getQuantity() {
        return this.quantity;
    }

    /**
     * Returns the calorie count of one unit of this item.
     * @return the calorie count
     */
    public int getCalories() {
        return this.calories;
    }

    /**
     * Returns the selling price of one unit of this item.
     * @return the price in pesos
     */
    public int getPrice() {
        return this.price;
    }

    /**
     * Returns the unique display name of this item.
     * @return the item name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the stock level recorded at the last (re)stocking, used as the
     * starting inventory in the transaction summary.
     * @return the starting stock since the last (re)stocking
     */
    public int getStartingStock() {
        return this.startingStock;
    }

    /**
     * Returns the number of units sold since the last (re)stocking.
     * @return the quantity sold in the current summary period
     */
    public int getQtySold() {
        return this.qtySold;
    }

    /**
     * Sets the current stock quantity of this item.
     * @param quantity the new stock quantity (pre: quantity &gt;= 0)
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Sets the calorie count of one unit of this item.
     * @param calories the new calorie count
     */
    public void setCalories(int calories) {
        this.calories = calories;
    }

    /**
     * Sets the selling price of one unit of this item.
     * @param price the new price in pesos (pre: price &gt;= 0)
     */
    public void setPrice(int price) {
        this.price = price;
    }

    /**
     * Reduces the stock quantity by the given amount.
     * @param quantity the number of units to remove (pre: quantity &lt;= current quantity)
     */
    public void deductQuantity(int quantity) {
        this.quantity -= quantity;
    }

    /**
     * Increases the stock quantity by the given amount.
     * @param quantity the number of units to add (pre: quantity &gt;= 0)
     */
    public void addQuantity(int quantity) {
        this.quantity += quantity;
    }

    /**
     * Records the sale of one unit of this item: decrements the stock by one
     * and increments the units-sold counter used by the transaction summary.
     * <p>pre: quantity &gt; 0; post: quantity is reduced by 1 and qtySold is
     * increased by 1.</p>
     */
    public void recordSale() {
        this.quantity -= 1;
        this.qtySold += 1;
    }

    /**
     * Resets the transaction-summary baseline for this item. This is called
     * whenever the item is (re)stocked so that the summary reflects only the
     * period since the most recent stocking.
     * <p>post: startingStock equals the current quantity and qtySold is 0.</p>
     */
    public void resetSummaryBaseline() {
        this.startingStock = this.quantity;
        this.qtySold = 0;
    }
}
