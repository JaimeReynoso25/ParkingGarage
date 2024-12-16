package wheresmycarpkg;

import java.sql.Connection;
import java.sql.SQLException;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import loginpkg.LoginController;
import menupkg.MenuController;
import objects.CurrentUser;
import scenechangerpkg.SceneChanger;
import sqliterepo.DBConnection;
import sqliterepo.SQLRepository;

/**
 * controller for managing the "where's my car" functionality
 * handles locating a parked car in the garage
 * based on its license plate.
 */

public class WheresMyCarController {

    /**
     * default constructor for wheresmycarcontroller.
     */
    
    public WheresMyCarController() {
    }

    /**
     * constructor has parameters
     *
     * @param currentUser 
     * @param connection 
     */
    
    public WheresMyCarController(CurrentUser currentUser, Connection connection) {
        this.currentUser = currentUser;
        this.connection = connection;
        sqlRepository = new SQLRepository(connection);
    }

    /**
     * initializes the controller.
     * sets focus to the main menu button after the scene is initialized
     */
    
    @FXML
    public void initialize() {

        //platform.runLater() to set focus after the scene is initialized
        Platform.runLater(() -> {
            getMainMenuButton().requestFocus();
        });
    }

    private SQLRepository sqlRepository;

    private CurrentUser currentUser;

    private Connection connection;
    
    @FXML
    private Label locationText;

    @FXML
    private TextField licensePlate;

    @FXML
    private Button findMyCar;

    @FXML
    private Button mainMenuButton;

    /**
     * handles the license plate text field
     * resets the inactivity timer when the user interacts with the text field
     */
    
    @FXML
    private void handleLicensePlate() {
        getLicensePlate().getText();
    }
    
    /**
     * handles the "main menu" button 
     * navigates back to the main menu scene
     *
     * @param triggered by the button click
     */

    @FXML
    private void handleMainMenuButton(ActionEvent event) {

        System.out.println("Current user email: " + currentUser.getEmail());
        System.out.println("Current user balance : $" + currentUser.getAccountBalance());

        MenuController menuController = new MenuController(currentUser, connection);

        SceneChanger sc = new SceneChanger();
        sc.sceneChanger(event, "menu", "UIS Parking Garage Menu", menuController);
    }

    /**
     * handles the "find my car" button 
     * checks the location of the car based on the license plate
     *
     * @paramtriggered by the button click
     */
    
    @FXML
    private void handleFindMyCar(ActionEvent event) {

        String findUserEmail = currentUser.getEmail();
        System.out.println(findUserEmail);

        Integer spotId = sqlRepository.findByLicensePlate(getLicensePlate().getText());
        if (spotId == null) {
            getLocationText().setText("License plate not in garage");
        } else {
            System.out.println(spotId);
            getLocationText().setText("Your car is located in spot: " + spotId);
        }
    }

    /*
     * 
     *  Getters and Setters for testing
     * 
     */

	public Label getLocationText() {
		return locationText;
	}

	public void setLocationText(Label locationText) {
		this.locationText = locationText;
	}

	public TextField getLicensePlate() {
		return licensePlate;
	}

	public void setLicensePlate(TextField licensePlate) {
		this.licensePlate = licensePlate;
	}

	public Button getFindMyCar() {
		return findMyCar;
	}

	public void setFindMyCar(Button findMyCar) {
		this.findMyCar = findMyCar;
	}

	public Button getMainMenuButton() {
		return mainMenuButton;
	}

	public void setMainMenuButton(Button mainMenuButton) {
		this.mainMenuButton = mainMenuButton;
	}
}
