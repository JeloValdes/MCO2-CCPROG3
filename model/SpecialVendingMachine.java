package model;

import java.util.ArrayList;

/** A vending machine that can also assemble a customizable ramen from the items it holds. */
public class SpecialVendingMachine extends VendingMachine {

    /** The name given to every bowl this machine assembles. */
    public static final String PRODUCT_NAME = "Custom Ramen";

    /**
     * Extra calories the noodles absorb from any broth in the bowl, as a fraction of the noodles' own calorie contribution.
     */
    private static final double BROTH_ABSORPTION_RATE = 0.10;

    /**
     * Extra calories contributed by a finishing oil coating every component, as a fraction of the running total.
     */
    private static final double ADD_ON_COATING_RATE = 0.05;

    private ArrayList<String> lastPreparation;
    private int ramenRevenue;
    private int ramenCount;

    /** Creates an empty special machine. */
    public SpecialVendingMachine() {
        super();
        this.lastPreparation = new ArrayList<String>();
        this.ramenRevenue = 0;
        this.ramenCount = 0;
    }

    /**
     * {@inheritDoc}
     *
     * @return the string "Special Ramen Vending Machine"
     */
    @Override
    public String getMachineName() {
        return "Special Ramen Vending Machine";
    }

    /** {@inheritDoc} */
    @Override
    public void loadDefaultItems() {
        this.slots.clear();

        addSlot("S01", "Noodles",          200, 25, true,  IngredientType.NOODLE,  10);
        addSlot("S02", "Chashu Pork",      300, 45, true,  IngredientType.TOPPING,  8);
        addSlot("S03", "Aji Tamago",        90, 15, true,  IngredientType.TOPPING, 10);
        addSlot("S04", "Fried Tofu",       120, 20, true,  IngredientType.TOPPING,  9);
        addSlot("S05", "Fish Cake",         80, 15, true,  IngredientType.TOPPING,  8);
        addSlot("S06", "Negi",              10,  5, true,  IngredientType.TOPPING, 10);
        addSlot("S07", "Tonkotsu Broth",   150, 35, false, IngredientType.BROTH,   10);
        addSlot("S08", "Miso Broth",       120, 30, false, IngredientType.BROTH,   10);
        addSlot("S09", "Shio Broth",       110, 25, false, IngredientType.BROTH,   10);
        addSlot("S10", "Black Garlic Oil",  40, 10, false, IngredientType.ADD_ON,  10);
        addSlot("S11", "Gyoza",            260, 50, true,  IngredientType.NONE,     6);
        addSlot("S12", "Onigiri",          180, 35, true,  IngredientType.NONE,     7);
        addSlot("S13", "Green Tea",          5, 25, true,  IngredientType.NONE,     9);

        resetTransactionSummary();
    }

