package wheresmycarpkg;

import java.sql.Connection;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import loginpkg.LoginController;
import objects.CurrentUser;
import scenechangerpkg.SceneChanger;
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
    
    public WheresMyCarController(Connection connection) {
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
            loginReturnButton.requestFocus();
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
    private Button loginReturnButton;

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
	private void handleLoginReturnButton(ActionEvent event) {

		LoginController loginController = new LoginController(connection);
		SceneChanger sc = new SceneChanger();
		sc.sceneChanger(event, "login", "UIS Parking Garage Login", loginController);
	}

    /**
     * handles the "find my car" button 
     * checks the location of the car based on the license plate
     *
     * @param triggered by the button click
     */
    
    @FXML
    private void handleFindMyCar(ActionEvent event) {

        Integer spotId = sqlRepository.findByLicensePlate(getLicensePlate().getText());
        if (spotId == null) {
            getLocationText().setText("License plate not in garage");
        } else {
            getLocationText().setText("Your car is located in spot: #" + spotId);
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

}
