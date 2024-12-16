package addfundspkg;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import objects.CurrentUser;
import sqliterepo.SQLRepository;
import toolkit.JavaFXTestUtils;

public class AddFundsControllerTest {

    private AddFundsController controller;
    private CurrentUser currentUser;
    private Method handleAddFundsButtonMethod;
    private Connection connection;
    private SQLRepository testRepo;
    

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
        handleAddFundsButtonMethod = AddFundsController.class.getDeclaredMethod("handleAddFundsButton");
        handleAddFundsButtonMethod.setAccessible(true);
        
        // set controller
        controller = new AddFundsController(currentUser, connection);
        injectMockComponents();
    }

    private void injectMockComponents() {
        // assign java components
        controller.setAddFundsField(new TextField());
        controller.setStatusLabel(new Label());
        controller.setAccountBalanceField(new Label());
    }

    @AfterEach
    public void closeConnection() throws Exception {
        if (connection != null) {
            connection.close();
        }
    } 
    //TC001
    /*
     * 
     *  I edited this to include the test database instead of the mock database.
     * 
     */
    @Test
    public void testHandleAddFundsButton_ValidInput() throws Exception {
    	// set funds field to $50
    	double addedFunds = 50;
        controller.getAddFundsField().setText(String.valueOf(addedFunds));
        System.out.println("valid input funds added");
        
        handleAddFundsButtonMethod.invoke(controller);

        //verify assumptions
        assertEquals("Your funds have been added!", controller.getStatusLabel().getText());
        
        // Subtracts the funds that were added, to ensure that the Test user has the same funds as before
        String query = "UPDATE users SET balance = balance - ? WHERE email = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setDouble(1, addedFunds);
            pstmt.setString(2, currentUser.getEmail());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

//    TC002
    @Test
    public void testHandleAddFundsButton_InvalidInput_NonNumeric() throws Exception {
        // sets the funds added
        controller.getAddFundsField().setText("invalid");
        
        // invoke the controller       
        handleAddFundsButtonMethod.invoke(controller);

        // create the assertion
        assertEquals("Please enter a valid number.", controller.getStatusLabel().getText());
    }
//TC003
    @Test
    public void testHandleAddFundsButton_InvalidInput_NegativeNumber() throws Exception {
        // sets the funds added
        controller.getAddFundsField().setText("-50");
   
        // invoke the controller
        handleAddFundsButtonMethod.invoke(controller);

        // create the assertion
        assertEquals("Please enter a positive number.", controller.getStatusLabel().getText());
    }
//TC004
    /*
     * 
     *  I edited to include test database
     * 
     */
    @Test
    public void testLoadUserBalance() throws Exception {
        // loads up the method to grab the user balance
        Method loadUserBalance = AddFundsController.class.getDeclaredMethod("loadUserBalance", CurrentUser.class);
        loadUserBalance.setAccessible(true);
        loadUserBalance.invoke(controller, currentUser);

        // converts currentUser.getAccountBalance() to a string in order to compare correctly
        assertEquals(String.format("$%.2f", currentUser.getAccountBalance()), controller.getAccountBalanceField().getText());
        System.out.println("load balance funds added");
    } 
}