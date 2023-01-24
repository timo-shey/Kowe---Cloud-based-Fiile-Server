module com.mycompany.coursework {
//    requires javafx.controls;
//    requires javafx.fxml;
//    requires javafx.graphics;
//    requires java.base;
//    requires java.sql;
//    requires junit; // added
//
//    opens com.mycompany.coursework to javafx.fxml;
//    exports com.mycompany.coursework;
    
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;
    requires java.base;
    requires java.sql; // added

    opens com.mycompany.coursework to javafx.fxml;
    exports com.mycompany.coursework;
}
