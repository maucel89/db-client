package it.maucel89.dbclient.connection.dialog;

import com.liferay.gradle.util.Validator;
import it.maucel89.dbclient.DbConnection;
import it.maucel89.dbclient.connection.ConnectionType;
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
public abstract class BaseConnectionDialog extends Dialog {

	public BaseConnectionDialog(
	        DbConnection selectedConnection, ConnectionType connType) {

	    DialogMode dialogMode = (selectedConnection != null)
                ? DialogMode.EDIT
                : DialogMode.ADD;

		setTitle(dialogMode.getTitle() + connType);

		Integer defaultPort = connType.getDefaultPort();

		GridPane grid = new GridPane();

		grid.setHgap(10);
		grid.setVgap(10);

		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField connectionName = addField(
			grid, new TextField(), "Connection Name", "Connection Name:");

		TextField hostname = addField(
			grid, new TextField(), "localhost", "Hostname:");

		TextField port = addField(
			grid, new TextField(), defaultPort.toString(), "Port:");

		TextField schema = addField(
			grid, new TextField(), "Schema", "Schema:");

		TextField username = addField(
			grid, new TextField(), "Username", "Username:");

		TextField password = addField(
			grid, new PasswordField(), "Password", "Password:");

		if (dialogMode == DialogMode.EDIT) {
		    connectionName.setText(selectedConnection.getName());
		    hostname.setText(selectedConnection.getHostname());
		    port.setText(String.valueOf(selectedConnection.getPort()));
		    schema.setText(selectedConnection.getSchema());
		    username.setText(selectedConnection.getUsername());
		    password.setText(selectedConnection.getPassword());
        }

		getDialogPane().setContent(grid);

		ButtonType loginButtonType = new ButtonType(
			dialogMode.getButtonLabel(), ButtonData.OK_DONE);

		getDialogPane().getButtonTypes().addAll(
			loginButtonType, ButtonType.CANCEL);

		// Convert the result to a username-password-pair when the
		// login button is clicked.
		setResultConverter(dialogButton -> {
			if (dialogButton == loginButtonType) {

				String portStr = port.getText();
				int portN = defaultPort;
				if (!portStr.isEmpty()) {
					try {
						portN = Integer.parseInt(portStr);
					}
					catch (NumberFormatException e) {
						// Save default database port
					}
				}

				String hostnameStr = Validator.isNotNull(hostname.getText())
					? hostname.getText()
					: DEFAULT_HOST;

				return new DbConnection(
					connType, connectionName.getText(), hostnameStr, portN,
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

	private static final String DEFAULT_HOST = "localhost";

}
