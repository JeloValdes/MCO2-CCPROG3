package model;

/** One physical unit of stock held inside a slot. */
public class Item {

    private final String name;
    private final int calories;

    /**
     * Creates one physical unit.
     *
     * @param name     the name of the item type (pre: not null)
     * @param calories the calorie count of this unit (pre: calories &gt;= 0)
     */
    public Item(String name, int calories) {
        this.name = name;
        this.calories = calories;
    }

    /**
     * Returns the name of the item type this unit belongs to.
     *
     * @return the item name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the calorie count of this unit.
     *
     * @return the calorie count
     */
    public int getCalories() {
        return this.calories;
    }

    /**
     * Returns a short description of this unit.
     *
     * @return the name followed by the calorie count
     */
    @Override
    public String toString() {
        return this.name + " (" + this.calories + " cal)";
    }
}
