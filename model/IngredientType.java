package model;

/** The role an item plays inside a customizable ramen. */
public enum IngredientType {

    /** The starch a bowl is built on. */
    NOODLE("noodles"),

    /** The liquid a bowl is finished with. */
    BROTH("broth"),

    /** A solid placed on top of the noodles. */
    TOPPING("topping"),

    /** A finishing aromatic used in small amounts, such as garlic oil. */
    ADD_ON("add-on"),

    /** Not part of any ramen; sold on its own only. */
    NONE("not a ramen ingredient");

    private final String label;

    /**
     * Creates a role with a readable label.
     *
     * @param label the human-readable name of the role
     */
    IngredientType(String label) {
        this.label = label;
    }

    /**
     * Returns the readable name of this role.
     *
     * @return the display label
     */
    public String getLabel() {
        return this.label;
    }

    /**
     * Reports whether items of this role may be built into a ramen.
     *
     * @return true for every role except {@link #NONE}
     */
    public boolean isRamenIngredient() {
        return this != NONE;
    }
}
