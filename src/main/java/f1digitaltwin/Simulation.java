package f1digitaltwin;

import f1digitaltwin.car.Car;
import f1digitaltwin.car.Track;
import f1digitaltwin.car.Tyre;

import java.util.Arrays;
import java.util.OptionalDouble;

/**
 * Class to simulate the car's race
 */
public class Simulation {

    // Upgradeable with https://www.r-bloggers.com/2021/09/f1-strategy-analysis/

    private final Car car;
    private final Track.Name track;
    private boolean changedWing = false;
    private int currentLap;
    private Time lapTime;
    private boolean pitStop = false;
    private boolean problem = false;
    private int speed = 3;

    /**
     * Constructor
     *
     * @param trackName  The track's name
     * @param engineDeg  The engine's degradation
     * @param fuelAmount The amount of fuel
     * @param type       The starting tyres' type
     * @param tyreAge    The starting tyres' age
     * @param tyreDeg    The starting tyres' degradation
     */
    public Simulation(Track.Name trackName, int engineDeg, int fuelAmount, Tyre.Type type, int tyreAge, int tyreDeg) {
        track = trackName;
        car = new Car(engineDeg, fuelAmount, type, tyreAge, tyreDeg);
    }

    /**
     * @param speed to change to
     */
    public void changeSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * 0 Front Left, 1 Front Right, 2 Rear Left, 3 Rear Right
     * 4 Front Wing, 5 Rear Wing, 6 Engine, 7 Fuel
     *
     * @return Array of integer representing the part's degradation
     */
    public double[] getDegradation() {
        double[] deg = new double[8];
        System.arraycopy(car.getTyreDeg(), 0, deg, 0, 4);
        System.arraycopy(car.getWingStatus(), 0, deg, 4, 2);
        deg[6] = car.getEngineDeg();
        deg[7] = car.getFuelLoad();

        return deg;
    }

    /**
     * @return A lap time
     */
    public Time getLapTime() {
        if (problem) return new Time();
        return lapTime;
    }

    /**
     * Front Right, FL, RR, RL
     *
     * @return The tyres' status
     */
    public String[] getTyreStatus() {
        return car.getTyreStatus();
    }

    /**
     * @param type The type of tyre to be put on
     */
    public void manualPitStop(Tyre.Type type) {
        car.newTyres(type);
        if (car.getWingStatus()[0] > 0) car.newWing();
        pitStop = true;
        changedWing = true;
    }

    /**
     * Method simulating the part's degradation
     *
     * @param currentLap The lap the car is in
     * @return whether a problem occurred or not
     */
    public boolean simulateLap(int currentLap) {
        this.currentLap = currentLap;
        //Cannot drive if the car is kaput
        if (problem) return true;

        //Calculate the lap time
        pitStop = pitStopNeeded();
        if (pitStop) changedWing = makePitStop();
        calculateLapTime();

        //Degrade every part
        boolean tyreProblem = car.degradeTyres(calculateTyreDeg());
        boolean engineProblem = car.degradeEngine(calculateEngineDeg());
        boolean fuelProblem = car.loseFuel(calculateFuelLoss());
        car.degradeWings(calculateWingDeg());

        //Return whether a problem occurred
        return problem = tyreProblem || engineProblem || fuelProblem;
    }

    /**
     * Calculates how much the engine degraded
     *
     * @return double
     */
    private double calculateEngineDeg() {
        //An F1 engine has lifetime of about 3000 km
        // (Length * FullThrottle/100) to calculate how many meter were driven full throttle
        // (^ / 3.000.000) to calculate how much of the 3000 km lifespan was spent
        // (^ * 100) to get the percentage [the 100 is cancellable]
        double deg = (Track.getLength(track) * Track.getFullThrottle(track)) / 3000000.0;

        double multiplicator;
        //Speed: 1 = 50 %; 2 = 75 % %; 3 = 100 %; 4 = 150 % 5 = 200 %
        switch (speed) {
            case 1:
                multiplicator = 0.5;
                break;
            case 2:
                multiplicator = 0.75;
                break;
            case 4:
                multiplicator = 1.5;
                break;
            case 5:
                multiplicator = 2.0;
                break;
            default:
                multiplicator = 1.0;
                break;
        }

        deg *= multiplicator;

        return deg;
    }

