import java.util.ArrayList;
import java.util.Scanner;

/**
 * Simulates a Regular Vending Machine for the Vending Machine Factory (MCO1).
 *
 * <p>The machine holds a set of unique {@link Item}s (one per slot) and a set
 * of {@link Money} denominations used to receive payment and dispense change.
 * It supports two groups of features:</p>
 * <ul>
 *   <li><b>Vending features</b> ({@link #vending()}): inserting cash,
 *       returning inserted cash, and buying an item with automatic change.</li>
 *   <li><b>Maintenance features</b> ({@link #maintenance()}): adding,
 *       restocking, removing items, setting item prices, collecting and
 *       replenishing money, and printing a transaction summary.</li>
 * </ul>
 */
public class VendingMachine {

    /** Maximum number of item slots the machine can hold. */
    private static final int MAX_SLOTS = 20;
    /** Maximum number of units a single slot can store. */
    private static final int MAX_PER_SLOT = 10;

    private ArrayList<Item> items = new ArrayList<>();
    private Money[] money = new Money[9];
    private int[] denominations = {1, 5, 10, 20, 50, 100, 200, 500, 1000};

    private Scanner scanner;
    private Input input;

    /**
     * Creates a Regular Vending Machine with empty slots and one entry per
     * supported denomination (each starting at a quantity of zero).
     *
     * @param scanner the shared scanner used to read user input
     */
    public VendingMachine(Scanner scanner) {
        this.scanner = scanner;
        this.input = new Input(scanner);

        for (int i = 0; i < money.length; i++) {
            money[i] = new Money(this.denominations[i]);
        }
    }

    /**
     * Runs the maintenance features menu until the user chooses to exit.
     *
     * <p>From here the operator can stock/restock/remove items and set their
     * prices, collect or replenish the machine's money, fill the machine with
     * a set of default items, and print a transaction summary.</p>
     */
    public void maintenance() {
        int sysEnd = -1;

        while (sysEnd != 1) {
            System.out.println();
            System.out.println("What do you wish to do maintenance on?");
            System.out.println("Stock / Restocking [1]");
            System.out.println("Collect / Replenish Money [2]");
            System.out.println("Print Transaction Summary [3]");
            System.out.println("Fill with Default Items [4]");
            System.out.println("Exit [5]");
            int choice = input.receive(1, 5);

            if (choice == 1) {
                stockMenu();
            } else if (choice == 2) {
                moneyMenu();
            } else if (choice == 3) {
                printSummary();
            } else if (choice == 4) {
                fillVending();
                System.out.println("Filled with Default items!");
            } else {
                sysEnd = 1;
            }
        }
    }

    /**
     * Handles the stock / restocking sub-menu: displaying current stock,
     * adding a new item, restocking an existing item, removing an item, and
     * setting the price of an existing item.
     */
    private void stockMenu() {
        int sysEnd = 0;

        while (sysEnd != 1) {
            System.out.println();
            System.out.println("CURRENT STOCK: ");
            for (int i = 0; i < this.items.size(); i++) {
                System.out.println("Item Name: " + this.items.get(i).getName()
                        + " QTY: " + this.items.get(i).getQuantity()
                        + " Price: " + this.items.get(i).getPrice()
                        + " Calories: " + this.items.get(i).getCalories());
            }
            System.out.println("ADD NEW ITEM [1]");
            System.out.println("RESTOCK EXISTING ITEM [2]");
            System.out.println("REMOVE ITEM [3]");
            System.out.println("SET ITEM PRICE [4]");
            System.out.println("BACK [5]");
            int choice = input.receive(1, 5);

            if (choice == 1) {
                addNewItem();
            } else if (choice == 2) {
                restockItem();
            } else if (choice == 3) {
                removeItem();
            } else if (choice == 4) {
                setItemPrice();
            } else {
                sysEnd = 1;
            }
        }
    }

    /**
     * Prompts for the details of a new item and adds it to the machine, as
     * long as the name is unique and there is a free slot available.
     */
    private void addNewItem() {
        System.out.println("Enter item name: ");
        String itemName = scanner.nextLine().trim();

        if (findItemIndex(itemName) != -1) {
            System.out.println("Item already in the machine! Press restock if you wish to replenish items.");
            return;
        }
        if (this.items.size() >= MAX_SLOTS) {
            System.out.println("Error, vending machine is full! Max capacity: " + MAX_SLOTS);
            return;
        }

        System.out.println("Enter item price: ");
        int itemPrice = input.receiveNonNegative();
        System.out.println("Enter item quantity: ");
        int itemQuantity = input.receive(1, MAX_PER_SLOT); // capacity per slot
        System.out.println("Enter item calories: ");
        int itemCalories = input.receiveNonNegative();

        this.items.add(new Item(itemQuantity, itemCalories, itemPrice, itemName));
        System.out.println("Item successfully added!");
    }

