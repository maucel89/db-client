package it.maucel89.dbclient.util;

import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

/**
 * @author Mauro Celani
 */
public abstract class AbsController {

	protected void showAlert(AlertType alertType, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(alertType.name());
		alert.setHeaderText(null);
		alert.setContentText(message);

		alert.showAndWait();
	}

}
