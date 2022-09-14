module f1digitaltwin {
    opens f1digitaltwin to javafx.fxml;

    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires org.eclipse.paho.client.mqttv3;

    exports f1digitaltwin;
    exports f1digitaltwin.car;
}