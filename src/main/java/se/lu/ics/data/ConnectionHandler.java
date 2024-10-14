package se.lu.ics.data;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import se.lu.ics.controllers.MainController;

public class ConnectionHandler {
    private static String connectionURL;
    private static String configFilePath = "src/main/resources/config/config.properties";
    static {
        Properties connectionProperties = new Properties();
        try {
            FileInputStream stream = new FileInputStream(configFilePath);
            connectionProperties.load(stream);
            stream.close();
        } catch (IOException e) {
            MainController.credentialsErrorTerminateApp();
        }
        String databaseServerName = (String) connectionProperties.get("database.server.name");
        String databaseServerPort = (String) connectionProperties.get("database.server.port");
        String databaseName = (String) connectionProperties.get("database.name");
        String databaseUserName = (String) connectionProperties.get("database.user.name");
        String databaseUserPassword = (String) connectionProperties.get("database.user.password");
        connectionURL = "jdbc:sqlserver://"
                + databaseServerName + ":"
                + databaseServerPort + ";"
                + "database=" + databaseName + ";"
                + "user=" + databaseUserName + ";"
                + "password=" + databaseUserPassword + ";"
                + "encrypt=true;" // Required for JDBC driver v10.2
                + "trustServerCertificate=true;"; // Required for JDBC driver v10.2
    } 

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(connectionURL);
    } 
}