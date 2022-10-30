package f1digitaltwin;

import f1digitaltwin.car.Tyre;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;

/**
 * The FX application's Controller
 */
public class FXController {

    private final int delay = 500;
    private final FXControllerDelegation delegator = new FXControllerDelegation(this);
    @FXML
    public Rectangle frontRight, frontLeft, rearRight, rearLeft;
    public Rectangle frontWing, rearWing, fuelTank, engine;
    public Label infoFW, infoRW, infoEngine, infoFuel;
    public Label infoType, infoAge, infoFR, infoFL, infoRR, infoRL;
    public Label lapLabel, totalTime;
    public LineChart<Number, Number> lineChart;
    public Button pitButton, startButton, pauseButton;
    public Label speedLabel, pitLabel, problemLabel;
    public Slider speedSlider;
    public Spinner<Integer> startLap, engineDeg, fuelLoad, tyreAge, tyreDeg;
    public ListView<String> timeList;
    public ChoiceBox<Track.Name> trackChoice;
    public ImageView trackView;
    public ChoiceBox<Tyre.Type> tyreType, pitTyreChoice;
    Controller controller;
    int currentLap = 0;
    int lapAmount;
    boolean problem = false;
    boolean started = false;
    Time timeTotal = new Time();

    /**
     * Method called at the very beginning
     */
    @FXML
    protected void initialize() {
        delegator.initialise();
        controller = new Controller(this);
    }

    /**
     * Method called when pause button is clicked
     */
    @FXML
    protected void onPauseButtonClick() {
        if (started) {
            pauseButton.setText("Unpause");
            started = false;
            return;
        }
        started = true;
        pauseButton.setText("Pause");
        controller.lap();
    }

    /**
     * Method called after the Pit button was clicked
     */
    @FXML
    protected void onPitButtonClick() {
        controller.manualPitStop(pitTyreChoice.getValue());
    }

    /**
     * Method called after a click on the start button
     */
    @FXML
    protected void onStartButtonClick() {
        started = true;

        lapAmount = Track.getLaps(trackChoice.getValue());
        timeTotal = new Time();

        delegator.initRaceFX();

        controller.init();
    }

    /**
     * Method called after another track is chosen
     */
    @FXML
    protected void onTrackChoiceChange() {
        delegator.setImageView();
    }

    void showGraph() {
        delegator.showGraph();
    }

    /**
     * Method used to make the programme wait for JavaFX to update.
     * <p>
     * Calls the updateFX function and
     * then sleeps for the specified time.
     * After finishing the FX tasks, the lap method is called.
     */
    void update(int currentLap) {
        this.currentLap = currentLap;

        Task<Void> task = new Task<>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(delay);
                Platform.runLater(() -> updateFX());

                return null;
            }

        };

        task.setOnSucceeded(workerStateEvent -> controller.lap());

        new Thread(task).start();
    }

    /**
     * Updates all the JavaFX objects
     */
    @FXML
    void updateFX() {
        delegator.switchButtonsBack();
        delegator.updateLabels();
        delegator.updateTimes(controller.getLapTime());
        double[] deg = controller.getDegradation();
        delegator.colourParts(deg);
        delegator.updateInfo(deg);
    }
}