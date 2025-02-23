package registerpkg;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import loginpkg.LoginController;
import scenechangerpkg.SceneChanger;
import sqliterepo.SQLRepository;

public class RegisterController implements Initializable {

	/**
	 * default constructor
	 */

	public RegisterController() {
	}

	/**
	 * constructor for RegisterController, has parameter
	 * 
	 * @param connection to database
	 */
	public RegisterController(Connection connection) {
		this.connection = connection;
		sqlRepository = new SQLRepository(connection);
	}

	private Connection connection;

	@FXML
	private TextField emailField;

	@FXML
	private PasswordField passwordField;

	@FXML
	private TextField passwordTextField;

	@FXML
	private Label statusLabel1;

	@FXML
	private Button loginReturnButton;

	@FXML
	private Button registerButton;

	@FXML
	private CheckBox passwordCheckBox;

	private SQLRepository sqlRepository;

	/**
	 * handles the "return to login" button navigates back to the login scene
	 *
	 * @param triggered by the button click
	 */
	@FXML
	private void handleLoginReturnButton(ActionEvent event) {

		LoginController loginController = new LoginController(connection);
		SceneChanger sc = new SceneChanger();
		sc.sceneChanger(event, "login", "UIS Parking Garage Login", loginController);
	}

	/**
	 * handles the "register" button validates user input, checks email uniqueness,
	 * registers the user if inputs are valid.
	 *
	 * @throws ClassNotFoundException if the SQLite JDBC driver is not found
	 */
	@FXML
	private void handleRegisterButton() throws ClassNotFoundException {

		String email = emailField.getText();
		String password = passwordField.isVisible() ? passwordField.getText() : passwordTextField.getText();

		// handle if the fields are empty
		if (email.isEmpty() || password.isEmpty()) {

			statusLabel1.setVisible(true);
			statusLabel1.setText("Please enter both fields");
			
		} else if (!validateEmail(email)) {

			statusLabel1.setText("Please enter a valid email");
			statusLabel1.setVisible(true);

		} else {
			if (!sqlRepository.isEmailUnique(email)) {
				statusLabel1.setText("Email already exists. Please use a different email.");
				statusLabel1.setVisible(true);
				return;
			}

			String registrationStatus = sqlRepository.registerUser(email, password);
			statusLabel1.setText(registrationStatus);
			statusLabel1.setVisible(true);
		}

	}

	/**
	 * some logic to validate email
	 * 
	 * @param email
	 * @return boolean
	 */
	private boolean validateEmail(String email) {
		String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
		Pattern p = Pattern.compile(ePattern);
		Matcher m = p.matcher(email);
		return m.matches();
	}

	/**
	 * initializes the controller. sets up the password visibility and tab key
	 *
	 * @param url the location used for root object paths
	 * @param resourceBundle used foir root objcet
	 */

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		// this allows us to toggle the "Show Password" checkbox
		passwordCheckBox.setOnAction(event -> {
			if (passwordCheckBox.isSelected()) {
				// show the password in plain text and password back to PasswordField
				passwordTextField.setText(passwordField.getText());
				passwordTextField.setVisible(true);
				passwordField.setVisible(false);
			} else {
				// hide the password and password back to PasswordField
				passwordField.setText(passwordTextField.getText());
				passwordField.setVisible(true);
				passwordTextField.setVisible(false);
			}
		});

		// makes sure that entering Tab in the email field takes you to password
		emailField.setOnKeyPressed(event -> {
			if (event.getCode() == KeyCode.TAB) {
				// move focus to the password field
				passwordField.requestFocus();
				// prevent the default behaivior
				event.consume();
			}
		});
	}

	/**
	 * getters and setters for testing
	 * 
	 * @return requested fields
	 */

	// Getters
	public TextField getEmailField() {
		return emailField;
	}

	public PasswordField getPasswordField() {
		return passwordField;
	}

	public TextField getPasswordTextField() {
		return passwordTextField;
	}

	public Label getStatusLabel1() {
		return statusLabel1;
	}

	public Button getLoginReturnButton() {
		return loginReturnButton;
	}

	public Button getRegisterButton() {
		return registerButton;
	}

	public CheckBox getPasswordCheckBox() {
		return passwordCheckBox;
	}

	public SQLRepository getSQLRepository() {
		return sqlRepository;
	}

	public Connection getConnection() {
		return connection;
	}

	// Setters
	public void setEmailField(TextField emailField) {
		this.emailField = emailField;
	}

	public void setPasswordField(PasswordField passwordField) {
		this.passwordField = passwordField;
	}

	public void setPasswordTextField(TextField passwordTextField) {
		this.passwordTextField = passwordTextField;
	}

	public void setStatusLabel1(Label statusLabel1) {
		this.statusLabel1 = statusLabel1;
	}

	public void setLoginReturnButton(Button loginReturnButton) {
		this.loginReturnButton = loginReturnButton;
	}

	public void setRegisterButton(Button registerButton) {
		this.registerButton = registerButton;
	}

	public void setPasswordCheckBox(CheckBox passwordCheckBox) {
		this.passwordCheckBox = passwordCheckBox;
	}

	public void setSQLRepository(SQLRepository sqlRepository) {
		this.sqlRepository = sqlRepository;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}
}
