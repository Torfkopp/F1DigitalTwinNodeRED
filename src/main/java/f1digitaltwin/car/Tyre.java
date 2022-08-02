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

    /**
     * Adds degradation to the tyre
     * Can't be less than 0 or more than 100.
     *
     * @param i The amount of deg to add
     * @return whether a tyre is over 100 degradation
     */
    boolean addDegradation(double i) {
        age++;
        degradation += i;
        return degradation > 100;
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
     * @return The tyre's type
     */
    Type getType() {
        return type;
    }
}
