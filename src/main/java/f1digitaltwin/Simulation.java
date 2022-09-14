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
    private boolean pitStop = false;
    private int speed = 3;

    /**
     * Constructor
     *
     * @param trackName The track's name
     * @param car       A car object to base the simulation on
     */
    public Simulation(Track.Name trackName, Car car) {
        track = trackName;
        //car = new Car(engineDeg, fuelAmount, type, tyreAge, tyreDeg);
        this.car = car;
    }

    /**
     * @param speed to change to
     */
    void changeSpeed(int speed) {
        this.speed = speed;
    }

    /**
     * Method to choose the new compound
     *
     * @return String[] of the new compound and if a new wing is needed
     */
    String[] makePitStop() {
        //TODO wip
        Tyre.Type type;
        boolean newWing;

        double remainingPercent = 1.0 * currentLap / Track.getLaps(track);

        if (remainingPercent > 0.75) type = Tyre.Type.SOFT;
        else if (remainingPercent > 0.55) type = Tyre.Type.MEDIUM;
        else type = Tyre.Type.HARD;

        newWing = car.getWingStatus()[0] > 0;
        return new String[]{String.valueOf(type), String.valueOf(newWing)};
    }

    /**
     * Changes the variables so that the simulation knows a pit stop was made
     *
     * @return If the wings needs to be changed
     */
    boolean manualPitStop() {
        //TODO wip
        pitStop = true;
        changedWing = car.getWingStatus()[0] > 0;
        return changedWing;
    }

    /**
     * Method simulating the part's degradation
     *
     * @param currentLap The lap the car is in
     * @return An Array of the lapTime, a double[] of the degradations, and a string[]
     */
    Object[] simulateLap(int currentLap) {
        Object[] obj = new Object[3];

        //Cannot drive if the car is kaput
        if (car.hasProblem()) return null;

        this.currentLap = currentLap;

        //Calculate the lap time
        obj[0] = calculateLapTime();

        //Calculate Degradation of every part
        double[] newValues = new double[8];
        newValues[0] = calculateEngineDeg();
        newValues[1] = calculateFuelLoss();
        double[] wingDeg = calculateWingDeg();
        newValues[2] = wingDeg[0];
        newValues[3] = wingDeg[1];

        double[] tyreDeg = calculateTyreDeg();
        newValues[4] = tyreDeg[0];
        newValues[5] = tyreDeg[1];
        newValues[6] = tyreDeg[2];
        newValues[7] = tyreDeg[3];

        obj[1] = newValues;

        //Calculate if a pit stop is needed and if yes, what to change
        obj[2] = null;
        if (pitStopNeeded()) obj[2] = makePitStop();

        //Return the string
        return obj;
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
    private Time calculateLapTime() {
        Time time = lapTimeBase();

        time.addMilliseconds(lapTimePitStop());
        time.addMilliseconds(lapTimeWingDamage());
        time.addMilliseconds(lapTimeTyres());
        time.addMilliseconds(lapTimeFuel());
        time.addMilliseconds(lapTimeSpeed());

        return time;
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
    private Time lapTimeBase() {
        switch (track) {
            case MONZA:
                return new Time("1:22,000");
            case MONACO:
                return new Time("1:12,000");
            default:
                return new Time("1:30,000");
        }
    }

    /**
     * Adds time depending on the fuel level
     */
    private int lapTimeFuel() {
        return (int) ((car.getFuelLoad() / 50) * 1000);
    }

    /**
     * Adds time if a pit stop was made
     */
    private int lapTimePitStop() {
        int time = 0;
        if (pitStop) {
            time += 20000;
            time += (int) (Math.random() * 4000);
            if (changedWing) time += 5000;
            pitStop = false;
            changedWing = false;
        }
        return time;
    }

    /**
     * Adds time depending on the current speed setting
     */
    private int lapTimeSpeed() {
        int time = (int) (Math.random() * 100);
        switch (speed) {
            case 1:
                time += 1200;
                break;
            case 2:
                time += 800;
                break;
            case 3:
                time += 400;
                break;
            case 4:
                time += 200;
                break;
            case 5:
                break;
        }
        return time;
    }

    /**
     * Adds time depending on the tyres' status
     */
    private int lapTimeTyres() {
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

        return (int) typeAdd + (int) (degAdd * 1000);
    }

    /**
     * Adds time depending on the wings' damage
     */
    private int lapTimeWingDamage() {
        int length = Track.getLength(track);
        double[] wingDmg = car.getWingStatus();

        return (int) (wingDmg[0] / 100 * length) + (int) (wingDmg[1] / 50 * length);
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
