package model;

import java.util.ArrayList;

/* Represents one slot in the vending machine. The slot
 owns multiple Item objects of the same kind.
 */
public class ItemSlot {
    private String slotCode;
    private String itemName;
    private int calories;
    private int price;
    private boolean canSellIndividually;
    private String ingredientType;
    private int capacity;
    private ArrayList<Item> stock;

    // for the summary of sales
    private int startingStock;
    private int quantitySold;
    private int salesRevenue;

    public ItemSlot(String slotCode, String itemName, int calories, int price,
                    boolean canSellIndividually, String ingredientType,
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
        this.salesRevenue = 0;
    }


    public boolean stock(Item item) {
        if (stock.size() >= capacity) {
            return false;
        }
        stock.add(item);
        return true;
    }

    public boolean stock(int quantity) {
        if (quantity <= 0 || stock.size() + quantity > capacity) {
            return false;
        }

        for (int i = 0; i < quantity; i++) {
            Item item = new Item(itemName, calories, price,
                    canSellIndividually, ingredientType);
            stock.add(item);
        }
        return true;
    }

    // Removes one  Item object from the slot.
    public Item dispense() {
        if (stock.isEmpty()) {
            return null;
        }

        Item item = stock.remove(0);
        quantitySold++;
        salesRevenue += price;
        return item;
    }

    public String getSlotCode() {
        return slotCode;
    }

    public String getItemName() {
        return itemName;
    }

    public int getCalories() {
        return calories;
    }

    public int getPrice() {
        return price;
    }

    public boolean canSellIndividually() {
        return canSellIndividually;
    }

    public String getIngredientType() {
        return ingredientType;
    }

    public int getQuantity() {
        return stock.size();
    }

    public int getCapacity() {
        return capacity;
    }

    public int getStartingStock() {
        return startingStock;
    }

    public int getQuantitySold() {
        return quantitySold;
    }

    public int getSalesRevenue() {
        return salesRevenue;
    }

    public ArrayList<Item> getStock() {
        return new ArrayList<Item>(stock);
    }


    public void setPrice(int newPrice) {
        price = newPrice;

        for (int i = 0; i < stock.size(); i++) {
            stock.get(i).setPrice(newPrice);
        }
    }

    public void resetSummary() {
        startingStock = stock.size();
        quantitySold = 0;
        salesRevenue = 0;
    }
}
