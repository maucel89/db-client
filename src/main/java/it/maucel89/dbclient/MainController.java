package it.maucel89.dbclient;

import it.maucel89.dbclient.connection.ConnectionType;
import it.maucel89.dbclient.schema.SchemaController;
import it.maucel89.dbclient.util.AbsController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @author Mauro Celani
 */
public class MainController extends AbsController {

	@FXML
	private Button addConnectionButton;

	@FXML
	private Button removeConnectionButton;

	@FXML
	private Button connectButton;

	@FXML
	private TabPane connectionsTabPane;
	private Map<ConnectionType, ListView>
		connectionsListView = new HashMap<>();
	private Map<ConnectionType, ObservableList<DbConnection>>
		connectionsListViewData = new HashMap<>();

	@FXML
	private void initialize() {

		initConnectionTabPane();


		// ADD NEW DATABASE CONNECTION

		addConnectionButton.setOnAction(event -> {

			ConnectionType connType = getConnectionType();

			Optional<DbConnection> result =
				connType.getAddDialog().showAndWait();

			result.ifPresent(dbConnection -> {

				connectionsListViewData.get(connType).add(dbConnection);

				DbConnection.storeDbConncections(connectionsListViewData);
			});

		});


		// REMOVE DATABASE CONNECTION

		removeConnectionButton.setOnAction(event -> {

			ConnectionType connType = getConnectionType();

			ObservableList<DbConnection> selectedItems =
				connectionsListView.get(connType)
					.getSelectionModel().getSelectedItems();

			if (selectedItems.isEmpty() || selectedItems.size() > 1) {
				showAlert(
					AlertType.WARNING, "Devi selezionare una connessione!");
			}
			else {
				// REMOVE SELECTED CONNECTION

				DbConnection dbConnection = selectedItems.get(0);

				connectionsListViewData.get(connType).remove(dbConnection);

				DbConnection.storeDbConncections(connectionsListViewData);
			}
		});


		// DO CONNECTION

		connectButton.setOnAction(event -> {

			ConnectionType connType = getConnectionType();

			ObservableList<DbConnection> selectedItems =
				connectionsListView.get(connType).getSelectionModel().getSelectedItems();

			if (selectedItems.isEmpty() || selectedItems.size() > 1) {
				showAlert(
					AlertType.WARNING, "Devi selezionare una connessione!");
			}
			else {
				// TRY TO CONNECT

				DbConnection dbConnection = selectedItems.get(0);

				try {
					// Do something with the Connection
//					showAlert(AlertType.INFORMATION, "Connessione riuscita!");

					try {
						FXMLLoader loader = new FXMLLoader(
							getClass().getResource("schema/schema.fxml"));
						Stage stage = new Stage();
						stage.setTitle("Schema Viewer");

						Scene scene = new Scene(loader.load());
						SchemaController controller =
							loader.<SchemaController>getController();

						controller.initData(scene, dbConnection);

						stage.setScene(scene);
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

	}

	private ConnectionType getConnectionType() {
		String tabId = connectionsTabPane.getSelectionModel()
			.getSelectedItem().getId();

		return ConnectionType.valueOf(tabId);
	}

	private void initConnectionTabPane() {

		Map<ConnectionType, List<DbConnection>> savedConnectionsMap =
			DbConnection.readDbConncections();

		for (ConnectionType connType : ConnectionType.values()) {

			Tab connTypeTab = new Tab(connType.name());

			connTypeTab.setId(connType.name());
			connTypeTab.setClosable(false);

			connectionsListViewData.put(
				connType, FXCollections.observableArrayList());

			ObservableList<DbConnection> dbConnectionViewData =
				connectionsListViewData.get(connType);

			ListView connectionListView = new ListView(dbConnectionViewData);

			connectionsListView.put(
				connType, connectionListView);

			if (savedConnectionsMap.containsKey(connType)) {
				dbConnectionViewData.addAll(
					savedConnectionsMap.get(connType));
			}

			connTypeTab.setContent(connectionListView);

			connectionsTabPane.getTabs().add(connTypeTab);

		}

	}

}