    /**
     * Restocks an existing item by name, respecting the per-slot capacity, and
     * resets that item's transaction-summary baseline.
     */
    private void restockItem() {
        System.out.print("Enter item to restock: ");
        String restockItem = scanner.nextLine().trim();
        int itemIndex = findItemIndex(restockItem);

        if (itemIndex == -1) {
            System.out.println("Item not found.");
            return;
        }

        System.out.print("Enter quantity: ");
        int restockQty = input.receivePositive();

        if (items.get(itemIndex).getQuantity() + restockQty > MAX_PER_SLOT) {
            System.out.println("Slots cannot store more than " + MAX_PER_SLOT + " items!");
        } else {
            items.get(itemIndex).addQuantity(restockQty);
            items.get(itemIndex).resetSummaryBaseline();
            System.out.println("Item successfully restocked with " + restockQty + " pieces");
        }
    }

    /**
     * Removes an existing item from the machine by name.
     */
    private void removeItem() {
        System.out.println("Enter item to remove: ");
        String removeItem = scanner.nextLine().trim();
        int itemIndex = findItemIndex(removeItem);

        if (itemIndex != -1) {
            this.items.remove(itemIndex);
            System.out.println("The item " + removeItem + " has successfully been removed!");
        } else {
            System.out.println("Item " + removeItem + " cannot be found.");
        }
    }

    /**
     * Sets a new selling price for an existing item, identified by name.
     */
    private void setItemPrice() {
        System.out.print("Enter item to set price: ");
        String priceItem = scanner.nextLine().trim();
        int itemIndex = findItemIndex(priceItem);

        if (itemIndex == -1) {
            System.out.println("Item not found.");
            return;
        }

        System.out.println("Current price: Php " + items.get(itemIndex).getPrice());
        System.out.println("Enter new price: ");
        int newPrice = input.receiveNonNegative();
        items.get(itemIndex).setPrice(newPrice);
        System.out.println("Price updated to Php " + newPrice);
    }

    /**
     * Handles the collect / replenish money sub-menu.
     */
    private void moneyMenu() {
        int sysEnd = 0;

        while (sysEnd != 1) {
            System.out.println();
            System.out.println("Do you wish to collect or replenish money?");
            System.out.println("Replenish Money [1]");
            System.out.println("Collect Money [2]");
            System.out.println("Exit [3]");
            int moneyOption = input.receive(1, 3);

            if (moneyOption == 1) {
                replenishMoney();
            } else if (moneyOption == 2) {
                collectMoney();
            } else {
                sysEnd = 1;
            }
        }
    }

    /**
     * Adds operator-supplied quantities of any denomination to the machine so
     * that it has enough cash to dispense change.
     */
    private void replenishMoney() {
        int sysEnd = -1;

        while (sysEnd != 1) {
            System.out.println("Select money to replenish: ");
            System.out.println("1 Peso coins [1]");
            System.out.println("5 Peso coins [2]");
            System.out.println("10 Peso coins [3]");
            System.out.println("20 Peso coins [4]");
            System.out.println("50 Peso bills [5]");
            System.out.println("100 Peso bills [6]");
            System.out.println("200 Peso bills [7]");
            System.out.println("500 Peso bills [8]");
            System.out.println("1000 Peso bills [9]");
            System.out.println("Back [0]");
            int moneyChoice = input.receive(0, 9);

            if (moneyChoice > 0) {
                System.out.println("Enter Quantity: ");
                money[moneyChoice - 1].addQty(input.receivePositive());
                System.out.println("Money Accepted!");
            } else {
                sysEnd = 1;
            }
        }
    }

    /**
     * Lets the operator collect (withdraw) quantities of each denomination
     * currently held by the machine, never allowing more than is available.
     */
    private void collectMoney() {
        int sysEnd = -1;

        while (sysEnd != 1) {
            System.out.println("Select money to collect: ");
            for (int i = 0; i < money.length; i++) {
                System.out.print(money[i].getValue() + " Peso ");
                if (money[i].getValue() > 20) {
                    System.out.print("bill/s ");
                } else {
                    System.out.print("coin/s ");
                }
                System.out.print("qty : " + money[i].getQty());
                System.out.print(" [" + (i + 1) + "]\n");
            }
            System.out.println("Exit [0]");
            System.out.println("Input: ");
            int choice = input.receive(0, 9);

            if (choice > 0) {
                System.out.println("Enter Quantity to collect: ");
                int collectQty = input.receivePositive();
                if (collectQty <= money[choice - 1].getQty()) {
                    money[choice - 1].deductQty(collectQty);
                    System.out.println("Collected.");
                } else {
                    System.out.println("You cannot collect more than the available amount.");
                }
            } else {
                sysEnd = 1;
            }
        }
    }

