package model;

/** The outcome of an attempted purchase. */
public enum PurchaseStatus {

    /** The item or ramen was dispensed and change was produced. */
    SUCCESS,

    /** The slot number given does not exist in this machine. */
    INVALID_SLOT,

    /** The item exists but may not be bought on its own. */
    NOT_SOLD_INDIVIDUALLY,

    /** The slot is empty, or holds fewer units than the order needs. */
    OUT_OF_STOCK,

    /** The cash inserted is less than the price. */
    INSUFFICIENT_PAYMENT,

    /** The machine cannot assemble the exact change owed. */
    NO_EXACT_CHANGE,

    /** The ramen order was empty or badly formed. */
    INVALID_ORDER,

    /** The order includes an item that is not a ramen ingredient. */
    NOT_A_RAMEN_INGREDIENT,

    /** The order is missing noodles, broth, or both. */
    MISSING_NOODLES_OR_BROTH
}
