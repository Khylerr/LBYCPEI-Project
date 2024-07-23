module com.example.login_test {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;

    opens com.example.login_test to javafx.fxml;
    exports com.example.login_test;
}