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
    private String ingredientType;
    private boolean canSellIndividually;

    /**
     * Creates a new item with the given attributes. The starting stock used
     * by the transaction summary is initialized to the given quantity and the
     * sold counter is set to zero.
     *
     * @param calories the calorie count of one unit
     * @param price    the selling price of one unit (pre: price &gt;= 0)
     * @param name      the unique display name of the item
     */
    public Item(String name, int calories, int price,
                boolean canSellIndividually, String ingredientType) {
        this.name = name;
        this.calories = calories;
        this.price = price;
        this.canSellIndividually = canSellIndividually;
        this.ingredientType = ingredientType;
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
     * Sets the selling price of one unit of this item.
     * @param price the new price in pesos (pre: price &gt;= 0)
     */
    public void setPrice(int price) {
        this.price = price;
    }


    public boolean canSellIndividually() {
        return canSellIndividually;
    }

    public String getIngredientType() {
        return ingredientType;
    }

}
