module verdevista.wms.project {
    exports se.lu.ics;

    opens se.lu.ics.controllers to javafx.fxml;
    opens se.lu.ics.models to javafx.base;

    requires java.sql;
    requires javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires java.desktop;
}