package it.maucel89.dbclient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

/**
 * @author Mauro Celani
 */
public class MainController {

	public MainController() {
		DbConnection.readDbConncections().forEach(connectionsViewData::add);
	}

	@FXML
	private Button addConnectionButton;

	@FXML
	private Button removeConnectionButton;

	@FXML
	private Button connectButton;

	@FXML
	private ListView<DbConnection> connectionsListView;
	private ObservableList<DbConnection> connectionsViewData =
		FXCollections.observableArrayList();

	@FXML
	private void initialize() {

		addConnectionButton.setOnAction(event -> {
			AddConnectionDialog dialog = new AddConnectionDialog();

			Optional<DbConnection> result = dialog.showAndWait();

			result.ifPresent(dbConnection -> {
				connectionsViewData.add(dbConnection);
				DbConnection.storeDbConncections(connectionsViewData);
			});

		});

		connectButton.setOnAction(event -> {

			ObservableList<DbConnection> selectedItems =
				connectionsListView.getSelectionModel().getSelectedItems();

			if (selectedItems.isEmpty() || selectedItems.size() > 1) {
				showAlert(
					AlertType.WARNING, "Devi selezionare una connessione!");
			}
			else {
				// TRY TO CONNECT

				DbConnection dbConnection = selectedItems.get(0);

				try (Connection conn = DriverManager.getConnection(
						dbConnection.getConnectionURL())) {

					// Do something with the Connection
					showAlert(AlertType.INFORMATION, "Connessione riuscita!");

				}
				catch (SQLException ex) {
					// handle any errors
					System.out.println("SQLException: " + ex.getMessage());
					System.out.println("SQLState: " + ex.getSQLState());
					System.out.println("VendorError: " + ex.getErrorCode());

					showAlert(AlertType.ERROR, "Connessione non riuscita!");
				}
			}
		});

		removeConnectionButton.setOnAction(event -> {

			ObservableList<DbConnection> selectedItems =
				connectionsListView.getSelectionModel().getSelectedItems();

			if (selectedItems.isEmpty() || selectedItems.size() > 1) {
				showAlert(
					AlertType.WARNING, "Devi selezionare una connessione!");
			}
			else {
				// REMOVE SELECTED CONNECTION

				DbConnection dbConnection = selectedItems.get(0);

				connectionsViewData.remove(dbConnection);

				DbConnection.storeDbConncections(connectionsViewData);
			}
		});


		// Init ListView.
		connectionsListView.setItems(connectionsViewData);
//		connectionsListView.setCellFactory((list) -> {
//			return new ListCell<DbConnection>() {
//				@Override
//				protected void updateItem(DbConnection item, boolean empty) {
//					super.updateItem(item, empty);
//
//					if (item == null || empty) {
//						setText(null);
//					} else {
//						setText(item.getName());
//					}
//				}
//			};
//		});

		// Handle ListView selection changes.
		connectionsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
		});

	}

	private void showAlert(AlertType alertType, String message) {
		Alert alert = new Alert(alertType);
		alert.setTitle(alertType.name());
		alert.setHeaderText(null);
		alert.setContentText(message);

		alert.showAndWait();
	}

}
