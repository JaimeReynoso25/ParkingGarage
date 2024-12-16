
package application;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import java.sql.Connection;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import sqliterepo.DBConnection;

public class MainTest {

    private static Connection connection;

    @BeforeAll
    static void setup() {
    	
//    	JavaFXTestUtils.initializeToolkit();
    	
        try {
//        	java toolkit for java application thread
            javafx.application.Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // exception for initialized toolkit
            System.out.println("JavaFX Toolkit already initialized.");
        }
    	
        // initialize the database connection before tests
        try {
            DBConnection dbConnection = new DBConnection();
            connection = dbConnection.getConnection();
        } catch (Exception e) {
            fail("Setup failed: Unable to establish database connection. Error: " + e.getMessage());
        }
    }
//TC001
    @Test
    void testGetConnectionSuccess() {
        // assert connection
        assertNotNull(connection, "Connection should not be null.");
        try {
            assertFalse(connection.isClosed(), "Connection should be open.");
        } catch (Exception e) {
            fail("Exception while checking connection status: " + e.getMessage());
        }
    }
//TC002
    @Test
    void testGetConnectionWithReflection() {
        try {
            Main mainApp = new Main();
            // use reflection for getConnection method 
            java.lang.reflect.Method method = Main.class.getDeclaredMethod("getConnection");
            method.setAccessible(true);
            Connection connection = (Connection) method.invoke(mainApp);
            //assertions for connections
            assertNotNull(connection, "Connection should not be null.");
            assertFalse(connection.isClosed(), "Connection should be open.");
            System.out.println("connection est");
        } catch (Exception e) {
            fail("Reflection test failed: " + e.getMessage());
        }
    }

//TC003

    @Test
    void testFXMLLoading() {
        assertDoesNotThrow(() -> {
            javafx.application.Platform.runLater(() -> {
                try {
                    Main mainApp = new Main();
                    mainApp.start(new javafx.stage.Stage());
                } catch (Exception e) {
//                	thorw a new expception
                    throw new RuntimeException(e); 
                }
            });
        }, "FXML loading should not throw exceptions.");
        System.out.println("fxml loaded");
    }

//TC004    
    @AfterAll
    static void teardown() {
        // close connection after tests
        if (connection != null) {
            try {
                connection.close();
                System.out.println("closed connection");
            } catch (Exception e) {
                System.err.println("Failed to close connection: " + e.getMessage());
            }
        }
    }
}
