package f1digitaltwin.car;

import java.util.stream.IntStream;

/**
 * Class representing the car
 */
public class Car {
    private final Engine engine;
    private final Fuel fuel;
    private final Wing rearWing;
    private final Tyre[] tyres;
    private Wing frontWing;

    /**
     * Constructor
     *
     * @param engineDeg The engine's degradation
     * @param fuelLoad  The amount of fuel to be put in
     * @param type      The type of the tyres
     * @param tyreAge   The tyres' age
     * @param tyreDeg   The tyres' degradation
     */
    public Car(double engineDeg, double fuelLoad, Tyre.Type type, int tyreAge, double tyreDeg) {
        engine = new Engine(engineDeg);
        fuel = new Fuel(fuelLoad);
        frontWing = new Wing();
        rearWing = new Wing();

        tyres = new Tyre[4];
        changeTyres(type, tyreAge, tyreDeg);
    }

    /**
     * @param degradation The amount to degrade the engine by
     * @return whether the engine is working
     */
    public boolean degradeEngine(double degradation) {
        return engine.addDegradation(degradation);
    }

    /**
     * Degrades all the tyres by the specified amount
     *
     * @param deg 0 fr, 1 fl, 2 rr, 3 rl
     * @return whether a tyre has a problem
     */
    public boolean degradeTyres(double[] deg) {
        return tyres[0].addDegradation(deg[0]) ||
                tyres[1].addDegradation(deg[1]) ||
                tyres[2].addDegradation(deg[2]) ||
                tyres[3].addDegradation(deg[3]);
    }

    /**
     * Method to degrade the wings
     */
    public void degradeWings(double[] wingDeg) {
        frontWing.addDegradation(wingDeg[0]);
        rearWing.addDegradation(wingDeg[1]);
    }

    /**
     * @return The engine's degradation
     */
    public double getEngineDeg() {
        return engine.getDegradation();
    }

    /**
     * @return The amount of fuel remaining
     */
    public double getFuelLoad() {
        return fuel.getAmount();
    }

    /**
     * Front Right, Front Left, Rear Right, Rear Left
     *
     * @return The tyres' degradation
     */
    public double[] getTyreDeg() {
        return IntStream.range(0, 4).mapToDouble(i -> tyres[i].getDegradation()).toArray();
    }

    /**
     * Front Right, Front Left, Rear Right, Rear Left
     *
     * @return The tyres' status
     */
    public String[] getTyreStatus() {
        return new String[]{String.valueOf(tyres[0].getType()), String.valueOf(tyres[3].getAge())};
    }

    /**
     * @return The tyres' type
     */
    public Tyre.Type getTyreType() {
        return tyres[0].getType();
    }

    /**
     * Front Wing at 0, Rear Wing at 1
     *
     * @return The wings' status
     */
    public double[] getWingStatus() {
        return new double[]{frontWing.getCondition(), rearWing.getCondition()};
    }

    /**
     * @param amount The amount of fuel to burn
     * @return whether the car has enough fuel
     */
    public boolean loseFuel(double amount) {
        return fuel.decreaseAmount(amount);
    }

    /**
     * @param type The type of tyre to change to
     */
    public void newTyres(Tyre.Type type) {
        changeTyres(type, 0, 0.0);
    }

    /**
     * Change the front wing to a new one
     */
    public void newWing() {
        frontWing = new Wing();
    }

    /**
     * Sets the tyres' status
     *
     * @param type The tyres' type
     * @param age  The tyres' age
     * @param deg  The tyres' degradation
     */
    private void changeTyres(Tyre.Type type, int age, double deg) {
        for (int i = 0; i < 4; i++) tyres[i] = new Tyre(type, age, deg);
    }
}
