package reservespotpkg;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import addfundspkg.AddFundsController;
import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import javafx.util.Duration;
import loginpkg.LoginController;
import menupkg.MenuController;
import objects.CurrentUser;
import objects.Reservation;
import scenechangerpkg.SceneChanger;
import sqliterepo.DBConnection;
import sqliterepo.SQLRepository;

/**
 * controller class for handling reservation user input for dates, license
 * plates, and payment account balance checks and updating the database with
 * reservations
 */

public class ReserveSpotController {

	/**
	 * constructor has params for ReserveSpotController
	 * pass in user and connection from login
	 * @param currentUser
	 * @param connection
	 */

	// inject the current user object
	public ReserveSpotController(CurrentUser currentUser, Connection connection) {
		this.currentUser = currentUser;
		this.connection = connection;
		sqlRepository = new SQLRepository(connection);
	}

	private Connection connection;
	private CurrentUser currentUser;
	private SQLRepository sqlRepository;

	// boolean used for testing purposes
	boolean successfulRegistration = false;

	@FXML
	private Label accountBalanceField;
	
	@FXML
	private Label label1;

	@FXML
	private Label label2;

	@FXML
	private Label label3;

	@FXML
	private DatePicker startDate;
	@FXML
	private DatePicker endDate;

	@FXML
	private TextField licensePlate;

	@FXML
	private Button mainMenuButton;

	@FXML
	private Button payButton;

	@FXML
	private Button addFundsButton;

	private LocalDate start;
	private LocalDate end;

	// store total number of days reserved temporarily
	private int daysBetween;

	private double chargedUser;

	/**
	 * initializes the controller,sets up the inactivity timer 
	 * and removes past
	 * dates from the calendar. ensures a valid database connection.
	 */

	@FXML
	public void initialize() {

		// call remove past dates, then initialize the database connection to remove the
		// past dates on the calendar at startup
		removePastDatesFromCalendar();
		
		// display users current balance as soon as they arrive on the scene
		loadUserBalance(currentUser);
	}

	/**
	 * handles the main menu button navigates back to the main menu scene
	 *
	 * @param triggered by the button click
	 */

	@FXML
	private void handleMenuButton(ActionEvent event) {

		MenuController menuController = new MenuController(currentUser, connection);

		SceneChanger sc = new SceneChanger();
		sc.sceneChanger(event, "menu", "UIS Parking Garage Main Menu", menuController);
	}

	/**
	 * handles the "add funds" button navigates to the add funds scene
	 *
	 * @param triggered by the button click
	 */

	@FXML
	private void handleAddFundsButton(ActionEvent event) {

		// Create an instance of the Add Funds controller and pass user info to it
		AddFundsController addFundsController = new AddFundsController(currentUser, connection);
		SceneChanger sc = new SceneChanger();
		sc.sceneChanger(event, "addfunds", "Account Balance Overview", addFundsController);
	}

	/**
	 * handles input for the license plate text field add license plate to object
	 */

	@FXML
	private void handleLicensePlate() {
//		add licnese to object
		licensePlate.getText();
		String lp = licensePlate.getText();
		System.out.println("License Plate = " + lp);
	}

	/**
	 * handles the selection of start and end dates. calculates the total cost based
	 * on the number of days reserved.
	 */

	@FXML
	private void handleDateRange() {
		start = startDate.getValue();
		end = endDate.getValue();
		System.out.printf("Start Date: %s, End Date: %s%n", start, end);

		if (start != null && end != null) {
			if (end.isBefore(start)) {
				System.out.println("End date cannot be before the start date.");
				label1.setText("Invalid Date Selection");
				return;
			}

			// Calculate the number of days between start and end
			daysBetween = (int) ChronoUnit.DAYS.between(start, end) + 1;
//			System.out.printf("Days selected: %d%n", daysBetween);
			label1.setText("Start Date: " + start + "      End Date: " + end);
			label2.setText("Days Reserved: " + daysBetween + "      Total cost: $" + (daysBetween * 10));
		} else {
			System.out.println("Please select both start and end dates.");
			label1.setText("Select both start and end dates");
		}
	}

	/**
	 * handles the "pay" button click checks user balance and processes payment if
	 * funds are available
	 *
	 * @param triggered by the button click
	 */