    /**
     * Assembles and sells one custom ramen.
     *
     * @param slotNumbers  the one-based slots chosen, matching {@code quantities}
     * @param quantities   the orders of each chosen slot (pre: each &gt; 0)
     * @param insertedCash the pieces inserted, indexed like {@link #DENOMINATIONS}
     * @return true if the ramen was assembled and dispensed; call {@link #getLastStatus()} for the reason when it was not
     */
    public boolean prepareRamen(int[] slotNumbers, int[] quantities, int[] insertedCash) {
        clearLastTransaction();
        this.lastPreparation = new ArrayList<String>();
        this.lastItemName = PRODUCT_NAME;

        if (slotNumbers == null || quantities == null
                || slotNumbers.length != quantities.length
                || slotNumbers.length == 0) {
            this.lastStatus = PurchaseStatus.INVALID_ORDER;
            return false;
        }

        boolean hasNoodles = false;
        boolean hasBroth = false;
        int totalPrice = 0;

        // First pass: check the whole order before consuming anything.
        for (int i = 0; i < slotNumbers.length; i++) {
            ItemSlot slot = getSlot(slotNumbers[i]);

            if (slot == null || quantities[i] <= 0) {
                this.lastStatus = PurchaseStatus.INVALID_ORDER;
                return false;
            }
            if (!slot.getIngredientType().isRamenIngredient()) {
                this.lastStatus = PurchaseStatus.NOT_A_RAMEN_INGREDIENT;
                this.lastItemName = slot.getItemName();
                return false;
            }
            if (slot.getQuantity() < quantities[i]) {
                this.lastStatus = PurchaseStatus.OUT_OF_STOCK;
                this.lastItemName = slot.getItemName();
                return false;
            }

            if (slot.getIngredientType() == IngredientType.NOODLE) {
                hasNoodles = true;
            }
            if (slot.getIngredientType() == IngredientType.BROTH) {
                hasBroth = true;
            }
            totalPrice += slot.getPrice() * quantities[i];
        }

        if (!hasNoodles || !hasBroth) {
            this.lastStatus = PurchaseStatus.MISSING_NOODLES_OR_BROTH;
            this.lastItemName = PRODUCT_NAME;
            return false;
        }

        int paid = getCashTotal(insertedCash);
        this.lastPrice = totalPrice;
        this.lastPaid = paid;

        if (paid < totalPrice) {
            this.lastStatus = PurchaseStatus.INSUFFICIENT_PAYMENT;
            return false;
        }

        int[] changePlan = makeChangePlan(paid - totalPrice, insertedCash);
        if (changePlan == null) {
            this.lastStatus = PurchaseStatus.NO_EXACT_CHANGE;
            return false;
        }

        int totalCalories = computeRamenCalories(slotNumbers, quantities);
        this.lastPreparation = buildPreparationSteps(slotNumbers, quantities);

        // Second pass: the order is certain to succeed, so consume the units.
        for (int i = 0; i < slotNumbers.length; i++) {
            getSlot(slotNumbers[i]).consume(quantities[i]);
        }

        addInsertedCash(insertedCash);
        removeChange(changePlan);

        this.ramenRevenue += totalPrice;
        this.ramenCount++;

        this.lastChange = changePlan;
        this.lastCalories = totalCalories;
        this.lastStatus = PurchaseStatus.SUCCESS;
        return true;
    }

    /**
     * Works out the calorie count of a bowl.
     *
     * @param slotNumbers the one-based slots chosen
     * @param quantities  the orders of each chosen slot
     * @return the combined calorie count, rounded to the nearest calorie
     */
    public int computeRamenCalories(int[] slotNumbers, int[] quantities) {
        int rawTotal = 0;
        int noodleCalories = 0;
        boolean hasBroth = false;
        boolean hasAddOn = false;

        for (int i = 0; i < slotNumbers.length; i++) {
            ItemSlot slot = getSlot(slotNumbers[i]);
            if (slot == null) {
                continue;
            }

            int contribution = slot.getCalories() * quantities[i];
            rawTotal += contribution;

            if (slot.getIngredientType() == IngredientType.NOODLE) {
                noodleCalories += contribution;
            } else if (slot.getIngredientType() == IngredientType.BROTH) {
                hasBroth = true;
            } else if (slot.getIngredientType() == IngredientType.ADD_ON) {
                hasAddOn = true;
            }
        }

        double total = rawTotal;
        if (hasBroth) {
            total += BROTH_ABSORPTION_RATE * noodleCalories;
        }
        if (hasAddOn) {
            total *= (1.0 + ADD_ON_COATING_RATE);
        }
        return (int) Math.round(total);
    }

    /**
     * Returns the running price of an order without buying it, so the view can show a total while the customer is still choosing.
     *
     * @param slotNumbers the one-based slots chosen
     * @param quantities  the orders of each chosen slot
     * @return the total price in pesos
     */
    public int computeRamenPrice(int[] slotNumbers, int[] quantities) {
        int total = 0;
        for (int i = 0; i < slotNumbers.length; i++) {
            ItemSlot slot = getSlot(slotNumbers[i]);
            if (slot != null) {
                total += slot.getPrice() * quantities[i];
            }
        }
        return total;
    }

