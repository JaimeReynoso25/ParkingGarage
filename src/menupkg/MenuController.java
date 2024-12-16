package menupkg;

import java.sql.Connection;

import addfundspkg.AddFundsController;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.util.Duration;
import loginpkg.LoginController;
import objects.CurrentUser;
import reservespotpkg.ReserveSpotController;
import scenechangerpkg.SceneChanger;
import wheresmycarpkg.WheresMyCarController;

/**
 * controller class for the main menu of the parking garage application.
 * handles user interactions for navigating to different functionalitie:
 * adding funds, reserving a parking spot, car location.
 */

public class MenuController {
	
	/**
	 * default constructor
	 */

	public MenuController() {
	}
	
	
	  /**
     * parameter constructor for menucontroller.
     *
     * @param currentUser: currently logged-in user
     * @param connection to database connection
     */

	public MenuController(CurrentUser currentUser, Connection connection) {
		this.currentUser = currentUser;
		this.connection = connection;
	}

	private CurrentUser currentUser;

	private Connection connection;

	@FXML
	private Button addFundsButton;

	@FXML
	private Button reserveSpotButton;

	@FXML
	private Button wheresMyCarButton;

	@FXML
	private Button logOutButton;

	
	/**
     * handles the "add funds" button click
     * navigates to the add funds scene
     *
     * @param triggered by the button click
     */
	
	// Method to handle add funds button
	@FXML
	private void handleAddFundsButton(ActionEvent event) {
		System.out.println("Current user email: " + currentUser.getEmail());
		System.out.println("Current user balance : $" + currentUser.getAccountBalance());

		// Create an instance of the Add Funds controller and pass user info to it
		AddFundsController addFundsController = new AddFundsController(currentUser, connection);

		SceneChanger sc = new SceneChanger();
		System.out.println(connection == null ? "Connection is NULL." : "Connection is NOT NULL.");
		sc.sceneChanger(event, "addfunds", "UIS Account Balance Overview", addFundsController);
		
	}
	
	
	  /**
     * handles the "reserve a spot" button
     * navigates to the reserve spot scene
     *
     * @param triggered by the button click
     */
	
	// Method to handle reserve a spot button
	@FXML
	private void handleReserveSpotButton(ActionEvent event) {
		System.out.println("Current user email: " + currentUser.getEmail());
		System.out.println("Current user balance : $" + currentUser.getAccountBalance());

		ReserveSpotController reserveSpotController = new ReserveSpotController(currentUser, connection);

		SceneChanger sc = new SceneChanger();
		sc.sceneChanger(event, "reservespot", "UIS Parking Garage Reservation", reserveSpotController);
	}

	  /**
     * handles the "where's my car" button click
     * navigates to the car location scene
     *
     * @param event the action event triggered by the button click
     */
	
	@FXML
	private void handleWheresMyCarButton(ActionEvent event) {
		System.out.println("Current user email: " + currentUser.getEmail());
		System.out.println("Current user balance : $" + currentUser.getAccountBalance());

		WheresMyCarController wheresMyCarController = new WheresMyCarController(currentUser, connection);
		SceneChanger sc = new SceneChanger();
		sc.sceneChanger(event, "wheresmycar", "UIS PArking Garage Where's My Car", wheresMyCarController);
	}
	
    /**
     * handles the "log out" button click 
     * logs out the user and navigates to the login scene.
     *
     * @param event the action event triggered by the button click
     */

	// Method to handle log out button
	@FXML
	private void handleLogOutButton(ActionEvent event) {
		currentUser.clear();
		System.out.println("Current user email: " + currentUser.getEmail());
		System.out.println("Current user balance : $" + currentUser.getAccountBalance());

		LoginController loginController = new LoginController(connection);
		SceneChanger sc = new SceneChanger();
		sc.sceneChanger(event, "login", "UIS Parking Garage Login", loginController);
	}

}


