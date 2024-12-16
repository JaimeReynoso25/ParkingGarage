package scenechangerpkg;

import java.io.IOException;

import application.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

/**
 * class for managing scene changes class allows navigation between different
 * views (scenes).
 * 
 * passing controllers to retain data
 */

public class SceneChanger {

	/**
	 * default constructor
	 */

	public SceneChanger() {
	}

	/**
	 * changes the current scene to a new one. loads the specified fxml file and
	 * sets the provided controller to the new scene
	 * 
	 * @param triggered  by a user interaction
	 * @param fxml       file for the new scene
	 * @param title
	 * @param controller object to manage the new scene
	 */

	public void sceneChanger(ActionEvent event, String fxmlFile, String title, Object controller) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile + "pkg/" + fxmlFile + ".fxml"));

			// set the controller for the new scene, passing on user data
			loader.setController(controller);
			HBox newPage = loader.load();

			// create a new scene with the loaded FXML
			Scene newScene = new Scene(newPage);

			// get the current stage and set the new scene
			Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			stage.setScene(newScene);
			stage.setTitle(title); 
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * changes the scene and clears user data if necessary. this method is for when
	 * the user is finished reservation, passing a null into action event causes
	 * all data from user to be removed prior to returning to login
	 * 
	 * @param triggered by a user interaction 
	 * @param fxml file for the new scene
	 * @param title 
	 * @param controller object to manage the new scene
	 */


	public void sceneEndChanger(ActionEvent event, String fxmlFile, String title, Object controller) {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/" + fxmlFile + "pkg/" + fxmlFile + ".fxml"));

			loader.setController(controller);
			HBox newPage = loader.load();

			// a new scene with the fxml
			Scene newScene = new Scene(newPage);

			Stage stage;
			if (event != null) {
				// if the event is not null, get the stage
				stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
			} else {
				// if the event is null, use the primary stage 
				stage = Main.getPrimaryStage();
			}

			stage.setScene(newScene);
			// set the title of the stage
			stage.setTitle(title);
			stage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
