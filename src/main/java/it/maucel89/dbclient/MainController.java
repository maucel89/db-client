package it.maucel89.dbclient;

import it.maucel89.dbclient.schema.SchemaController;
import it.maucel89.dbclient.util.AbsController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Optional;

/**
 * @author Mauro Celani
 */
public class MainController extends AbsController {

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
//					showAlert(AlertType.INFORMATION, "Connessione riuscita!");

					try {
						FXMLLoader loader = new FXMLLoader(
							getClass().getResource("schema/schema.fxml"));
						Stage stage = new Stage();
						stage.setTitle("Schema Viewer");
						stage.setScene(new Scene(loader.load()));

						SchemaController controller =
							loader.<SchemaController>getController();

						controller.initData(dbConnection, conn);

						stage.show();

						// Hide this current window
						((Node)(event.getSource())).getScene().getWindow()
							.hide();
					}
					catch (IOException e) {
						e.printStackTrace();
					}

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

}
