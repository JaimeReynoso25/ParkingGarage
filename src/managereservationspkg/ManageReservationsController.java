package managereservationspkg;

import java.sql.Connection;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import menupkg.MenuController;
import objects.CurrentUser;
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
	private void handleMainMenuButton(ActionEvent event) {
		System.out.println("Current user email: " + currentUser.getEmail());
		System.out.println("Current user balance : $" + currentUser.getAccountBalance());
		
		// Create an instance of the main menu controller and pass user info to it
		MenuController menuController = new MenuController(currentUser, connection);
		SceneChanger sc = new SceneChanger();
		sc.sceneChanger(event, "menu", "UIS Parking Garage Menu", menuController);
	}

}
