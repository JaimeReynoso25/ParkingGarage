package managereservationspkg;

import static org.junit.jupiter.api.Assertions.assertFalse;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import addfundspkg.AddFundsController;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import objects.CurrentUser;
import sqliterepo.SQLRepository;
import toolkit.JavaFXTestUtils;

public class ManageReservationsControllerTest {

	private ManageReservationsController controller;
	private CurrentUser currentUser;
	private Method handleDeleteButtonMethod;
	private Connection connection;
	private SQLRepository testRepo;
	private ActionEvent mockEvent;
	
	@BeforeEach
	public void setup() throws NoSuchMethodException, SecurityException, SQLException, ClassNotFoundException {
		// java toolkit for java application thread
        JavaFXTestUtils.initializeToolkit();
        
        //get test database connection
        connection = DriverManager.getConnection("jdbc:sqlite:garage_system_test.db");
	    testRepo = new SQLRepository(connection);
	    
	    // set user details
	    currentUser = testRepo.setUserDetails("test@test.com");
	    
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
	
	@Test
    public void testLoadReservations() throws SQLException {
        controller.initialize();
        assertFalse(controller.getReservationsList().getItems().isEmpty(), "Reservations list should not be empty after loading.");
    }
}



























