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
     * @return The engine's degradation
     */
    double getDegradation() {
        return degradation;
    }

    /**
     * @param degradation of the engine
     */
    public void setDegradation(double degradation) {
        this.degradation = degradation;
    }
}
