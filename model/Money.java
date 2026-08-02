package model;

/** One denomination of money together with the number of pieces held. */
public class Money {

    private final int value;
    private int qty;

    /**
     * Creates an empty tray for a denomination.
     *
     * @param value the face value of the denomination (pre: value &gt; 0)
     */
    public Money(int value) {
        this.value = value;
        this.qty = 0;
    }

    /**
     * Creates a tray already holding pieces.
     *
     * @param value the face value of the denomination (pre: value &gt; 0)
     * @param qty   the initial number of pieces (pre: qty &gt;= 0)
     */
    public Money(int value, int qty) {
        this.value = value;
        this.qty = qty;
    }

    /**
     * Adds pieces to this tray.
     *
     * @param qty the number of pieces to add (pre: qty &gt;= 0)
     */
    public void addQty(int qty) {
        if (qty > 0) {
            this.qty += qty;
        }
    }

    /**
     * Removes pieces from this tray, refusing to go below zero.
     *
     * @param qty the number of pieces to remove
     * @return true if the tray held that many pieces and they were removed
     */
    public boolean removeQty(int qty) {
        if (qty < 0 || qty > this.qty) {
            return false;
        }
        this.qty -= qty;
        return true;
    }

    /**
     * Returns the face value of this denomination.
     *
     * @return the face value in pesos
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Returns the number of pieces currently held.
     *
     * @return the quantity held
     */
    public int getQty() {
        return this.qty;
    }

    /**
     * Returns the total peso value stored in this tray.
     *
     * @return the face value multiplied by the quantity held
     */
    public int getTotalValue() {
        return this.value * this.qty;
    }
}
