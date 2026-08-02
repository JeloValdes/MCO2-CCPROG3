package model;

/** One problem found by the operator dashboard. */
public class Alert {

    private final AlertType type;
    private final String subject;
    private final int value;
    private final int threshold;

    /**
     * Creates an alert about one item, denomination, or machine-wide figure.
     *
     * @param type      the kind of problem
     * @param subject   what the problem concerns, such as an item name
     * @param value     the figure that triggered the alert
     * @param threshold the figure the value was compared against
     */
    public Alert(AlertType type, String subject, int value, int threshold) {
        this.type = type;
        this.subject = subject;
        this.value = value;
        this.threshold = threshold;
    }

    /**
     * Returns the kind of problem.
     *
     * @return the alert type
     */
    public AlertType getType() {
        return this.type;
    }

    /**
     * Returns what the problem concerns.
     *
     * @return the subject, such as an item name or denomination label
     */
    public String getSubject() {
        return this.subject;
    }

    /**
     * Returns the figure that triggered the alert.
     *
     * @return the measured value
     */
    public int getValue() {
        return this.value;
    }

    /**
     * Returns the figure the value was compared against.
     *
     * @return the threshold
     */
    public int getThreshold() {
        return this.threshold;
    }

    /**
     * Reports whether this problem needs attention before trading continues.
     *
     * @return true for a critical problem, false for a warning
     */
    public boolean isCritical() {
        return this.type.isCritical();
    }
}