    /**
     * Describes, step by step, how the machine puts a bowl together.
     *
     * @param slotNumbers the one-based slots chosen
     * @param quantities  the orders of each chosen slot
     * @return the preparation steps, in order
     */
    private ArrayList<String> buildPreparationSteps(int[] slotNumbers, int[] quantities) {
        ArrayList<String> steps = new ArrayList<String>();

        String noodles = describeRole(slotNumbers, quantities, IngredientType.NOODLE);
        String broths = describeRole(slotNumbers, quantities, IngredientType.BROTH);
        String toppings = describeRole(slotNumbers, quantities, IngredientType.TOPPING);
        String addOns = describeRole(slotNumbers, quantities, IngredientType.ADD_ON);

        steps.add("Blanching " + noodles + "...");
        steps.add("Heating " + broths + "...");
        steps.add("Placing " + noodles + " in cup...");

        if (!toppings.isEmpty()) {
            steps.add("Topping with " + toppings + "...");
        }
        steps.add("Pouring " + broths + "...");

        if (!addOns.isEmpty()) {
            steps.add("Drizzling " + addOns + "...");
        }
        steps.add(PRODUCT_NAME + " done!");
        return steps;
    }

    /**
     * Joins the names of every component playing one role into a phrase.
     *
     * @param slotNumbers the one-based slots chosen
     * @param quantities  the orders of each chosen slot
     * @param role        the role to collect
     * @return a phrase such as "noodles (x2) and chashu pork", or an empty string when no component plays that role
     */
    private String describeRole(int[] slotNumbers, int[] quantities, IngredientType role) {
        ArrayList<String> names = new ArrayList<String>();

        for (int i = 0; i < slotNumbers.length; i++) {
            ItemSlot slot = getSlot(slotNumbers[i]);
            if (slot != null && slot.getIngredientType() == role) {
                String name = slot.getItemName().toLowerCase();
                if (quantities[i] > 1) {
                    name += " (x" + quantities[i] + ")";
                }
                names.add(name);
            }
        }

        String phrase = "";
        for (int i = 0; i < names.size(); i++) {
            if (i > 0) {
                phrase += (i == names.size() - 1) ? " and " : ", ";
            }
            phrase += names.get(i);
        }
        return phrase;
    }

    /**
     * Returns the narration of the most recent successful ramen.
     *
     * @return a copy of the preparation steps, empty if none has been made
     */
    public ArrayList<String> getLastPreparation() {
        return new ArrayList<String>(this.lastPreparation);
    }

    /**
     * Returns the slots holding items that may be built into a ramen.
     *
     * @return the usable component slots, in slot order
     */
    public ArrayList<ItemSlot> getIngredientSlots() {
        ArrayList<ItemSlot> usable = new ArrayList<ItemSlot>();
        for (int i = 0; i < this.slots.size(); i++) {
            if (this.slots.get(i).getIngredientType().isRamenIngredient()) {
                usable.add(this.slots.get(i));
            }
        }
        return usable;
    }

    /** {@inheritDoc} */
    @Override
    public int getTotalSales() {
        return super.getTotalSales() + this.ramenRevenue;
    }

    /**
     * Returns the money taken for assembled bowls alone.
     *
     * @return the ramen revenue in pesos
     */
    public int getRamenRevenue() {
        return this.ramenRevenue;
    }

    /**
     * Returns how many bowls have been assembled since the last stocking.
     *
     * @return the ramen count
     */
    public int getRamenCount() {
        return this.ramenCount;
    }

    /** {@inheritDoc} */
    @Override
    public void resetTransactionSummary() {
        super.resetTransactionSummary();
        this.ramenRevenue = 0;
        this.ramenCount = 0;
    }
}
