package model;

import java.util.ArrayList;


public class SpecialVendingMachine extends RegularVendingMachine {
    private String lastPreparation;

    public SpecialVendingMachine() {
        super();
        lastPreparation = "";
    }

    @Override
    public String getMachineName() {
        return "Special Ramen Vending Machine";
    }


    @Override
    public void loadDefaultItems() {
        slots.clear();

        addSlot("S01", "Noodles", 200, 25, true, "NOODLE", 10);
        addSlot("S02", "Chashu Pork", 300, 45, true, "TOPPING", 8);
        addSlot("S03", "Aji Tamago", 90, 15, true, "TOPPING", 10);
        addSlot("S04", "Fried Tofu", 120, 20, true, "TOPPING", 9);
        addSlot("S05", "Fish Cake", 80, 15, true, "TOPPING", 8);
        addSlot("S06", "Negi", 10, 5, false, "TOPPING", 10);
        addSlot("S07", "Tonkotsu Broth", 150, 35, false, "BROTH", 10);
        addSlot("S08", "Miso Broth", 120, 30, false, "BROTH", 10);
        addSlot("S09", "Black Garlic Oil", 40, 10, false, "ADD_ON", 10);
        addSlot("S10", "Gyoza", 260, 50, true, "NONE", 6);

        resetTransactionSummary();
    }

    public String getLastPreparation() {
        return lastPreparation;
    }

    /**
     * Creates one custom ramen.
     * slotNumbers and quantities must have matching indexes.
     * Example: {1,2,7} and {1,2,1} means
     * 1 Noodles, 2 Chashu, 1 Tonkotsu Broth.
     */
    public boolean prepareRamen(int[] slotNumbers, int[] quantities,
                                int[] insertedCash) {
        clearLastTransaction();
        lastPreparation = "";

        if (slotNumbers == null || quantities == null
                || slotNumbers.length != quantities.length
                || slotNumbers.length == 0) {
            lastMessage = "Invalid ramen order.";
            return false;
        }

        boolean hasNoodles = false;
        boolean hasBroth = false;
        int totalPrice = 0;
        int totalCalories = 0;

        // validate the whole order before removing anything.
        for (int i = 0; i < slotNumbers.length; i++) {
            ItemSlot slot = getSlot(slotNumbers[i]);
            int quantity = quantities[i];

            if (slot == null || quantity <= 0) {
                lastMessage = "Invalid ingredient selection.";
                return false;
            }
            if (slot.getIngredientType().equals("NONE")) {
                lastMessage = slot.getItemName()
                        + " is not a ramen ingredient.";
                return false;
            }
            if (slot.getQuantity() < quantity) {
                lastMessage = "Not enough stock for " + slot.getItemName() + ".";
                return false;
            }

            if (slot.getIngredientType().equals("NOODLE")) {
                hasNoodles = true;
            }
            if (slot.getIngredientType().equals("BROTH")) {
                hasBroth = true;
            }

            totalPrice += slot.getPrice() * quantity;
            totalCalories += slot.getCalories() * quantity;
        }

        if (!hasNoodles || !hasBroth) {
            lastMessage = "A ramen must have noodles and broth.";
            return false;
        }

        int insertedTotal = getCashTotal(insertedCash);
        if (insertedTotal < totalPrice) {
            lastMessage = "Not enough money inserted.";
            return false;
        }

        int changeAmount = insertedTotal - totalPrice;
        int[] changePlan = makeChangePlan(changeAmount);
        if (changePlan == null) {
            lastMessage = "The machine cannot produce exact change.";
            return false;
        }

        ArrayList<Item> usedIngredients = new ArrayList<Item>();

        // Second: consume the actual Item objects.
        for (int i = 0; i < slotNumbers.length; i++) {
            ItemSlot slot = getSlot(slotNumbers[i]);

            for (int j = 0; j < quantities[i]; j++) {
                Item item = slot.dispense();
                usedIngredients.add(item);
            }
        }

        addInsertedCash(insertedCash);
        removeChange(changePlan);

        lastPrice = totalPrice;
        lastCalories = totalCalories;
        lastChange = changePlan;
        lastMessage = "Custom ramen completed.";
        lastPreparation = buildPreparationText(slotNumbers, quantities);
        return true;
    }

    private String buildPreparationText(int[] slotNumbers, int[] quantities) {
        String text = "Blanching noodles...\n";
        text += "Heating broth...\n";
        text += "Placing noodles in cup...\n";

        for (int i = 0; i < slotNumbers.length; i++) {
            ItemSlot slot = getSlot(slotNumbers[i]);
            String type = slot.getIngredientType();

            if (type.equals("TOPPING") || type.equals("ADD_ON")) {
                text += "Adding " + quantities[i] + " "
                        + slot.getItemName() + "...\n";
            }
        }

        text += "Pouring broth...\n";
        text += "Ramen Done!";
        return text;
    }
}
