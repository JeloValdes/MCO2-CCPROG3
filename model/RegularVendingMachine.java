package model;

/** A vending machine that dispenses stocked items exactly as they are. */
public class RegularVendingMachine extends VendingMachine {

    /** Creates an empty regular machine. */
    public RegularVendingMachine() {
        super();
    }

    /**
     * {@inheritDoc}
     *
     * @return the string "Regular Vending Machine"
     */
    @Override
    public String getMachineName() {
        return "Regular Vending Machine";
    }

    /** {@inheritDoc} */
    @Override
    public void loadDefaultItems() {
        this.slots.clear();

        addSlot("S01", "Noodles",       200, 25, true, IngredientType.NOODLE,  10);
        addSlot("S02", "Chashu Pork",   300, 45, true, IngredientType.TOPPING,  8);
        addSlot("S03", "Aji Tamago",     90, 15, true, IngredientType.TOPPING, 10);
        addSlot("S04", "Fried Tofu",    120, 20, true, IngredientType.TOPPING,  9);
        addSlot("S05", "Fish Cake",      80, 15, true, IngredientType.TOPPING,  8);
        addSlot("S06", "Gyoza",         260, 50, true, IngredientType.NONE,     6);
        addSlot("S07", "Onigiri",       180, 35, true, IngredientType.NONE,     7);
        addSlot("S08", "Green Tea",       5, 25, true, IngredientType.NONE,     9);
        addSlot("S09", "Bottled Water",   0, 20, true, IngredientType.NONE,    10);
        addSlot("S10", "Edamame",       120, 30, true, IngredientType.NONE,     8);

        resetTransactionSummary();
    }
}