    /**
     * Calculates how much fuel was burned
     *
     * @return double
     */
    private double calculateFuelLoss() {
        double fuelLoss = 96.0 / Track.getLaps(track);

        double multiplicator;
        switch (speed) {
            case 1:
                multiplicator = 0.8;
                break;
            case 2:
                multiplicator = 0.9;
                break;
            case 4:
                multiplicator = 1.1;
                break;
            case 5:
                multiplicator = 1.2;
                break;
            default:
                multiplicator = 1.0;
                break;
        }

        return fuelLoss * multiplicator;
    }

    /**
     * Calculates the lap time
     */
    private void calculateLapTime() {
        lapTime = new Time();

        lapTimeBase();
        lapTimePitStop();
        lapTimeWingDamage();
        lapTimeTyres();
        lapTimeFuel();
        lapTimeSpeed();
    }

    /**
     * Calculates how much the tyres degraded
     *
     * @return double[] FR, FL, RR, RL
     */
    private double[] calculateTyreDeg() {
        int base = 35;
        switch (car.getTyreType()) {
            case SOFT:
                base = 15;
                break;
            case HARD:
                base = 55;
                break;
        }

        double[] tyreDeg = new double[4];

        for (int i = 0; i < 4; i++) {
            double deg = base;
            // Clockwise, more right corners, more degradation front right
            if (Track.isClockwise(track) && i == 0 || !Track.isClockwise(track) && i == 1) deg += 2.5;
            // Less fullThrottle, more acceleration, more wheel spin, higher degradation
            if (i == 2 || i == 3) deg += 5 - 0.05 * Track.getFullThrottle(track);

            deg += Math.random() * 11 - 5;
            deg += (3 - speed) * 5;

            tyreDeg[i] = (Track.getLaps(track) * 1.0) / deg;
        }

        return tyreDeg;
    }

    /**
     * Calculates how much the wings degraded
     *
     * @return int[] front wing, rear wing
     */
    private double[] calculateWingDeg() {
        //Early laps and Monaco have higher chance of damage
        double frontWing = Math.random() * 100;

        if (track == Track.Name.MONACO) frontWing += 0.5;
        if (currentLap < 5) frontWing += 0.5;

        if (frontWing >= 99.9) frontWing = 100.0;
        else if (frontWing >= 99.75) frontWing = 75.0;
        else if (frontWing >= 99.5) frontWing = 50.0;
        else if (frontWing >= 99.0) frontWing = 25.0;
        else frontWing = 0.0;

        double rearWing = Math.random() * 100;

        if (rearWing >= 99.9) rearWing = 100.0;
        else if (rearWing >= 99.85) rearWing = 75.0;
        else if (rearWing >= 99.7) rearWing = 50.0;
        else if (rearWing >= 99.5) rearWing = 25.0;
        else rearWing = 0.0;

        return new double[]{frontWing, rearWing};
    }

    /**
     * Adds the fastest lap time as base
     */
    private void lapTimeBase() {
        switch (track) {
            case MONZA:
                lapTime.addTime(new Time("1:22,000"));
                break;
            case MONACO:
                lapTime.addTime(new Time("1:12,000"));
                break;
            default:
                lapTime.addTime(new Time("1:30,000"));
                break;
        }
    }

    /**
     * Adds time depending on the fuel level
     */
    private void lapTimeFuel() {
        lapTime.addMilliseconds((int) ((car.getFuelLoad() / 50) * 1000));
    }

    /**
     * Adds time if a pit stop was made
     */
    private void lapTimePitStop() {
        if (pitStop) {
            lapTime.addSeconds(20);
            lapTime.addMilliseconds((int) (Math.random() * 4000));
            if (changedWing) lapTime.addSeconds(5);
            pitStop = false;
            changedWing = false;
        }
    }

    /**
     * Adds time depending on the current speed setting
     */
    private void lapTimeSpeed() {
        switch (speed) {
            case 1:
                lapTime.addMilliseconds(1200);
                break;
            case 2:
                lapTime.addMilliseconds(800);
                break;
            case 3:
                lapTime.addMilliseconds(400);
                break;
            case 4:
                lapTime.addMilliseconds(200);
                break;
            case 5:
                lapTime.addMilliseconds(0);
                break;
        }
        lapTime.addMilliseconds((int) (Math.random() * 100));
    }