	@FXML
	private void handlePayButton(ActionEvent event) {

		// this needs to first check the users balance against the days being reserved
		double charge = (daysBetween * 10);
		chargedUser = charge;
		System.out.println("Checking balance: " + currentUser.getAccountBalance());
//		System.out.println("Your account will be charged: " + charge);
		// check license plate if it's not unique, then return that it is in parking
		// spot #
		if (!sqlRepository.checkLicenseUnique(licensePlate.getText())) {
//			System.out.println("License Plate is already in garage in spot #"
//					+ sqlRepository.findByLicensePlate(licensePlate.getText()));

			String uniqueLicense = "License Plate is already in garage in spot #"
					+ sqlRepository.findByLicensePlate(licensePlate.getText());
			label1.setText(uniqueLicense);

			// log user out, disables other buttons from working.
			mainMenuButton.setDisable(true);
			payButton.setDisable(true);
			addFundsButton.setDisable(true);
			label2.setText("Now logging out...");
			label3.setText("You will NOT be charged");
			logout(10);

			// check users funds
		} else if (currentUser.getAccountBalance() < (charge)) {

//			System.out.println("Insuffiecient Funds, add more funds");
			label1.setText("Insuffiecient Funds, add more funds");
		}

		else {
			// if license plate is not in the garage and the user has sufficient funds then
			// add the user to the reservation table
			Reservation reserve = new Reservation();
			reserve.setEmail(currentUser.getEmail());
			reserve.setLicenseplate(licensePlate.getText());
			reserve.setStart_date(start);
			reserve.setEnd_date(end);
			try {
				sqlRepository.makeReservation(reserve);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				CurrentUser newBalance = sqlRepository.updateUserBalance(currentUser, (-charge));
				System.out.println("Your available funds are: $" + newBalance.getAccountBalance());
				Double newbal = newBalance.getAccountBalance();
				String pmt = "Payment processed, Your available funds are: $" + newbal.toString();
				label1.setText(pmt);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// log user out, disables other buttons from working.
			mainMenuButton.setDisable(true);
			payButton.setDisable(true);
			addFundsButton.setDisable(true);

			label2.setText("Days Reserved: " + start + " - " + end + "| Charged: $" + chargedUser);

			// boolean used for testing purposes
			successfulRegistration = true;

			logout(10);
		}

	}

	/**
	 * logs out the user after amt of time disables buttons and navigates back to
	 * login
	 *
	 * @param delay time in seconds before logging out
	 */

	private void logout(int time) {
		// Create a PauseTransition for a 30-second delay
		PauseTransition pause = new PauseTransition(Duration.seconds(time));

		// Define what happens after the delay
		pause.setOnFinished(e -> {

			// Re-enables buttons on logout
			mainMenuButton.setDisable(false);
			payButton.setDisable(false);
			addFundsButton.setDisable(false);

			// Clear the current user information
			currentUser = null;

			LoginController loginController = new LoginController(connection);
			SceneChanger sc = new SceneChanger();
			sc.sceneEndChanger(null, "login", "UIS Parking Garage Login", loginController);
		});

		// Start the pause transition
		pause.play();
	}

	/**
	 * removes dates from calendar that are previous to todays local date
	 */
	@FXML
	public void removePastDatesFromCalendar() {
		Callback<DatePicker, DateCell> dayCellFactory = dp -> new DateCell() {
			@Override
			public void updateItem(LocalDate item, boolean empty) {
				super.updateItem(item, empty);

				// disable the dates before today
				if (item.isBefore(LocalDate.now())) {
					setDisable(true);
					// grey out the dates
					setStyle("-fx-background-color: #cccccc;");
				}
			}
		};

		startDate.setDayCellFactory(dayCellFactory);
		endDate.setDayCellFactory(dayCellFactory);
	}
	
	/*
	 * 
	 * method to retrieve and display user's balance from the Current User
	 * 
	 */

	private void loadUserBalance(CurrentUser currentUser) {

		accountBalanceField.setText(String.format("$%.2f", currentUser.getAccountBalance()));
	}

	/**
	 * logs out the user after inactivity clears the current user and navigates to
	 * the login screen
	 */

	private void logout() {
		// Clear the current user information
		currentUser = null;
		// Create the SceneChanger and switch to login once registration is complete
		LoginController loginController = new LoginController(connection);
		SceneChanger sc = new SceneChanger();
		sc.sceneEndChanger(null, "login", "UIS Parking Garage Login", loginController);
	}

	/**
	 * Getters and setters for testing purposes
	 * 
	 * @return
	 */
	public Label getLabel1() {
		return label1;
	}

	public Label getLabel2() {
		return label2;
	}

	public Label getLabel3() {
		return label3;
	}

	public DatePicker getStartDate() {
		return startDate;
	}

	public DatePicker getEndDate() {
		return endDate;
	}

	public TextField getLicensePlate() {
		return licensePlate;
	}

	public void setLabel1(Label label1) {
		this.label1 = label1;
	}

	public void setLabel2(Label label2) {
		this.label2 = label2;
	}

	public void setLabel3(Label label3) {
		this.label3 = label3;
	}

	public void setStartDate(DatePicker startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(DatePicker endDate) {
		this.endDate = endDate;
	}

	public void setLicensePlate(TextField licensePlate) {
		this.licensePlate = licensePlate;
	}

	public void setMainMenuButton(Button button) {
		this.mainMenuButton = button;

	}

	public void setPayButton(Button button) {
		this.payButton = button;

	}

	public void setAddFundsButton(Button button) {
		this.addFundsButton = button;

	}

}