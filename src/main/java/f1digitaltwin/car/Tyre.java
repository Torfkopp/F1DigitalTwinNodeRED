package f1digitaltwin.car;

/**
 * Class representing a tyre
 */
public class Tyre {

    /**
     * The types an F1 tyre can have.
     * C1 is the hardest compound,
     * C5 the softest.
     */
    public enum Type {
        SOFT,
        MEDIUM,
        HARD,
        //INTERMEDIATE,
        //WETS
    }

    private final Type type;
    private int age;
    private double degradation;

    /**
     * Constructor
     *
     * @param tyreType The tyre's types
     * @param tyreAge  The tyre's age
     * @param tyreDeg  The tyre's degradation
     */
    Tyre(Type tyreType, int tyreAge, double tyreDeg) {
        type = tyreType;
        age = tyreAge;
        degradation = tyreDeg;
    }

    public void increaseAge() {
        age++;
    }

    /**
     * @return The tyre's age
     */
    int getAge() {
        return age;
    }

    /**
     * @return The tyre's degradation
     */
    double getDegradation() {
        return degradation;
    }

    /**
     * @param degradation of the tyre
     */
    public void setDegradation(double degradation) {
        this.degradation = degradation;
    }

    /**
     * @return The tyre's type
     */
    Type getType() {
        return type;
    }
}
