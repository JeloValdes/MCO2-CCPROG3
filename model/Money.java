package model;

/**
 * Represents a single denomination of money held by the vending machine.
 *
 * <p>A {@code Money} object stores the face value of the denomination (for
 * example 1, 5, 20, or 100 pesos) together with the quantity of that
 * denomination currently held by the machine.</p>
 */
public class Money {
    private int qty;
    private int value;

    /**
     * Creates a denomination with a given face value.
     * @param value the face value of the denomination
     */
    public Money(int value) {
        this.qty = 0;
        this.value = value;
    }



    /**
     * Increases the quantity of this denomination by the given amount.
     * @param qty the number of pieces to add (pre: qty &gt;= 0)
     */
    public void addQty(int qty) {
        this.qty += qty;
    }


    public boolean removeQty(int amount) {
        if (amount < 0 || amount > qty) {
            return false;
        }

        qty -= amount;
        return true;
    }



    /**
     * Returns the face value of this denomination.
     * @return the face value
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Returns the quantity of this denomination currently held.
     * @return the quantity held
     */
    public int getQty() {
        return this.qty;
    }
}