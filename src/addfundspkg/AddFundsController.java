package addfundspkg;

import java.net.URL;
import java.sql.Connection;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import menupkg.MenuController;
import objects.CurrentUser;
import scenechangerpkg.SceneChanger;
import sqliterepo.SQLRepository;



/**
 * controller class to "add funds" in the parking garage application.
 * users can add funds to their account also displays their updated balance.
 */

public class AddFundsController implements Initializable {

	
    /**
     * default constructor
     */
	public AddFundsController() {
	}
	
	/**
     * constructor for addfundscontroller has parameters.
     *
     * @param currentUser: logged-in user
     * @param connection: database connection
     */

	public AddFundsController(CurrentUser currentUser, Connection connection) {
		this.currentUser = currentUser;
		this.connection = connection;
		sqlRepository = new SQLRepository(connection);
	}


	private CurrentUser currentUser;

	private Connection connection;
	private SQLRepository sqlRepository;

	@FXML
	private Label accountBalanceField;

	@FXML
	private Button addFundsButton;

	@FXML
	private Button mainMenuButton;

	@FXML
	private TextField addFundsField;

	@FXML
	private Label statusLabel;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// display users current balance as soon as they arrive on the scene
		loadUserBalance(currentUser);
	}

	
	/**
     * handles the "main menu" button click
     * navigates back to the main menu scene
     *
     * @param triggered by the button click
     */
	
	@FXML
	private void handleMainMenuButton(ActionEvent event) {
		System.out.println("Current user email: " + currentUser.getEmail());
		System.out.println("Current user balance : $" + currentUser.getAccountBalance());
		
		// Create an instance of the main menu controller and pass user info to it
		MenuController menuController = new MenuController(currentUser, connection);
		SceneChanger sc = new SceneChanger();
		sc.sceneChanger(event, "menu", "UIS Parking Garage Menu", menuController);
	}

	  /**
     * handles the "add funds" button click 
     * validates the input, updates the user's account balance, and shows the updated balance
     *
     * @throws ClassNotFoundException if the SQLite JDBC driver is not found
     */
	@FXML
	private void handleAddFundsButton() throws ClassNotFoundException {

		try {
			double addFunds = Double.parseDouble(addFundsField.getText());
			if (addFunds > 0) {
				currentUser = sqlRepository.updateUserBalance(currentUser, addFunds);
				
				statusLabel.setText("Your funds have been added!");
				statusLabel.setVisible(true);
				// updates the current account balance on the scene
				loadUserBalance(currentUser); 
				addFundsField.clear();
			} else {
				// Handle invalid input
				statusLabel.setText("Please enter a positive number.");
				System.out.println("Please enter a positive number.");
				statusLabel.setVisible(true);
			}

		} catch (NumberFormatException e) {
			statusLabel.setText("Please enter a valid number.");
			System.out.println("Please enter a valid number.");
			statusLabel.setVisible(true);
		}
	}


/*
 * 
 * method to retrieve and display user's balance from the Current User
 * 
 */

	private void loadUserBalance(CurrentUser currentUser) {

		accountBalanceField.setText(String.format("$%.2f", currentUser.getAccountBalance()));
	}

	
	/*
	 * Getters and setters below for testing
	 * AddFundsControllerTest.java
	 * 
	 */

	public TextField getAddFundsField() {
		return addFundsField;
	}

	public void setAddFundsField(TextField addFundsField) {
		this.addFundsField = addFundsField;
	}

	public Label getStatusLabel() {
		return statusLabel;
	}

	public void setStatusLabel(Label statusLabel) {
		this.statusLabel = statusLabel;
	}

	public Label getAccountBalanceField() {
		return accountBalanceField;
	}

	public void setAccountBalanceField(Label accountBalanceField) {
		this.accountBalanceField = accountBalanceField;
	}

	public void setCurrentUser(CurrentUser currentUser) {
		this.currentUser = currentUser;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

}
