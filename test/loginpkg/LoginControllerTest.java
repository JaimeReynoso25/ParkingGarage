package loginpkg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.event.ActionEvent;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sqliterepo.SQLRepository;
import toolkit.JavaFXTestUtils;


class LoginControllerTest {
	
    //Global variables 
	private LoginController controller;	
    private ActionEvent mockEvent;
    private Method handleLoginButtonMethod;
    private SQLRepository testRepo;
    private Connection connection;
    private LocalDate localDate = LocalDate.now();
    private Statement statement;
	
    @BeforeEach
    public void setup() throws NoSuchMethodException, SecurityException, SQLException {
        // java toolkit for java application thread
        JavaFXTestUtils.initializeToolkit();

        //get test database connection
        connection = DriverManager.getConnection("jdbc:sqlite:garage_system_test.db");
	    testRepo = new SQLRepository(connection);
	    
	    // Clear parking spots 1-20 in garage table
	 	emptyGarageTable();
        
        // Initialize the mock event
        mockEvent = new ActionEvent();
        
        statement = connection.createStatement();
        
        //reflection method once to reuse
        handleLoginButtonMethod = LoginController.class.getDeclaredMethod("handleLoginButton", ActionEvent.class);
        handleLoginButtonMethod.setAccessible(true);

        // set up the controller
        controller = new LoginController(connection);
        injectComponents();
    }
    
    private void injectComponents() {
        // set components
        controller.setPasswordCheckBox(new CheckBox());  
        controller.setPasswordField(new PasswordField());
        controller.setPasswordTextField(new TextField());
        controller.setEmailField(new TextField());
        controller.setStatusLabel(new Label());      
    }
    
    @AfterEach
    public void closeConnection() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
	
    // method to check if the email and password fields are empty
    private void assertStatusMessageForLogin(String email, String password, String expectedMessage) throws Exception {
        controller.getEmailField().setText(email);
        controller.getPasswordField().setText(password);
        handleLoginButtonMethod.invoke(controller, mockEvent);
        assertEquals(expectedMessage, controller.getStatusLabel().getText());
    }
        
    //method to populate test reservation table
    private void populateGarage() throws SQLException {
    	String startDate = localDate.toString(); 
        String endDate = localDate.plusDays(3).toString(); 
        
        // clears the reservation table
     	statement.execute("DELETE FROM reservations");
        
        // add reservations for testing
        statement.execute("INSERT INTO reservations (email, license_plate, start_date, end_date, alreadyParked) " +
                          "VALUES ('tempuser1@test.com', 'temp1' , '" + startDate + "', '" + endDate + "', 0)");
        statement.execute("INSERT INTO reservations (email, license_plate, start_date, end_date, alreadyParked) " +
                          "VALUES ('tempuser2@test.com', 'temp2' , '" + startDate + "', '" + endDate + "', 0)");
    }
    
    //method to clear the test garage table
    private void emptyGarageTable() throws SQLException {
    	String clearSpotsQuery = "UPDATE garage SET occupied = 0, email = NULL, license_plate = NULL WHERE parkingSpot BETWEEN 1 AND 20";
	    PreparedStatement clearSpotsStmt = connection.prepareStatement(clearSpotsQuery);
	    clearSpotsStmt.executeUpdate();
    }

//TC001    
	@Test
	public void handleLoginButtonTest_CheckForEmptyFields() throws Exception {
		//insert empty email and password.
		assertStatusMessageForLogin("", "", "Please enter both fields");
	}
//TC002
	@Test
	public void handleLoginButtonTest_OnlyEmptyEmail() throws Exception {
		// insert empty email
		assertStatusMessageForLogin("", "test", "Please enter both fields");
	}
//TC003 
	@Test
	public void handleLoginButtonTest_OnlyEmptyPassword() throws Exception {
		// insert empty password
		assertStatusMessageForLogin("test@test.com", "", "Please enter both fields");
	}
//TC004	
	@Test
	public void handleLoginButtonTest_FailedAuthentication() throws Exception {
		// testing with a wrong user account
		assertFalse(testRepo.authenticateUser("wrong@test.com", "test"));
	}
//TC005
	@Test
	public void handleLoginButtonTest_SuccessfulAuthentication() throws Exception {
		// testing with a known valid user account
		 assertTrue(testRepo.authenticateUser("test@test.com", "test"));
	}
//TC006
	@Test
	public void updateGarageTest_addReservation() throws SQLException {
        
		//fill reservation table with test data
        populateGarage();
        
        //update test garage with new reservations
        testRepo.updateGarageTable();
        
        // Check if parking spots 1 and 2 have been filled
        ResultSet resultSet = statement.executeQuery("SELECT * FROM garage WHERE parkingSpot = 1");
        assertTrue(resultSet.next() && "temp1".equals(resultSet.getString("license_plate")));
        
        ResultSet resultSet2 = statement.executeQuery("SELECT * FROM garage WHERE parkingSpot = 2");
        assertTrue(resultSet2.next() && "temp2".equals(resultSet2.getString("license_plate")));        
	}
//TC007
	@Test
	public void updateGarageTest_removeReservation() throws SQLException {
		// newEndDate should be a past date, so that it gets deleted from the reservation and garage tables
		String newEndDate = localDate.minusDays(1).toString();
		String licensePlate = "temp2";
		        
		//fill reservation table with test data
        populateGarage();
        
        //update test garage with new reservations
        testRepo.updateGarageTable();
		
        // Set the new end_date for temp1 license_plate		
		String updateQuery = "UPDATE reservations SET end_date = ? WHERE license_plate = ?";
        try (PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {

            preparedStatement.setString(1, newEndDate);
            preparedStatement.setString(2, licensePlate);

            preparedStatement.executeUpdate();
        }
        
        //update test garage with new reservation endDate, making sure it expired
        testRepo.updateGarageTable();
		
        // Check if parking spots 2 has been set to occupied == 0 (not occupied)
        ResultSet resultSet = statement.executeQuery("SELECT * FROM garage WHERE parkingSpot = 2");
        assertTrue(resultSet.next() && resultSet.getInt("occupied") == 0);
        
        // check if parking spot 1 is still occupied
        ResultSet resultSet2 = statement.executeQuery("SELECT * FROM garage WHERE parkingSpot = 1");
        assertTrue(resultSet2.next() && "temp1".equals(resultSet2.getString("license_plate")));   

        // check if temp2 license plate has been removed from reservation table
        String checkReservationDeletion = "SELECT * FROM reservations WHERE license_plate = ?";
        PreparedStatement checkReservationStmt = connection.prepareStatement(checkReservationDeletion);
        checkReservationStmt.setString(1, "temp2");
        ResultSet checkResult = checkReservationStmt.executeQuery();
        
        // clears the reservation table
     	statement.execute("DELETE FROM reservations");

        // Assert that no result is returned, meaning the temp2 license plate is no longer in the reservations table
        assertFalse(checkResult.next());
	}
}
