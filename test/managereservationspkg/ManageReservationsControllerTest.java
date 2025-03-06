package managereservationspkg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import addfundspkg.AddFundsController;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import objects.CurrentUser;
import objects.Reservation;
import sqliterepo.SQLRepository;
import toolkit.JavaFXTestUtils;

public class ManageReservationsControllerTest {

	private ManageReservationsController controller;
	private CurrentUser currentUser;
	private Method handleDeleteButtonMethod;
	private Connection connection;
	private SQLRepository testRepo;
	private ActionEvent mockEvent;
	private LocalDate localDate = LocalDate.now();
	private Statement statement;
	
	@BeforeEach
	public void setup() throws NoSuchMethodException, SecurityException, SQLException, ClassNotFoundException {
		// java toolkit for java application thread
        JavaFXTestUtils.initializeToolkit();
        
        //get test database connection
        connection = DriverManager.getConnection("jdbc:sqlite:garage_system_test.db");
	    testRepo = new SQLRepository(connection);
	    
	    // set user details
	    currentUser = testRepo.setUserDetails("test@test.com");
	    
	    statement = connection.createStatement();
	    
	    // initialize the method
        handleDeleteButtonMethod = ManageReservationsController.class.getDeclaredMethod("handleDeleteButton", ActionEvent.class);
        handleDeleteButtonMethod.setAccessible(true);
        
        // set controller
        controller = new ManageReservationsController(currentUser, connection);
        injectMockComponents();
	}
	
	private void injectMockComponents() {
		controller.setReservationsList(new ListView<>());
		controller.setDeleteButton(new Button());
	}
	
	@AfterEach
    public void closeConnection() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
	
	private class TestManageReservationsController extends ManageReservationsController {
        public TestManageReservationsController(CurrentUser currentUser, Connection connection) {
            super(currentUser, connection);
        }
        
        @Override
        protected Optional<ButtonType> showConfirmationDialog(Alert alert) {
            // Skip showing the dialog and simulate pressing OK
            return Optional.of(ButtonType.OK);
        }
        
        @Override
        protected void showInformationDialog(Alert alert) {
            // Skip showing the information dialog entirely
        }
    }
	
	//method to populate test reservation table
    private void populateGarage() throws SQLException {
    	String startDate = localDate.toString(); 
        String endDate = localDate.plusDays(3).toString(); 
        
        // clears the reservation table
     	statement.execute("DELETE FROM reservations");
        
        // add reservations for testing
        statement.execute("INSERT INTO reservations (email, license_plate, start_date, end_date, alreadyParked) " +
                          "VALUES ('test@test.com', 'temp1' , '" + startDate + "', '" + endDate + "', 0)");
        statement.execute("INSERT INTO reservations (email, license_plate, start_date, end_date, alreadyParked) " +
                          "VALUES ('test@test.com', 'temp2' , '" + startDate + "', '" + endDate + "', 0)");
    }
	
	@Test
    public void testLoadReservations() throws SQLException {
		
		populateGarage();
        controller.initialize();
        assertFalse(controller.getReservationsList().getItems().isEmpty(), "Reservations list should not be empty after loading.");
    }
	
	@Test
	public void testDeleteReservation() throws Exception {
		// Create your test controller instance
	    TestManageReservationsController controller = new TestManageReservationsController(currentUser, connection);
	    
	    
	}
}

