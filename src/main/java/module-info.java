module com.example.f1digitaltwin {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;

    opens f1digitaltwin to javafx.fxml;
    exports f1digitaltwin;
    exports f1digitaltwin.car;
}