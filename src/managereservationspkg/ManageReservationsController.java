package managereservationspkg;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import menupkg.MenuController;
import objects.CurrentUser;
import objects.Reservation;
import scenechangerpkg.SceneChanger;
import sqliterepo.SQLRepository;

public class ManageReservationsController {
	
	public ManageReservationsController(CurrentUser currentUser, Connection connection) {
		this.currentUser = currentUser;
		this.connection = connection;
		sqlRepository = new SQLRepository(connection);
	}
	
	private Connection connection;
	private CurrentUser currentUser;
	private SQLRepository sqlRepository;
	
	@FXML
	private Button mainMenuButton;
	
	@FXML
	private Button deleteButton;
	
	@FXML
	private ListView<Reservation> reservationsList;
	
	@FXML
	private Label label;
	
	private int daysBetween;
	private LocalDate currentDate = LocalDate.now();
	private int refundAmount;
	
	public void initialize() throws SQLException {
		//updates garage table before loading everything up.
		sqlRepository.updateGarageTable();
		
		// display users reservations as soon as they arrive on the scene
		List<Reservation> reservations = sqlRepository.loadReservations(currentUser);
		reservationsList.getItems().addAll(reservations);
		
		// Add a listener to enable/disable delete button based on selection
        reservationsList.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> deleteButton.setDisable(newValue == null)
        );
        
        // Initially disable delete button
        deleteButton.setDisable(true);
	}
	
	@FXML
	private void handleMainMenuButton(ActionEvent event) {
		// Create an instance of the main menu controller and pass user info to it
		MenuController menuController = new MenuController(currentUser, connection);
		SceneChanger sc = new SceneChanger();
		sc.sceneChanger(event, "menu", "UIS Parking Garage Menu", menuController);
	}
	
	@FXML 
	private void handleDeleteButton(ActionEvent event) throws SQLException, ClassNotFoundException {
		Reservation selectedReservation = reservationsList.getSelectionModel().getSelectedItem();
		
		// Determines whether or not the reservation has started to get the correct refund amount
		if (currentDate.isBefore(selectedReservation.getStart_date())) {
			daysBetween = (int) ChronoUnit.DAYS.between(selectedReservation.getStart_date(), selectedReservation.getEnd_date()) + 1;
			refundAmount = daysBetween * 10;
			System.out.println(refundAmount + "<- Refund         days->" + daysBetween);
		} else {
			daysBetween = (int) ChronoUnit.DAYS.between(currentDate, selectedReservation.getEnd_date());
			refundAmount = daysBetween * 5;
			System.out.println(refundAmount + "<- Refund         days->" + daysBetween);
		}	
		
		if (selectedReservation != null) {
			//create confirmation dialog
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle("Confirm Deletion");
			alert.setHeaderText("Delete Reservation");
			alert.setContentText("Are you sure you want to delete the reservation?\n\nLicense Plate: " +
								  selectedReservation.getLicenseplate() + "\n\nRefund Amount: $" + refundAmount);
			
			//Show's alert box and wait for response
			Optional<ButtonType> result = alert.showAndWait();
			
			//if user confirms, proceed with deletion
			if (result.isPresent() && result.get() == ButtonType.OK) {	
				//delete from DataBase
				sqlRepository.deleteReservation(selectedReservation.getLicenseplate());
				
				// Remove from ListView
	            reservationsList.getItems().remove(selectedReservation);
	            
	            //Give the refund back to the user once reservation deleted
	            currentUser = sqlRepository.updateUserBalance(currentUser, refundAmount);     
	            
	            // Show success message with a new alert
	            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
	            successAlert.setTitle("Success");
	            successAlert.setHeaderText(null);  // No header text
	            successAlert.setContentText("Reservation for \"" + selectedReservation.getLicenseplate() + "\" deleted successfully!");
	            successAlert.showAndWait();
			}
		}
	}
}
