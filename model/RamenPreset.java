package model;

import java.util.ArrayList;

/** A named, ready-made ramen recipe the customer can load in one step. */
public class RamenPreset {

    private final String name;
    private final String description;
    private final ArrayList<String> ingredientNames;
    private final ArrayList<Integer> quantities;

    /**
     * Creates an empty preset.
     *
     * @param name        the display name of the recipe
     * @param description a one-line summary shown beside the name
     */
    public RamenPreset(String name, String description) {
        this.name = name;
        this.description = description;
        this.ingredientNames = new ArrayList<String>();
        this.quantities = new ArrayList<Integer>();
    }

    /**
     * Adds one component to this recipe.
     *
     * @param itemName the item the machine should draw from
     * @param quantity the number of orders (pre: quantity &gt; 0)
     */
    public void addIngredient(String itemName, int quantity) {
        if (itemName != null && quantity > 0) {
            this.ingredientNames.add(itemName);
            this.quantities.add(quantity);
        }
    }

    /**
     * Returns the item names making up this recipe.
     *
     * @return a copy of the name list, in recipe order
     */
    public ArrayList<String> getIngredientNames() {
        return new ArrayList<String>(this.ingredientNames);
    }

    /**
     * Returns the orders of each component.
     *
     * @return a copy of the quantity list, matching the name list by index
     */
    public ArrayList<Integer> getQuantities() {
        return new ArrayList<Integer>(this.quantities);
    }

    /**
     * Returns how many distinct components this recipe uses.
     *
     * @return the component count
     */
    public int size() {
        return this.ingredientNames.size();
    }

    /**
     * Returns the display name of this recipe.
     *
     * @return the recipe name
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the one-line summary of this recipe.
     *
     * @return the description
     */
    public String getDescription() {
        return this.description;
    }
}
