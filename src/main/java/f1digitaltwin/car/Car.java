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
     * @return The engine's degradation
     */
    public double getEngineDeg() {
        return engine.getDegradation();
    }

    public void setEngineDeg(double deg) {
        engine.setDegradation(deg);
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
     * Type and Age of the tyres; the same for all four
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
     * @param amount of fuel
     */
    public void setFuelAmount(double amount) {
        fuel.setAmount(amount);
    }

    /**
     * @param deg0 Front Right
     * @param deg1 Front Left
     * @param deg2 Rear Right
     * @param deg3 Rear Left
     */
    public void setTyreDegradation(double deg0, double deg1, double deg2, double deg3) {
        tyres[0].setDegradation(deg0);
        tyres[1].setDegradation(deg1);
        tyres[2].setDegradation(deg2);
        tyres[3].setDegradation(deg3);
    }

    /**
     * Increases the Tyres' age
     */
    public void setTyreStatus() {
        for (Tyre t : tyres) t.increaseAge();
    }

    /**
     * @param conFront Front wing's condition
     * @param conRear  Rear wing's condition
     */
    public void setWingCondition(double conFront, double conRear) {
        frontWing.setCondition(conFront);
        rearWing.setCondition(conRear);
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
