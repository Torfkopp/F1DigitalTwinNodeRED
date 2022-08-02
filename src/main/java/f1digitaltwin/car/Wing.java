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
     * @param condition The wing's condition
     */
    void addDegradation(double condition) {
        this.condition += condition;
        if (this.condition > 100) this.condition = 100;
    }

    /**
     * @return The wing's condition
     */
    double getCondition() {
        return condition;
    }
}
