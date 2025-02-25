package reservespotpkg;

import java.sql.Connection;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import menupkg.MenuController;
import objects.CurrentUser;
import objects.Reservation;
import scenechangerpkg.SceneChanger;
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

	// removes past dates from calendar and loads the users balance
	@FXML
	public void initialize() {
		removePastDatesFromCalendar();
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
	 * handles the selection of start and end dates. calculates the total cost based
	 * on the number of days reserved.
	 */

	@FXML
	private void handleDateRange() {
		start = startDate.getValue();
		end = endDate.getValue();

		if (start != null && end != null) {
			if (end.isBefore(start)) {
				label1.setText("Invalid Date Selection");
				return;
			}

			// Calculate the number of days between start and end
			daysBetween = (int) ChronoUnit.DAYS.between(start, end) + 1;
			label1.setText("Start Date: " + start + "      End Date: " + end);
			label2.setText("Days Reserved: " + daysBetween + "      Total cost: $" + (daysBetween * 10));
		} else {
			label1.setText("Select both start and end dates");
		}
	}

	/**
	 * handles the "pay" button click checks user balance and processes payment if
	 * funds are available
	 *
	 * @param triggered by the button click
	 * @throws ClassNotFoundException 
	 */

	@FXML
	private void handlePayButton(ActionEvent event) throws ClassNotFoundException {

		// this needs to first check the users balance against the days being reserved
		double charge = (daysBetween * 10);
		chargedUser = charge;
		
		// check if license plate already has a reservation. 
		if (!sqlRepository.checkLicenseUnique(licensePlate.getText())) {

			label1.setText("This license plate already has a reservation set.");

			label2.setText("Only one license plate is allowed in our system.");
			label3.setText("Please pick a new license plate.");

			// check users funds
		} else if (currentUser.getAccountBalance() < (charge)) {

			label1.setText("Insuffiecient Funds, add more funds");
		}

		else {
			// if license plate is not in the garage and the user has sufficient funds then
			// add the user to the reservation table
			Reservation reserve = new Reservation(currentUser.getEmail(), licensePlate.getText(), start, end);
			sqlRepository.makeReservation(reserve);

			//updates the user's account balance
			currentUser = sqlRepository.updateUserBalance(currentUser, (-charge));
			loadUserBalance(currentUser);
			label1.setText("Payment processed, your remaining balance is: $" + currentUser.getAccountBalance());

			// disables other buttons from working.
			mainMenuButton.setDisable(true);
			payButton.setDisable(true);

			label2.setText("Days Reserved: " + start + " - " + end + "| Charged: $" + chargedUser);
			label3.setText("Now returning to the main menu...");

			// boolean used for JUnit testing purposes
			successfulRegistration = true;

			returnToMenu(10);
		}

	}

	/**
	 * This method automatically returns the user back to the 
	 * Main Menu with a 10 second delay after making a successful reservation
	 *
	 * @param delay time in seconds before logging out
	 */

	private void returnToMenu(int time) {
	    // Create a PauseTransition for a 10-second delay
	    PauseTransition pause = new PauseTransition(Duration.seconds(time));
	    
	    // Define what happens after the delay
	    pause.setOnFinished(e -> {
	        // Re-enables buttons once leaving the scene
	        mainMenuButton.setDisable(false);
	        payButton.setDisable(false);

	        try {
	            // Get the current stage if available
	            Stage currentStage = (Stage) mainMenuButton.getScene().getWindow();
	            
	            // Return to main menu if we have a stage
	            if (currentStage != null) {
	                MenuController menuController = new MenuController(currentUser, connection);
	                SceneChanger sc = new SceneChanger();
	                sc.sceneChanger(currentStage, "menu", "UIS Parking Garage Main Menu", menuController);
	            }
	        } catch (NullPointerException ex) {
	            // During testing, the scene might be null
	            // Just skip the scene change in this case
	            System.out.println("Scene change skipped in test environment");
	        }
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

	public void setAccountBalanceField(Label accountBalanceField) {
	    this.accountBalanceField = accountBalanceField;
	}

}