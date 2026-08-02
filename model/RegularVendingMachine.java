package model;

public class RegularVendingMachine extends VendingMachine {

    @Override
    public String getMachineName() {
        return "Regular Vending Machine";
    }

    @Override
    public void loadDefaultItems() {
        slots.clear();

        // Ramen-related items so the design can grow into MCO2.
        addSlot("S01", "Noodles", 200, 25, true, "NOODLE", 10);
        addSlot("S02", "Chashu Pork", 300, 45, true, "TOPPING", 8);
        addSlot("S03", "Aji Tamago", 90, 15, true, "TOPPING", 10);
        addSlot("S04", "Fried Tofu", 120, 20, true, "TOPPING", 9);
        addSlot("S05", "Fish Cake", 80, 15, true, "TOPPING", 8);
        addSlot("S06", "Gyoza", 260, 50, true, "NONE", 6);
        addSlot("S07", "Onigiri", 180, 35, true, "NONE", 7);
        addSlot("S08", "Green Tea", 5, 25, true, "NONE", 9);

        resetTransactionSummary();
    }
}

