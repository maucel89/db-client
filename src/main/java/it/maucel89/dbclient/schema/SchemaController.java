package it.maucel89.dbclient.schema;

import it.maucel89.dbclient.DbConnection;
import it.maucel89.dbclient.code.area.SQLCodeArea;
import it.maucel89.dbclient.util.AbsController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import org.fxmisc.flowless.VirtualizedScrollPane;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

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

	@FXML
	private TableView dataTableView;

	@FXML
	private SQLCodeArea sqlCodeArea;

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

				String schema = dbConnection.getSchema();
				String table = newValue;

				populateSchemaColumns(dbmd, schema, table);
				populateTableData(conn, schema, table);
				populateSQLCodeArea(table);
			}
		);

	}

	private void populateSQLCodeArea(String table) {
		sqlCodeArea.initCode("SELECT *\nFROM " + table + "\n;\n");
	}

	private void populateSchemaColumns(
		DatabaseMetaData dbmd, String schema, String table) {

		// EMPTY OLD DATA
		columnsTableView.getItems().clear();

		try {
			ResultSet rs = dbmd.getColumns(
				schema, null, table, "%");

			while (rs.next()) {
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

	private void populateTableData(
		Connection conn, String schema, String table) {

		// EMPTY OLD DATA
		dataTableView.getItems().clear();
		dataTableView.getColumns().clear();

		ObservableList<Column> columns = columnsTableView.getItems();

		try {

			String sql = "SELECT ";

			for (Column column : columns) {
				sql += column.getName() + ", ";
			}
			sql = sql.substring(0, sql.length() - 2);

			sql += " FROM " + table;

			// TODO Limit or Paginate

			PreparedStatement statement = conn.prepareStatement(sql);

			ResultSet rs = statement.executeQuery();


			for (int i = 0; i < rs.getMetaData().getColumnCount(); i++) {
				final int j = i;

				TableColumn tableColumn = new TableColumn(
					rs.getMetaData().getColumnName(i + 1));
				tableColumn.setCellValueFactory(
					(Callback<CellDataFeatures<ObservableList, String>,
						ObservableValue<String>>) param -> {

						String value = "";
						if (param.getValue().get(j) != null) {
							value = param.getValue().get(j).toString();
						}
						return new SimpleStringProperty(value);
					}

				);

				dataTableView.getColumns().addAll(tableColumn);
			}

			while (rs.next()) {
//				dataTableView.getItems().add(rs);
				ObservableList<String> row = FXCollections.observableArrayList();
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
					row.add(rs.getString(i));
				}
				//System.out.println(row);
				dataTableView.getItems().add(row);
			}

		}
		catch (SQLException ex) {
			// handle any errors
			System.out.println("SQLException: " + ex.getMessage());
			System.out.println("SQLState: " + ex.getSQLState());
			System.out.println("VendorError: " + ex.getErrorCode());

			showAlert(AlertType.ERROR, "Lettrua dati non riuscita!");
		}
	}


}
