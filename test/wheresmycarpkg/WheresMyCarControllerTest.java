package wheresmycarpkg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import objects.CurrentUser;
import sqliterepo.SQLRepository;
import toolkit.JavaFXTestUtils;

class WheresMyCarControllerTest {

	private WheresMyCarController controller;
    private CurrentUser currentUser;
    private Connection connection;
    private SQLRepository testRepo;
    private Method handleFindMyCarMethod;
    private ActionEvent mockEvent;

    @BeforeEach
    public void setup() throws NoSuchMethodException, SecurityException, SQLException, ClassNotFoundException {
        // java toolkit for java application thread
        JavaFXTestUtils.initializeToolkit();
        
        // setup test database connection
        connection = DriverManager.getConnection("jdbc:sqlite:garage_system_test.db");
        testRepo = new SQLRepository(connection);
        
        // set the user details
        currentUser = testRepo.setUserDetails("test@test.com");
        
        ActionEvent mockEvent = new ActionEvent();
        
        // setup the methods
        handleFindMyCarMethod = WheresMyCarController.class.getDeclaredMethod("handleFindMyCar", ActionEvent.class);
        handleFindMyCarMethod.setAccessible(true);

        // set the controller with user and database connection
        controller = new WheresMyCarController(connection);

        injectComponents();
    }

    private void injectComponents() {
//        setup the components
        controller.setLocationText(new Label());
        controller.setLicensePlate(new TextField());
    }
    
    @AfterEach
    public void closeConnection() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
    
    @Test
    public void testHandleFindMyCar_LicensePlateFound() throws SQLException, IllegalAccessException, InvocationTargetException {
    	String testPlate = "testPlate";
    	
    	//adds a car into our garage with licese_plate = 'testPlate'
    	Statement statement = connection.createStatement();
    	statement.execute("UPDATE garage SET occupied = 1, email = 'test@test.com', license_plate = '" + 
    					   testPlate + "' WHERE parkingSpot = 5");
    	
    	// sets the testPlate string into the licensePlate field to search it up
    	controller.getLicensePlate().setText(testPlate);
    	
    	// calls the method
    	handleFindMyCarMethod.invoke(controller, mockEvent);
    	
    	// verifies that a user can search up their car in the database
    	assertEquals("Your car is located in spot: #5", controller.getLocationText().getText());
    	
    	// deletes the car from the garage table, to ensure it stays empty
    	statement.execute("UPDATE garage SET occupied = 0, email = 'NULL', license_plate = 'NULL' WHERE parkingSpot = 5");
    }
    
    @Test
    public void testHandleFindMyCar_LicensePlateNotFound() throws SQLException, IllegalAccessException, InvocationTargetException {
    	String invalidPlate = "invalidPlate";
    	
    	// sets the testPlate string into the licensePlate field to search it up
    	controller.getLicensePlate().setText(invalidPlate);
    	
    	// calls the method
    	handleFindMyCarMethod.invoke(controller, mockEvent);
    	
    	// verifies that a license plate is not in the garage
    	assertEquals("License plate not in garage", controller.getLocationText().getText());
    }

}
