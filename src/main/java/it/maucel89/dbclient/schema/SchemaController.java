package it.maucel89.dbclient.schema;

import it.maucel89.dbclient.DbConnection;
import it.maucel89.dbclient.util.AbsController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Mauro Celani
 */
public class SchemaController extends AbsController {

	@FXML
	private TextField filterTableTextField;

	@FXML
	private ListView<String> tablesListView;
	private ObservableList<String> tablesViewData =
		FXCollections.observableArrayList();
	private FilteredList<String> filteredTablesList =
		new FilteredList<>(tablesViewData, data -> true);

	@FXML
	private TableView<Column> columnsTableView;

	public void initData(DbConnection dbConnection) throws SQLException {

		Connection conn = DriverManager.getConnection(
			dbConnection.getConnectionURL());

		DatabaseMetaData dbmd = conn.getMetaData();

		try {
			String[] types = {"TABLE"};

			ResultSet rs = dbmd.getTables(
				dbConnection.getSchema(), null, "%", types);

			while (rs.next()) {
				tablesViewData.add(rs.getString("TABLE_NAME"));
			}

		}
		catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());

			showAlert(AlertType.ERROR, "Lettura tabelle non riuscita!");
		}

		// Init ListView.
		tablesListView.setItems(filteredTablesList);

		filterTableTextField.textProperty().addListener(
			((observable, oldValue, newValue) -> {
				filteredTablesList.setPredicate(data -> {
					if (newValue == null || newValue.isEmpty()){
						return true;
					}
					return data.toLowerCase().contains(newValue.toLowerCase());
				});
			})
		);

		// Handle ListView selection changes.
		tablesListView.getSelectionModel().selectedItemProperty().addListener(
			(observable, oldValue, newValue) -> {
//				System.out.print(newValue);

				columnsTableView.getItems().removeAll(
					columnsTableView.getItems());

				try {
					ResultSet rs = dbmd.getColumns(
						dbConnection.getSchema(), null, newValue, "%");

					while (rs.next()) {
//						tablesViewData.add(rs.getString("TABLE_NAME"));
//						System.out.println(rs.getString(4));
//						System.out.println(rs.getString("COLUMN_NAME"));
						columnsTableView.getItems().add(Column.fromRow(rs));
					}
				}
				catch (SQLException ex) {
					// handle any errors
					System.out.println("SQLException: " + ex.getMessage());
					System.out.println("SQLState: " + ex.getSQLState());
					System.out.println("VendorError: " + ex.getErrorCode());

					showAlert(AlertType.ERROR, "Lettrua colonne non riuscita!");
				}
			}
		);
	}


}
