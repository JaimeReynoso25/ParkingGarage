package managereservationspkg;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Text;
import javafx.util.Callback;
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
	private ListView<String> reservationsList;
	
	// Display user's reservations as soon as they arrive on the scene
    public void initialize() throws SQLException {
        List<String> reservations = sqlRepository.loadReservations(currentUser);
        reservationsList.getItems().addAll(reservations);

        // Set a simple custom cell factory for each reservation
        reservationsList.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (!empty && item != null) {
                    // Create the text for the reservation details
                    Text text = new Text(item);

                    // Create the delete button and handle action
                    Button deleteButton = new Button("Delete");
                    deleteButton.setOnAction(e -> {
                        System.out.println("Delete clicked for: " + item);
                        // Add deletion logic here (remove from list and DB)
                    });

                    // Create HBox layout to contain both the text and button
                    HBox hbox = new HBox(10); // 10px spacing between text and button
                    hbox.getChildren().addAll(text, deleteButton);

                    // Set the graphic of the ListCell to the HBox (text + button)
                    setGraphic(hbox);
                } else {
                    setGraphic(null);  // Clear the cell content if it's empty
                }
            }
        });
    }
	
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
