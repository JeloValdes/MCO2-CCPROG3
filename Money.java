/**
 * Represents a single denomination of money held by the vending machine.
 *
 * <p>A {@code Money} object stores the face value of the denomination (for
 * example 1, 5, 20, or 100 pesos) together with the quantity of that
 * denomination currently held by the machine.</p>
 */
public class Money {
    private int qty = 0;
    private int value;

    /**
     * Creates a denomination with a given face value and quantity.
     * @param qty   the initial quantity held (pre: qty &gt;= 0)
     * @param value the face value of the denomination
     */
    public Money(int qty, int value) {
        this.qty = qty;
        this.value = value;
    }

    /**
     * Creates a denomination with a given face value and a quantity of zero.
     * @param value the face value of the denomination
     */
    public Money(int value) {
        this.value = value;
    }

    /**
     * Sets the quantity of this denomination held by the machine.
     * @param qty the new quantity (pre: qty &gt;= 0)
     */
    public void setQty(int qty) {
        this.qty = qty;
    }

    /**
     * Increases the quantity of this denomination by the given amount.
     * @param qty the number of pieces to add (pre: qty &gt;= 0)
     */
    public void addQty(int qty) {
        this.qty += qty;
    }

    /**
     * Reduces the quantity of this denomination by the given amount.
     * @param qty the number of pieces to remove (pre: qty &lt;= current quantity)
     */
    public void deductQty(int qty) {
        this.qty -= qty;
    }

    /**
     * Sets the face value of this denomination.
     * @param value the new face value
     */
    public void setValue(int value) {
        this.value = value;
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
