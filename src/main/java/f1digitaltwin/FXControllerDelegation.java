package f1digitaltwin;

import f1digitaltwin.car.Track;
import f1digitaltwin.car.Tyre;
import javafx.fxml.FXML;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;

import static javafx.scene.paint.Color.*;

/**
 * Method to handle all JavaFX related methods
 */
public class FXControllerDelegation {

    private final FXController c;

    public FXControllerDelegation(FXController FXController) {
        c = FXController;
    }

    /**
     * Helper method to colour the parts according to the part's degradation
     *
     * @param deg Array with the degradation of all parts
     */
    @FXML
    void colourParts(double[] deg) {
        c.frontRight.setFill(degToColour(deg[0]));
        c.frontLeft.setFill(degToColour(deg[1]));
        c.rearRight.setFill(degToColour(deg[2]));
        c.rearLeft.setFill(degToColour(deg[3]));

        c.frontWing.setFill(degToColour(deg[4]));
        c.rearWing.setFill(degToColour(deg[5]));
        c.engine.setFill(degToColour(deg[6]));

        c.fuelTank.setFill(degToColour(100 - deg[7]));
    }

    /**
     * Helper method to prepare all race related things
     */
    @FXML
    void initRaceFX() {
        c.problem = false;

        c.startButton.setVisible(false);
        c.pauseButton.setVisible(true);
        c.problemLabel.setVisible(false);
        c.timeList.getItems().clear();
        c.lineChart.setVisible(false);
        c.trackView.setVisible(true);

        c.lapLabel.setVisible(true);
        c.totalTime.setVisible(true);
        c.pitButton.setVisible(true);
        c.speedLabel.setVisible(true);
        c.pitLabel.setVisible(true);

        c.pitTyreChoice.setVisible(true);
        c.pitTyreChoice.getItems().addAll(Tyre.Type.values());
        c.pitTyreChoice.setValue(Tyre.Type.MEDIUM);

        c.speedSlider.setVisible(true);
        c.speedSlider.setValue(3);
        c.speedSlider.valueProperty().addListener((observableValue, number, t1) -> onSpeedSliderChange());
    }

    /**
     * Helper method to initialise the FX objects
     */
    @FXML
    void initialise() {
        c.trackChoice.getItems().addAll(Track.Name.values());
        c.trackChoice.setValue(Track.Name.MONZA);
        setImageView();
        setSpinners();
        setTyreTypeChoices();
    }

    /**
     * Helper method to set up the Image View
     */
    @FXML
    void setImageView() {
        Path image = Paths.get(
                String.format("src/main/resources/f1digitaltwin/%s.png", c.trackChoice.getValue()));
        try {
            c.trackView.setImage(new Image(image.toUri().toURL().toExternalForm()));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Helper method to initialise the spinners
     */
    @FXML
    void setSpinners() {
        c.engineDeg.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 0));
        c.tyreAge.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 1));
        c.tyreDeg.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 100, 2));
        c.startLap.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, Track.getLaps(Track.Name.MONZA), 0));
        c.fuelLoad.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 110, 105));
    }

    /**
     * Helper method to update the TyreType ChoiceBox
     */
    @FXML
    void setTyreTypeChoices() {
        c.tyreType.getItems().addAll(Tyre.Type.values());
        c.tyreType.setValue(Tyre.Type.MEDIUM);
    }

    /**
     * Method to show a graph after the race
     */
    void showGraph() {
        NumberAxis xAxis = (NumberAxis) c.lineChart.getXAxis();
        NumberAxis yAxis = (NumberAxis) c.lineChart.getYAxis();

        xAxis.setLabel("Laps");
        yAxis.setAutoRanging(false);
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Race");

        //Reverses the timeList
        LinkedList<String> list = new LinkedList<>();
        for (String s : c.timeList.getItems()) list.addFirst(s);

        // Lap XX: XX:XX,XXX
        int firstLap = Integer.parseInt((list.getFirst().split(" ")[1].replace(":", "")));
        int lastLap = Integer.parseInt((list.getLast().split(" ")[1].replace(":", "")));

        int i = firstLap;
        double d;
        String timeString;
        Time time;
        double min = 300;
        double max = 0;

        //Adds the time in seconds to the data
        for (String s : list) {
            d = 0;
            timeString = s.split(": ")[1];
            time = new Time(timeString);
            d += time.getMinutes() * 60;
            d += time.getSeconds();
            d += time.getMilliseconds() / 1000.0;

            if (d < min) min = d;
            if (d > max) max = d;

            series.getData().add(new XYChart.Data<>(i++, d));
        }

        c.lineChart.setCreateSymbols(false);

        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(firstLap);
        xAxis.setUpperBound(lastLap);

        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(min - 1);
        yAxis.setUpperBound(max + 1);

        c.lineChart.getData().add(series);

        c.trackView.setVisible(false);
        c.lineChart.setVisible(true);
    }

    /**
     * Helper method to make the start button visible again
     * and hide the pause button
     */
    @FXML
    void switchButtonsBack() {
        if (c.currentLap == c.lapAmount || c.problem) {
            c.pauseButton.setVisible(false);
            c.startButton.setVisible(true);
        }
    }

    /**
     * Helper method to update the info part
     *
     * @param deg Array with the degradation of all parts
     */
    @FXML
    void updateInfo(double[] deg) {
        String[] tyreStatus = c.controller.getTyreStatus();

        for (int i = 0; i < 8; i++) deg[i] = Math.round(deg[i] * 100) / 100.0;

        c.infoType.setText("Type\n" + tyreStatus[0]);
        c.infoAge.setText("Age\n" + tyreStatus[1]);

        c.infoFR.setText("Front Right\n" + deg[0]);
        c.infoFL.setText("Front Left\n" + deg[1]);
        c.infoRR.setText("Rear Right\n" + deg[2]);
        c.infoRL.setText("Rear Left\n" + deg[3]);

        c.infoFW.setText("Front Wing\n" + deg[4]);
        c.infoRW.setText("Rear Wing\n" + deg[5]);
        c.infoEngine.setText("Engine\n" + deg[6]);
        c.infoFuel.setText("Fuel\n" + deg[7]);
    }

    /**
     * Helper method to set the lap label
     */
    @FXML
    void updateLabels() {
        c.lapLabel.setText("Lap " + c.currentLap + " of " + c.lapAmount);
        c.problemLabel.setVisible(c.problem);
    }

    /**
     * Helper method to update the total time label
     */
    void updateTimes(Time lapTime) {
        if (lapTime.isZero()) return;
        c.timeTotal.addTime(lapTime);
        c.totalTime.setText("Total time:\n" + c.timeTotal);

        c.timeList.getItems().add(0, "Lap " + c.currentLap + ": " + lapTime);
    }

    /**
     * Helper method to colour the parts
     *
     * @param deg The part's degradation
     * @return A colour representing the degradation
     */
    private Color degToColour(double deg) {
        if (deg <= 10) return DARKGREEN;
        else if (deg <= 20) return GREEN;
        else if (deg <= 30) return LIGHTGREEN;
        else if (deg <= 40) return GREENYELLOW;
        else if (deg <= 50) return YELLOW;
        else if (deg <= 60) return ORANGE;
        else if (deg <= 70) return DARKORANGE;
        else if (deg <= 80) return ORANGERED;
        else if (deg <= 90) return RED;
        else if (deg <= 100) return DARKRED;
        else return BLACK;
    }

    /**
     * Method called when the slider is being slided
     */
    @FXML
    private void onSpeedSliderChange() {
        c.controller.changeSpeed((int) c.speedSlider.getValue());
    }
}