    /**
     * Runs the vending features menu until the user chooses to go back.
     *
     * <p>The user may insert cash in any denomination, get their inserted cash
     * back (as if changing their mind), or select an item. On a successful
     * purchase, the item is dispensed and the correct change is produced from
     * the machine's own money inventory. If the machine cannot make exact
     * change, the transaction does not take place and the user is informed.</p>
     *
     * @return {@code 1} once the user chooses to leave the vending features
     */
    public int vending() {
        int[] insertedCash = {0, 0, 0, 0, 0, 0, 0, 0, 0};
        int sysEnd = -1;

        while (sysEnd != 1) {
            displayItems();
            System.out.println("Insert Cash [1]");
            System.out.println("Get Change [2]");
            System.out.println("Select Item [3]");
            System.out.println("Back [4]");
            int choice = input.receive(1, 4);

            if (choice == 1) {
                insertCash(insertedCash);
            } else if (choice == 2) {
                returnInsertedCash(insertedCash);
            } else if (choice == 3) {
                selectItem(insertedCash);
            } else {
                sysEnd = 1;
            }
        }

        return 1;
    }

    /**
     * Repeatedly accepts single pieces of any denomination into the current
     * transaction until the user chooses to stop.
     *
     * @param insertedCash the running tally of pieces inserted this transaction
     */
    private void insertCash(int[] insertedCash) {
        int sysEnd = 0;

        while (sysEnd != 1) {
            System.out.println("Insert 1 Peso Coin [1]");
            System.out.println("Insert 5 Peso Coin [2]");
            System.out.println("Insert 10 Peso Coin [3]");
            System.out.println("Insert 20 Peso Coin [4]");
            System.out.println("Insert 50 Peso Bill [5]");
            System.out.println("Insert 100 Peso Bill [6]");
            System.out.println("Insert 200 Peso Bill [7]");
            System.out.println("Insert 500 Peso Bill [8]");
            System.out.println("Insert 1000 Peso Bill [9]");
            System.out.println("Exit [0]");
            System.out.println("Input: ");
            int choice = input.receive(0, 9);

            if (choice > 0) {
                insertedCash[choice - 1] += 1;
                System.out.println("Cash accepted!");
            } else {
                sysEnd = 1;
            }
        }
    }

    /**
     * Returns the cash the user has inserted so far (the "changed my mind"
     * case), printing the denominations returned and clearing the tally.
     *
     * @param insertedCash the pieces inserted this transaction
     */
    private void returnInsertedCash(int[] insertedCash) {
        int sysEnd = 0;

        while (sysEnd != 1) {
            System.out.println("Do you wish to dispense the inserted money?");
            System.out.println("Confirm [1]");
            System.out.println("Back [2]");
            int choice = input.receive(1, 2);

            if (choice == 1) {
                System.out.println("Dispensed Cash: ");
                for (int j = 0; j < insertedCash.length; j++) {
                    if (insertedCash[j] > 0) {
                        System.out.print("/ " + denominations[j]);
                        if (denominations[j] > 20) {
                            System.out.print(" Peso Bills");
                        } else {
                            System.out.print(" Peso Coin/s");
                        }
                        System.out.print(" / QTY: " + insertedCash[j] + "\n");
                    }
                    insertedCash[j] = 0;
                }
            } else {
                sysEnd = 1;
            }
        }
    }

