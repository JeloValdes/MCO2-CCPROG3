package model;

/** The kinds of problem the operator dashboard can report. */
public enum AlertType {

    /** A slot has run out entirely. */
    SLOT_EMPTY(true),

    /** A slot is close to running out. */
    SLOT_LOW(false),

    /** A denomination the machine relies on for change has run out. */
    DENOMINATION_EMPTY(true),

    /** A denomination is running low. */
    DENOMINATION_LOW(false),

    /** The change fund as a whole is too small to cover normal trading. */
    CHANGE_FUND_LOW(true),

    /** The machine has fewer slots than the specifications require. */
    BELOW_MINIMUM_SLOTS(true);

    private final boolean critical;

    /**
     * Creates an alert kind with a severity.
     *
     * @param critical true if the problem stops the machine trading normally
     */
    AlertType(boolean critical) {
        this.critical = critical;
    }

    /**
     * Reports whether this problem needs attention before trading continues.
     *
     * @return true for a critical problem, false for a warning
     */
    public boolean isCritical() {
        return this.critical;
    }
}
