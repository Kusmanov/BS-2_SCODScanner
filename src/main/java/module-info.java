module com.example.demofx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.scodscanner to javafx.fxml;
    exports com.example.scodscanner;
    exports com.example.scodscanner.functionality;
    opens com.example.scodscanner.functionality to javafx.fxml;
    exports com.example.scodscanner.objects;
    opens com.example.scodscanner.objects to javafx.fxml;
}