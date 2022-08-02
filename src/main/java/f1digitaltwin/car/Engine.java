package f1digitaltwin.car;

/**
 * Class representing the engine
 */
class Engine {

    private double degradation;

    /**
     * @param degradation The engine's degradation
     */
    Engine(double degradation) {
        this.degradation = degradation;
    }

    /**
     * @param degradation The engine's new degradation
     * @return whether the engine is over 100 degradation
     */
    boolean addDegradation(double degradation) {
        this.degradation += degradation;
        return this.degradation >= 100;
    }

    /**
     * @return The engine's degradation
     */
    double getDegradation() {
        return degradation;
    }

}
