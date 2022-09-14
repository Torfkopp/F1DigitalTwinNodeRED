package f1digitaltwin.car;

/**
 * Class representing a wing
 */
class Wing {

    private double condition;

    /**
     * Constructor
     */
    Wing() {
        condition = 0.0;
    }

    /**
     * @return The wing's condition
     */
    double getCondition() {
        return condition;
    }

    /**
     * @param condition of the wing
     */
    public void setCondition(double condition) {
        this.condition = condition;
    }
}