    /**
     * Handles selecting and purchasing an item using the currently inserted
     * cash. Validates stock, sufficient payment, and the machine's ability to
     * make exact change before completing the sale.
     *
     * @param insertedCash the pieces inserted this transaction
     */
    private void selectItem(int[] insertedCash) {
        if (this.items.isEmpty()) {
            System.out.println("No items available to select.");
            return;
        }

        System.out.println("Enter item number: ");
        int itemPicked = input.receive(1, this.items.size());
        Item selectedItem = this.items.get(itemPicked - 1);

        int price = selectedItem.getPrice();
        int insertedTotal = sumMoney(insertedCash);
        int changeAmount = insertedTotal - price;

        if (selectedItem.getQuantity() <= 0) {
            System.out.println("Sorry, this item is out of stock.");
        } else if (insertedTotal < price) {
            System.out.println("Not enough cash inserted.");
            System.out.println("Item price: Php " + price);
            System.out.println("Inserted cash: Php " + insertedTotal);
            System.out.println("Please insert more cash.");
        } else {
            int[] changePlan = findChange(changeAmount);

            if (changePlan == null) {
                System.out.println("Sorry, the machine does not have enough change.");
                System.out.println("Please insert exact amount or get your inserted cash back.");
            } else {
                System.out.println("Dispensing " + selectedItem.getName()
                        + " (" + selectedItem.getCalories() + " calories) for Php " + price);

                selectedItem.recordSale();

                addInsertedCashToMachine(insertedCash);
                deductChangeFromMachine(changePlan);
                clearInsertedCash(insertedCash);

                printChange(changePlan);
            }
        }
    }

    /**
     * Fills the machine with a set of default ramen-themed items. These are
     * chosen so that they align with the planned MCO2 customizable product
     * (a customizable ramen), while remaining sellable individually here.
     */
    public void fillVending() {
        this.items.clear();
        this.items.add(new Item(10, 200, 25, "Noodles"));
        this.items.add(new Item(8, 300, 45, "Chashu Pork"));
        this.items.add(new Item(10, 90, 15, "Aji Tamago"));
        this.items.add(new Item(9, 120, 20, "Fried Tofu"));
        this.items.add(new Item(8, 80, 15, "Fish Cake"));
        this.items.add(new Item(10, 10, 5, "Negi"));
        this.items.add(new Item(6, 260, 50, "Gyoza"));
        this.items.add(new Item(7, 180, 35, "Onigiri"));
        this.items.add(new Item(8, 120, 30, "Edamame"));
        this.items.add(new Item(10, 0, 20, "Bottled Water"));
        this.items.add(new Item(9, 5, 25, "Green Tea"));
        this.items.add(new Item(8, 60, 40, "Iced Coffee"));
        this.items.add(new Item(6, 150, 45, "Mochi Ice Cream"));
    }

    /**
     * Prints the transaction summary covering the period since the last
     * (re)stocking. For each item it shows the starting inventory, the ending
     * (current) inventory, and the quantity sold, followed by the total amount
     * collected from sales.
     */
    public void printSummary() {
        System.out.println();
        System.out.println("================= TRANSACTION SUMMARY =================");
        if (this.items.isEmpty()) {
            System.out.println("No items in the machine.");
            System.out.println("======================================================");
            return;
        }

        System.out.printf("%-20s %-8s %-8s %-8s %-10s%n",
                "Item", "Start", "End", "Sold", "Revenue");
        System.out.println("------------------------------------------------------");

        int totalSales = 0;
        for (int i = 0; i < this.items.size(); i++) {
            Item it = this.items.get(i);
            int revenue = it.getQtySold() * it.getPrice();
            totalSales += revenue;
            System.out.printf("%-20s %-8d %-8d %-8d Php %-8d%n",
                    shortenName(it.getName(), 20),
                    it.getStartingStock(),
                    it.getQuantity(),
                    it.getQtySold(),
                    revenue);
        }

        System.out.println("------------------------------------------------------");
        System.out.println("Total amount collected from sales: Php " + totalSales);
        System.out.println("======================================================");
        System.out.println();
    }

    /**
     * Shortens a name with an ellipsis if it exceeds the given length, so that
     * table columns stay aligned.
     *
     * @param name      the name to shorten
     * @param maxLength the maximum allowed length
     * @return the original name, or a truncated version ending in "..."
     */
    private String shortenName(String name, int maxLength) {
        if (name.length() <= maxLength) {
            return name;
        }
        return name.substring(0, maxLength - 3) + "...";
    }

    /**
     * Prints the item slots with their number, name, price, calories, and
     * available quantity so the user can see what is available for purchase.
     */
    private void displayItems() {
        System.out.println();
        System.out.println("======================================================================");
        System.out.println("                        VENDING MACHINE ITEMS");
        System.out.println("======================================================================");

        if (this.items.isEmpty()) {
            System.out.println("No items available.");
            System.out.println("======================================================================");
            System.out.println();
            return;
        }

        System.out.printf("%-6s %-24s %-12s %-12s %-10s%n",
                "No.", "Item Name", "Price", "Calories", "Qty");
        System.out.println("----------------------------------------------------------------------");

        for (int i = 0; i < this.items.size(); i++) {
            Item item = this.items.get(i);

            System.out.printf("[%-3d] %-24s Php %-8d %-12d %-10d%n",
                    (i + 1),
                    shortenName(item.getName(), 24),
                    item.getPrice(),
                    item.getCalories(),
                    item.getQuantity());
        }

        System.out.println("======================================================================");
        System.out.println();
    }

