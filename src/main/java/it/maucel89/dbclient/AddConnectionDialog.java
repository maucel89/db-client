package it.maucel89.dbclient;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

/**
 * @author Mauro Celani
 */
public class AddConnectionDialog extends Dialog {

	public AddConnectionDialog() {
		setTitle("Aggiungi Connessione");

		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField connectionName = addField(
			grid, new TextField(), "Connection Name", "Connection Name:");

		TextField hostname = addField(
			grid, new TextField(), "Hostname", "Hostname:");

		TextField port = addField(
			grid, new TextField(), "3306", "Port:");

		TextField schema = addField(
			grid, new TextField(), "Schema", "Schema:");

		TextField username = addField(
			grid, new TextField(), "Username", "Username:");

		TextField password = addField(
			grid, new PasswordField(), "Password", "Password:");

		getDialogPane().setContent(grid);

		ButtonType loginButtonType = new ButtonType(
			"Aggiungi", ButtonData.OK_DONE);

		getDialogPane().getButtonTypes().addAll(
			loginButtonType, ButtonType.CANCEL);

		// Convert the result to a username-password-pair when the
		// login button is clicked.
		setResultConverter(dialogButton -> {
			if (dialogButton == loginButtonType) {

				String portStr = port.getText();
				int portN = 3306;
				if (!portStr.isEmpty()) {
					try {
						portN = Integer.parseInt(portStr);
					}
					catch (NumberFormatException e) {
						// Save default 3306 port
					}
				}

				return new DbConnection(
					connectionName.getText(), hostname.getText(), portN,
					schema.getText(), username.getText(), password.getText());
			}
			return null;
		});

		Platform.runLater(() -> connectionName.requestFocus());
	}

	private TextField addField(
		GridPane grid, TextField textField, String placeholder, String label) {

		textField.setPromptText(placeholder);
		grid.add(new Label(label), 0, buttonCount);
		grid.add(textField, 1, buttonCount);

		buttonCount++;

		return textField;
	}

	private int buttonCount = 0;

}
