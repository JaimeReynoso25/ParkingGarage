package reservespotpkg;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import objects.CurrentUser;
import sqliterepo.SQLRepository;
import toolkit.JavaFXTestUtils;

public class ReserveSpotControllerTest {

    private ReserveSpotController controller;
    private Connection connection;
    private SQLRepository testRepo;
    private CurrentUser currentUser;
    private Method handleDateRangeMethod;
    private Method handlePayButtonMethod;
    private LocalDate localDate = LocalDate.now();
    private ActionEvent mockEvent;
    private Statement statement;


    @BeforeEach
    public void setup() throws SQLException, ClassNotFoundException, NoSuchMethodException, SecurityException {
//    	java toolkit for java application thread
    	JavaFXTestUtils.initializeToolkit();
    	
    	// set connection to the db
        connection = DriverManager.getConnection("jdbc:sqlite:garage_system_test.db");
        testRepo = new SQLRepository(connection);
        
        // set the user details
        currentUser = testRepo.setUserDetails("test@test.com");
        
        ActionEvent mockEvent = new ActionEvent();
        
        // setup the methods 
        handleDateRangeMethod = ReserveSpotController.class.getDeclaredMethod("handleDateRange");
        handleDateRangeMethod.setAccessible(true);
        handlePayButtonMethod = ReserveSpotController.class.getDeclaredMethod("handlePayButton", ActionEvent.class);
        handlePayButtonMethod.setAccessible(true);

        // intialize the controller
        controller = new ReserveSpotController(currentUser, connection);
        injectMockComponents();
    }

    private void injectMockComponents() {
//      set uup the new components
        controller.setStartDate(new DatePicker());
        controller.setEndDate(new DatePicker());
        controller.setLicensePlate(new TextField());
        controller.setLabel1(new Label());
        controller.setLabel2(new Label());
        controller.setLabel3(new Label());
        
        controller.setMainMenuButton(new Button());
        controller.setPayButton(new Button());
        controller.setAddFundsButton(new Button());
    }
    
    @AfterEach
    public void closeConnection() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }

//TC001
    @Test
    public void testHandleDateRange_ValidDates() throws Exception {
        // setup valid dates
        controller.getStartDate().setValue(LocalDate.of(2024, 1, 1));
        controller.getEndDate().setValue(LocalDate.of(2024, 1, 5));

        handleDateRangeMethod.invoke(controller);

        // assert valid dates
        assertEquals("Start Date: 2024-01-01      End Date: 2024-01-05", controller.getLabel1().getText());
        assertEquals("Days Reserved: 5      Total cost: $50", controller.getLabel2().getText());
    }

    @Test
    public void testHandleDateRange_InvalidEndDate() throws Exception {
        // setup the invalid dates
        controller.getStartDate().setValue(LocalDate.of(2024, 1, 5));
        controller.getEndDate().setValue(LocalDate.of(2024, 1, 1));

        handleDateRangeMethod.invoke(controller);

        // assert invalid dates
        assertEquals("Invalid Date Selection", controller.getLabel1().getText());
    }
    
    @Test
    public void testPayButton_InsufficientFunds() throws Exception {
    	// input a reservation for 100 days, which would cost $1000
    	controller.getLicensePlate().setText("poorMan");
    	LocalDate startDate = localDate;
    	LocalDate endDate = localDate.plusDays(99); // <----- This number is 0-based. so '99' is actually 100 days
    	
    	controller.setStartDate(new DatePicker(startDate));
    	controller.setEndDate(new DatePicker(endDate));
    	
    	// call methods
    	handleDateRangeMethod.invoke(controller);
    	handlePayButtonMethod.invoke(controller, mockEvent);
    	
    	// verify that insufficient funds error message shows
    	assertEquals("Insuffiecient Funds, add more funds", controller.getLabel1().getText());
    	
    }
    
    @Test
    public void testPayButton_SuccessfulRegistration() throws Exception {
    	// input a reservation for 5 days, which would cost $50
    	controller.getLicensePlate().setText("ImRich");
    	LocalDate startDate = localDate;
    	LocalDate endDate = localDate.plusDays(4); // <----- This number is 0-based. so '4' is actually 5 days
    	
    	controller.setStartDate(new DatePicker(startDate));
    	controller.setEndDate(new DatePicker(endDate));
    	
    	// call methods
    	handleDateRangeMethod.invoke(controller);
    	handlePayButtonMethod.invoke(controller, mockEvent);
    	
    	// verify that registration is successful
    	assertTrue(controller.successfulRegistration);
    	
    	// clears the reservation table
    	statement = connection.createStatement();
     	statement.execute("DELETE FROM reservations");
     	
     	// adds $50 back into the account in order to bring test user to default $100 state for accountBalance
     	SQLRepository testRepo = new SQLRepository(connection);
     	testRepo.updateUserBalance(currentUser, 50);
    }
    
}
