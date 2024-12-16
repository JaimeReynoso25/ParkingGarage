package application;


import java.sql.Connection;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import loginpkg.LoginController;
import sqliterepo.DBConnection;


/**
 * main class for launching the parking garage application.
 * initializes the database connection, sets up the primary stage,
 * loads the login screen as the starting point of the application.
 */

public class Main extends Application {

	private Connection connection;
	private static Stage primaryStage;
	
	
	
	
	 /**
     * starts the apoplication
     *
     * @param stage the primary stage for the application
     * @throws exception if there is initiliazing error
     */
	 
    @Override 
    public void start(Stage stage) throws Exception {
    	connection = getConnection();
    	this.primaryStage = stage;
    	
    	// Load the FXML file
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/loginpkg/login.fxml"));        
    	
        // Get the Login controller and pass the connection
        LoginController loginController = new LoginController(connection);
        loader.setController(loginController);
        
        Parent root = loader.load();
        
        System.out.println(connection == null ? "Connection is NULL." : "Connection is NOT NULL.");

        primaryStage.setTitle("UIS Parking Garage Login");
        primaryStage.setScene(new Scene(root, 620, 500));
        primaryStage.show();
        
    }
    
    /**
     * establishes a connection to the database.
     *
     * @return the database connection object
     */
    
    private Connection getConnection() {
        try {
        	
        	DBConnection dbConnection = new DBConnection();
        	connection = dbConnection.getConnection();
        	
        } catch (SQLException | ClassNotFoundException e) {
                System.out.println("Exception: " + e.getMessage());
        }
        return connection;
    }
    
    /**
     * gets primary stage of the application.
     *
     * @return primary stage
     */
    
    public static Stage getPrimaryStage() {
    	
        return primaryStage;
    }
 
    //launches the JavaFX application
    public static void main(String[] args) {
    	
        launch(args);  
    }
}
