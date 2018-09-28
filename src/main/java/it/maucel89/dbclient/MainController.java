package it.maucel89.dbclient;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.util.Pair;

import java.util.Optional;

/**
 * @author Mauro Celani
 */
public class MainController {

	public MainController() {
		connectionsViewData.add(new DbConnection("Pippo"));
	}

	@FXML
	private Button addConnectionButton;

	@FXML
	private ListView<DbConnection> connectionsListView;
	private ObservableList<DbConnection> connectionsViewData =
		FXCollections.observableArrayList();


	@FXML
	private void initialize() {

		addConnectionButton.setOnAction(event -> {
//			connectionsViewData.add(new DbConnection("ppp"));

			AddConnectionDialog dialog = new AddConnectionDialog();

			Optional<String> result = dialog.showAndWait();

			result.ifPresent(connectionName -> {
				connectionsViewData.add(new DbConnection(connectionName));
			});
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
//			outputTextArea.appendText("ListView Selection Changed (selected: " + newValue.toString() + ")\n");
		});

	}

}