    /**
     * Finds the index of an item by name (case-insensitive).
     *
     * @param name the item name to search for
     * @return the index of the item in the list, or -1 if it is not found
     */
    private int findItemIndex(String name) {
        for (int i = 0; i < this.items.size(); i++) {
            if (this.items.get(i).getName().equalsIgnoreCase(name)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Computes the total peso value of a set of inserted denominations.
     *
     * @param inserted the count of each denomination, ordered like
     *                 {@code denominations}
     * @return the total value in pesos
     */
    private int sumMoney(int[] inserted) {
        int total = 0;
        for (int i = 0; i < inserted.length; i++) {
            total += inserted[i] * denominations[i];
        }
        return total;
    }

    /**
     * Attempts to build an exact-change plan for the given amount using the
     * denominations currently held by the machine.
     *
     * @param changeAmount the amount of change owed to the user
     * @return an array giving the quantity of each denomination to dispense,
     *         or {@code null} if exact change cannot be made
     */
    private int[] findChange(int changeAmount) {
        int[] availableQty = new int[money.length];
        for (int i = 0; i < money.length; i++) {
            availableQty[i] = money[i].getQty();
        }

        int[] changePlan = new int[money.length];

        if (findChangeHelper(changeAmount, money.length - 1, availableQty, changePlan)) {
            return changePlan;
        }
        return null;
    }

    /**
     * Recursive backtracking helper that tries to fill the remaining change
     * amount using denominations from {@code index} down to 0.
     *
     * @param amount       the remaining amount of change to make
     * @param index        the current denomination index being considered
     * @param availableQty the quantity available of each denomination
     * @param changePlan   the plan being built (filled in on success)
     * @return {@code true} if the remaining amount can be made exactly
     */
    private boolean findChangeHelper(int amount, int index, int[] availableQty, int[] changePlan) {
        if (amount == 0) {
            return true;
        }
        if (index < 0) {
            return false;
        }

        int denomination = denominations[index];
        int maxNeeded = amount / denomination;
        int maxCanUse = Math.min(maxNeeded, availableQty[index]);

        for (int qty = maxCanUse; qty >= 0; qty--) {
            changePlan[index] = qty;
            int remainingAmount = amount - (qty * denomination);

            if (findChangeHelper(remainingAmount, index - 1, availableQty, changePlan)) {
                return true;
            }
        }

        changePlan[index] = 0;
        return false;
    }

    /**
     * Adds the user's inserted cash to the machine's money inventory (called
     * once a purchase is confirmed).
     *
     * @param insertedCash the pieces inserted this transaction
     */
    private void addInsertedCashToMachine(int[] insertedCash) {
        for (int i = 0; i < insertedCash.length; i++) {
            if (insertedCash[i] > 0) {
                money[i].addQty(insertedCash[i]);
            }
        }
    }

    /**
     * Removes the dispensed change denominations from the machine's money
     * inventory.
     *
     * @param changePlan the quantity of each denomination being dispensed
     */
    private void deductChangeFromMachine(int[] changePlan) {
        for (int i = 0; i < changePlan.length; i++) {
            if (changePlan[i] > 0) {
                money[i].deductQty(changePlan[i]);
            }
        }
    }

    /**
     * Resets the inserted-cash tally to all zeros.
     *
     * @param insertedCash the pieces inserted this transaction
     */
    private void clearInsertedCash(int[] insertedCash) {
        for (int i = 0; i < insertedCash.length; i++) {
            insertedCash[i] = 0;
        }
    }

    /**
     * Prints the change being dispensed, from the largest denomination to the
     * smallest, or a message if no change is needed.
     *
     * @param changePlan the quantity of each denomination to dispense
     */
    private void printChange(int[] changePlan) {
        System.out.println("Dispensed Change:");
        boolean hasChange = false;

        for (int i = changePlan.length - 1; i >= 0; i--) {
            if (changePlan[i] > 0) {
                hasChange = true;
                System.out.print(denominations[i] + " Peso ");
                if (denominations[i] > 20) {
                    System.out.print("Bill/s");
                } else {
                    System.out.print("Coin/s");
                }
                System.out.println(" - QTY: " + changePlan[i]);
            }
        }

        if (!hasChange) {
            System.out.println("No change needed.");
        }
    }
}
