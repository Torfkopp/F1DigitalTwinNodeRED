package f1digitaltwin;

import f1digitaltwin.car.Car;
import f1digitaltwin.car.Track;
import f1digitaltwin.car.Tyre;

import java.util.Arrays;

/**
 * Class controlling the flow between the
 * Simulation, the FXController, and the NodeREDCommunication
 */
public class Controller {

    private static final boolean simulationOn = true;
    private final FXController fxc;
    private final NodeREDCommunication nrc;
    private final String split = "|";
    private Car car;
    private int currentLap;
    private Time lapTime = new Time();
    private Simulation simulation;
    private Track.Name trackName;

    /**
     * Constructor
     *
     * @param fxc The FXController
     */
    public Controller(FXController fxc) {
        this.fxc = fxc;
        nrc = new NodeREDCommunication(this);

    }

    /**
     * Forwards a changed speed the simulation
     *
     * @param value The new speed
     */
    void changeSpeed(int value) {
        if (simulationOn) simulation.changeSpeed(value);
    }

    /**
     * 0 Front Left, 1 Front Right, 2 Rear Left, 3 Rear Right
     * 4 Front Wing, 5 Rear Wing, 6 Engine, 7 Fuel
     *
     * @return Array of integer representing the part's degradation
     */
    double[] getDegradation() {
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
    Time getLapTime() {
        return lapTime;
    }

    /**
     * @return Car's tyres' status
     */
    String[] getTyreStatus() {
        return car.getTyreStatus();
    }

    /**
     * Initialises the important values and sends an initialMessage
     */
    void init() {
        trackName = fxc.trackChoice.getValue();
        currentLap = fxc.startLap.getValue();
        car = new Car(
                fxc.engineDeg.getValue(),
                fxc.fuelLoad.getValue(),
                fxc.tyreType.getValue(),
                fxc.tyreAge.getValue(),
                fxc.tyreDeg.getValue()
        );

        if (simulationOn) simulation = new Simulation(trackName, car);
        sendInitialMessage();
    }

    /**
     * Method to simulate a lap
     */
    void lap() {
        if (currentLap == Track.getLaps(trackName)) fxc.showGraph();
        if (currentLap >= Track.getLaps(trackName) || !fxc.started || !simulationOn) return;

        sendSimulatedValues(simulation.simulateLap(currentLap));
    }

    /**
     * Method to handle a manuel pit stop
     *
     * @param type The type of compound to change to
     */
    void manualPitStop(Tyre.Type type) {
        if (simulationOn) simulation.manualPitStop(type);
    }

    /**
     * Method to handle a received message
     *
     * @param message received
     */
    void messageReceived(String message) {
        String[] s = message.split("\\|");

        currentLap = Integer.parseInt(s[0]);
        lapTime = new Time(s[1]);

        car.setEngineDeg(Double.parseDouble(s[2]));
        car.setFuelAmount(Double.parseDouble(s[3]));
        car.setWingCondition(Double.parseDouble(s[4]), Double.parseDouble(s[5]));
        car.setTyreStatus();
        car.setTyreDegradation(Double.parseDouble(s[8]), Double.parseDouble(s[9]), Double.parseDouble(s[10]), Double.parseDouble(s[11]));

        if (!s[12].equals("noStop")) {
            car.newTyres(Tyre.Type.valueOf(s[12]));
            if (Boolean.parseBoolean(s[13])) car.newWing();
        }

        //lap(); is getting called when the fxUpdate is finished
        fxc.update(currentLap);
    }

    /**
     * Sends the initial message
     */
    void sendInitialMessage() {
        String s = "";

        s += currentLap + split;
        s += lapTime + split;
        s += car.getEngineDeg() + split;
        s += car.getFuelLoad() + split;
        s += Arrays.toString(car.getWingStatus()).replaceAll("^.|.$", "").replaceAll(", ", split) + split;
        s += Arrays.toString(car.getTyreStatus()).replaceAll("^.|.$", "").replaceAll(", ", split) + split;
        s += Arrays.toString(car.getTyreDeg()).replaceAll("^.|.$", "").replaceAll(", ", split) + split;
        s += "noStop" + split + "false";

        nrc.send("initial", s);
    }

    /**
     * Sends the simulated values
     *
     * @param sim Array of all the simulated values
     */
    private void sendSimulatedValues(Object[] sim) {
        String s = "";

        double[] deg = (double[]) sim[1];

        double engineDeg = car.getEngineDeg() + deg[0];
        double fuelLoad = car.getFuelLoad() - deg[1];
        double frontWing = car.getWingStatus()[0] + deg[2];
        double rearWing = car.getWingStatus()[1] + deg[3];

        double[] tyres = car.getTyreDeg();
        double frontRight = tyres[0] + deg[4];
        double frontLeft = tyres[1] + deg[5];
        double rearRight = tyres[2] + deg[6];
        double rearLeft = tyres[3] + deg[7];

        //Build String
        s += (currentLap + 1) + split; //Lap
        s += sim[0] + split; //LapTime

        s += engineDeg + split; //engine deg
        s += fuelLoad + split; //fuel load
        s += frontWing + split; //front wing deg
        s += rearWing + split; //rear wing deg

        //Type and Age of the tyres
        s += Arrays.toString(car.getTyreStatus()).replaceAll("^.|.$", "").replaceAll(", ", split) + split;

        s += frontRight + split; //front right tyre deg
        s += frontLeft + split; //front left tyre deg
        s += rearRight + split; //rear right tyre deg
        s += rearLeft + split; //rear left tyre deg

        //the new tyre compound or "noStop" and if a new wing is needed
        s += (Arrays.toString((String[]) sim[2])).replaceAll("^.|.$", "").replaceAll(", ", split);

        nrc.send("simulation", s);
    }
}
