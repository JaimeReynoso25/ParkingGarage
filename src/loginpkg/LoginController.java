package loginpkg;
import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import menupkg.MenuController;
import objects.CurrentUser;
import registerpkg.RegisterController;
import wheresmycarpkg.WheresMyCarController;
import scenechangerpkg.SceneChanger;
import sqliterepo.SQLRepository;

/**
 * controller for handling the login of the parking garage application
 * allows users to log in using their email and password or navigate to the registration paghe
 * includes features such as password visibility, input validation.
 */


public class LoginController implements Initializable {
	
	/**
	 * default constructor
	 */
	public LoginController() {
	}
	
    /**
     *  constructor for logincontroller, has parameter
     *
     * @param database connection
     */
	 public LoginController(Connection connection) {
	        this.connection = connection;
	        sqlRepository = new SQLRepository(connection);
	    }
	
	private Connection connection;
	
	@FXML
	private Button loginButton;
	
	@FXML
	private Button signUpButton;
	
	@FXML
	private Button carSearchButton;
	
	@FXML
	private PasswordField passwordField;
	
	@FXML
	private TextField passwordTextField;
	
	@FXML 
	private TextField emailField;
	
	@FXML
	private CheckBox passwordCheckBox;
	
	@FXML
    private Label statusLabel;
	
	private SQLRepository sqlRepository;
	
	
	/**
     * handles the "sign up" button click
     * navigates to the registration page
     *
     * @param triggered by the button click
     */
	
	@FXML
	private void handleSignUpButton(ActionEvent event) {
		
		RegisterController registerController = new RegisterController(connection);
		SceneChanger sc = new SceneChanger();
		sc.sceneChanger(event, "register", "UIS Parking Garage Login", registerController);
	}
	
	@FXML
	private void handleCarSearchButton(ActionEvent event) {
		
		//updates the garage table before entering the Where's my car scene
		sqlRepository.updateGarageTable();
		
		WheresMyCarController wheresMyCarController = new WheresMyCarController(connection);
		SceneChanger sc = new SceneChanger();
		sc.sceneChanger(event, "wheresmycar", "Dude, where's my car?!", wheresMyCarController);
	}
	
	
	 /**
     * handles the "login" button click
     * validates user input, authenticates the user, and navigates to the main menu on successful login
     * error messages for invalid password, emnail or empty fields.
     *
     * @param triggered by the button click
     * @throws ClassNotFoundException if the SQLite JDBC driver is not found
     */

	@FXML
	private void handleLoginButton(ActionEvent event) throws ClassNotFoundException {
		String email = emailField.getText();
        String password = passwordField.isVisible() ? passwordField.getText() : passwordTextField.getText();
        
        
        //handle errors for empty fields
        if (email.isEmpty() || password.isEmpty()) {
        	statusLabel.setVisible(true);
        	statusLabel.setText("Please enter both fields");
        	System.out.println("Please fill out both fields");
            return;
        }
        
        // authenticate user
        boolean isAuthenticated = SQLRepository.authenticateUser(email, password);
        if (isAuthenticated) {
        	
        	//Creates currentUser object and sets the new data
        	CurrentUser currentUser = sqlRepository.setUserDetails(email);
        	statusLabel.setText("Logged in successfully!");
            System.out.println("Logged in succesfully!");
            System.out.println("Current user email: " + currentUser.getEmail());
            System.out.println("Current user balance : $" + currentUser.getAccountBalance());
            
            //populates the garage table right before log in, making sure 
            //that the current garage table is up to date
            
            sqlRepository.updateGarageTable();
            
            // Create an instance of the main menu controller and pass user info to it
            MenuController menuController = new MenuController(currentUser, connection);
          
            // Proceed to main menu
            SceneChanger sc = new SceneChanger();
            sc.sceneChanger(event, "menu", "UIS Parking Garage Menu", menuController);
        } else {
            statusLabel.setText("Invalid email or password");
            statusLabel.setVisible(true);
        }
	}
	
    /**
     * initializes the controller.
     * sets up the password visibility and tab key 
     *
     * @param url the location used for root object paths
     * @param resourceBundle used foir root objcet
     */

	public void initialize(URL arg0, ResourceBundle arg1) {
		// This allows us to toggle the "Show Password" checkbox
        passwordCheckBox.setOnAction(event -> {
            if (passwordCheckBox.isSelected()) {
                // Show the password in plain text
                passwordTextField.setText(passwordField.getText());  
                passwordTextField.setVisible(true);
                passwordField.setVisible(false);
            } else {
                // hide the password
            	// then copy password back to PasswordField
                passwordField.setText(passwordTextField.getText());  
                passwordField.setVisible(true);
                passwordTextField.setVisible(false);
            }
        });
        emailField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.TAB) {
                // move focus to the password field
                passwordField.requestFocus();
//                default prevention
                event.consume(); 
            }
        });
	}
	
	/*
	 * Getters and setters below for testing
	 * LoginControllerTest.java
	 * 
	 */
	
	public PasswordField getPasswordField() {
	    return passwordField;
	}

	public void setPasswordField(PasswordField passwordField) {
	    this.passwordField = passwordField;
	}

	public TextField getPasswordTextField() {
	    return passwordTextField;
	}

	public void setPasswordTextField(TextField passwordTextField) {
	    this.passwordTextField = passwordTextField;
	}

	public TextField getEmailField() {
	    return emailField;
	}

	public void setEmailField(TextField emailField) {
	    this.emailField = emailField;
	}

	public CheckBox getPasswordCheckBox() {
	    return passwordCheckBox;
	}

	public void setPasswordCheckBox(CheckBox passwordCheckBox) {
	    this.passwordCheckBox = passwordCheckBox;
	}

	public Label getStatusLabel() {
	    return statusLabel;
	}

	public void setStatusLabel(Label statusLabel) {
	    this.statusLabel = statusLabel;
	}

	public SQLRepository getSQLRepository() {
	    return sqlRepository;
	}

	public Connection getConnection() {
	    return connection;
	}

}