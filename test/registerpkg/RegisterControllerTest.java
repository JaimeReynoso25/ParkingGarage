package registerpkg;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import sqliterepo.SQLRepository;
import toolkit.JavaFXTestUtils;

public class RegisterControllerTest {

	//Global variables 
    private RegisterController controller;
    private SQLRepository testRepo;
    private Connection connection;
    private Method handleRegisterButtonMethod;

    @BeforeEach
    public void setup() throws SQLException, NoSuchMethodException, SecurityException {
    	
    	JavaFXTestUtils.initializeToolkit();
    	
    	//get test database connection
        connection = DriverManager.getConnection("jdbc:sqlite:garage_system_test.db");
	    testRepo = new SQLRepository(connection);
               
        // initialize the methods for use
        handleRegisterButtonMethod = RegisterController.class.getDeclaredMethod("handleRegisterButton");
        handleRegisterButtonMethod.setAccessible(true);
        
        // setup the new controller
        controller = new RegisterController(connection);
        injectMockComponents();
        
        
    }
    
    private void injectMockComponents() {
    	// assign controller
    	controller.setEmailField(new TextField());
        controller.setPasswordField(new PasswordField());
        controller.setStatusLabel1(new Label());
    }
    
    @AfterEach
    public void closeConnection() throws Exception {
        if (connection != null) {
            connection.close();
        }
    }
//TC001
    @Test
    public void testHandleRegisterButton_EmptyFields() throws Exception {
        // setup empty fields
        controller.getEmailField().setText("");
        controller.getPasswordField().setText("");

        handleRegisterButtonMethod.invoke(controller);

        // assert status label
        assertEquals("Please enter both fields", controller.getStatusLabel1().getText());
    }
//TC002
    @Test
    public void testHandleRegisterButton_InvalidEmail() throws Exception {
        // setup invalid email
        controller.getEmailField().setText("invalid-email");
        controller.getPasswordField().setText("password123");

        handleRegisterButtonMethod.invoke(controller);

        // assert status label
        assertEquals("Please enter a valid email", controller.getStatusLabel1().getText());
    }
    
    @Test
    public void testHandleRegisterButton_OnlyPassword() throws Exception {
        // setup only a password
        controller.getPasswordField().setText("password123");

        // invoke the controller to the method
        handleRegisterButtonMethod.invoke(controller);

        // assert the status label
        assertEquals("Please enter both fields", controller.getStatusLabel1().getText());
    }
//TC004   
    /*
     * 
     * Changed from testHandleRegisterButton_ValidInput() to testHandleRegisterButton_SuccesfulRegistration()
     * 
     * 
     */
    @Test
    public void testHandleRegisterButton_SuccesfulRegistration() throws Exception {
        // set new user email and password
        controller.getEmailField().setText("tempUser@example.com");
        controller.getPasswordField().setText("password");

        handleRegisterButtonMethod.invoke(controller);

        // validate output
        assertEquals("Registration successful!", controller.getStatusLabel1().getText());
        
        // deletes user from the database after test is complete
        String query = "DELETE FROM users WHERE email = ?";
        try (PreparedStatement statement = connection.prepareStatement(query)) {
        	statement.setString(1, controller.getEmailField().getText());
        	statement.executeUpdate();
        }
    }
//TC005    
    @Test
    public void testHandleRegisterButton_EmailNotUnique() throws IllegalAccessException, InvocationTargetException {
    	// set a known user email and password
        controller.getEmailField().setText("test@test.com");
        controller.getPasswordField().setText("test");
        
        handleRegisterButtonMethod.invoke(controller);
        
        // validate output
        assertEquals("Email already exists. Please use a different email.", controller.getStatusLabel1().getText());
    }
}
