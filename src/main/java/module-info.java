module com.example.calcu {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.calcu to javafx.fxml;
    exports com.example.calcu;
}