package managereservationspkg;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
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
	private void handleDeleteButton(ActionEvent event) throws SQLException {
		Reservation selectedReservation = reservationsList.getSelectionModel().getSelectedItem();
		
		if (selectedReservation != null) {
			
			//delete from DataBase
			sqlRepository.deleteReservation(selectedReservation.getLicenseplate());
			
			// Remove from ListView
            reservationsList.getItems().remove(selectedReservation);
		}
	}

}
