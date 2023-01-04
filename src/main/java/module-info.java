module com.example.demofx {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.demofx to javafx.fxml;
    exports com.example.demofx;
    exports com.example.demofx.functionality;
    opens com.example.demofx.functionality to javafx.fxml;
    exports com.example.demofx.objects;
    opens com.example.demofx.objects to javafx.fxml;
}