    /**
     * Adds time depending on the tyres' status
     */
    private void lapTimeTyres() {
        //inspiration from https://www.racedepartment.com/attachments/wear-grip-png.489787/)
        int length = Track.getLength(track);
        OptionalDouble temp = Arrays.stream(car.getTyreDeg()).average();
        double degAvg = temp.isEmpty() ? car.getTyreDeg()[0] : temp.getAsDouble();

        double typeAdd = 0;
        double degAdd = 0;

        switch (car.getTyreType()) {
            case SOFT:
                typeAdd = 0.0 * length;
                if (degAvg <= 63) degAdd = 2.0 / 63 * degAvg;
                else degAdd = 2.0 / 37 * (4 * degAvg - 215);
                break;
            case MEDIUM:
                typeAdd = 0.15 * length;
                if (degAvg <= 63) degAdd = 1.5 / 63 * degAvg;
                else degAdd = 0.5 / 37 * (8 * degAvg - 393);
                break;
            case HARD:
                typeAdd = 0.3 * length;
                if (degAvg <= 63) degAdd = 1.0 / 63 * degAvg;
                else degAdd = 1.0 / 37 * (2 * degAvg - 89);
                break;
        }

        lapTime.addMilliseconds((int) typeAdd);
        lapTime.addMilliseconds((int) (degAdd * 1000));
    }

    /**
     * Adds time depending on the wings' damage
     */
    private void lapTimeWingDamage() {
        int length = Track.getLength(track);
        double[] wingDmg = car.getWingStatus();

        lapTime.addMilliseconds((int) (wingDmg[0] / 100 * length));
        lapTime.addMilliseconds((int) (wingDmg[1] / 50 * length));
    }

    /**
     * Method to make a pit stop
     *
     * @return whether a new wing was needed
     */
    private boolean makePitStop() {
        double remainingPercent = 1.0 * currentLap / Track.getLaps(track);

        if (remainingPercent > 0.75) car.newTyres(Tyre.Type.SOFT);
        else if (remainingPercent > 0.55) car.newTyres(Tyre.Type.MEDIUM);
        else car.newTyres(Tyre.Type.HARD);

        if (car.getWingStatus()[0] > 0) {
            car.newWing();
            return true;
        }
        return false;
    }

    /**
     * @return Whether a pit stop is needed
     */
    private boolean pitStopNeeded() {
        // Final few laps; only pit if there's an emergency
        // Pit if the tyre is almost completely dead
        if (Arrays.stream(car.getTyreDeg()).anyMatch(x -> x > 95.0)) return true;
        // Pit if the wing is heavily damaged
        if (car.getWingStatus()[0] >= 75) return true;
        // Don't pit if there are only a few laps to go
        if (1.0 * currentLap / Track.getLaps(track) >= 0.9) return false;

        // Last laps of the race; only pit if the wing is too damaged or the soft tyre is too old
        // Pit if the wing is damaged
        if (car.getWingStatus()[0] >= 50) return true;
        // Pit if the soft tyre is too old
        if (car.getTyreType() == Tyre.Type.SOFT && Arrays.stream(car.getTyreDeg()).anyMatch(x -> x > 63.0)) return true;
        // Don't pit if there are only a few laps to go on the hard/medium
        if (1.0 * currentLap / Track.getLaps(track) >= 0.8) return false;

        // Last part of the race; Only pit if the mediums are too old to make the final stretch
        // Pit if the medium tyre is too old
        if (car.getTyreType() == Tyre.Type.MEDIUM && Arrays.stream(car.getTyreDeg()).anyMatch(x -> x > 63.0))
            return true;
        // Don't pit if there are only a few laps to go on the hard
        if (1.0 * currentLap / Track.getLaps(track) >= 0.7) return false;

        // Begin and middle part of the race; always pit if the wing is damaged or the tyres too old
        // Pit if wing is damaged
        if (car.getWingStatus()[0] >= 25) return true;
        // Pit if the hard tyre is too old
        return car.getTyreType() == Tyre.Type.HARD && Arrays.stream(car.getTyreDeg()).anyMatch(x -> x > 63.0);
    }
}
