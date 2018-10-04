package it.maucel89.dbclient.schema;

import com.liferay.petra.string.StringPool;
import it.maucel89.dbclient.DbConnection;
import it.maucel89.dbclient.code.area.SQLCodeArea;
import it.maucel89.dbclient.util.AbsController;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Mauro Celani
 */
public class SchemaController extends AbsController {

	private static final String SELECT_AND_SPACE = "SELECT" + StringPool.SPACE;
	private static final String FROM_AND_SPACE = "FROM" + StringPool.SPACE;

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

	@FXML
    private Button executeQueryButton;

    @FXML
    private TableView queryTableView;

	public void initData(
			Scene scene, DbConnection dbConnection)
		throws SQLException {

		Connection conn = DriverManager.getConnection(
			dbConnection.getConnectionURL());

		DatabaseMetaData dbmd = conn.getMetaData();

		initLateralTablesList(dbmd, dbConnection);

		sqlCodeArea.initScene(scene, tablesViewData);

		// Init ListView.
		tablesListView.setItems(filteredTablesList);

		filterTableTextField.textProperty().addListener(
			((observable, oldValue, newValue) -> {

				tablesListView.getSelectionModel().clearSelection();

				filteredTablesList.setPredicate(data -> {
					if (newValue == null || newValue.isEmpty()) {
						return true;
					}
					return data.toLowerCase().contains(newValue.toLowerCase());
				});
			})
		);

		// Handle ListView selection changes.
		tablesListView.getSelectionModel().selectedItemProperty().addListener(
			(observable, oldTable, newTable) -> {

				String schema = dbConnection.getSchema();

				List<Column> oldColumns = new ArrayList<>(
					columnsTableView.getItems());

				populateSchemaColumns(dbmd, schema, newTable);

				// SAVE COLUMNS BEFORE POPULATE DATAS
				List<Column> newColumns = new ArrayList<>(
					columnsTableView.getItems());

				populateTableData(conn, newTable);
				populateSQLCodeArea(newTable, oldColumns, newColumns);
			}
		);


		// SQL QUERY EXECUTION

        executeQueryButton.setOnAction(event -> {
            populateTableView(conn, queryTableView, sqlCodeArea.getQuery());
        });

		sqlCodeArea.setOnKeyPressed(event -> {
			if (event.isControlDown() && event.getCode() == KeyCode.ENTER) {
				populateTableView(conn, queryTableView, sqlCodeArea.getQuery());
			}
		});

	}

	private void initLateralTablesList(
		DatabaseMetaData dbmd, DbConnection dbConnection) {

		try {
			ResultSet rs = dbConnection.getTables(dbmd);

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
	}

	private void populateSQLCodeArea(
		String newTable, List<Column> oldColumns, List<Column> newColumns) {

		sqlCodeArea.clear();

		if (newTable != null) {
			sqlCodeArea.initCode(
				SELECT_AND_SPACE + StringPool.STAR + StringPool.NEW_LINE +
				FROM_AND_SPACE + newTable + StringPool.NEW_LINE +
				StringPool.SEMICOLON + StringPool.NEW_LINE);
		}

		sqlCodeArea.initAutoCompletePopup(
			Column.toNameList(oldColumns), Column.toNameList(newColumns));
	}

	private void populateSchemaColumns(
		DatabaseMetaData dbmd, String schema, String table) {

		// EMPTY OLD DATA
		columnsTableView.getItems().clear();

		try {
			if (table != null) {
				ResultSet rs = dbmd.getColumns(
					schema, null, table, StringPool.PERCENT);

				while (rs.next()) {
					columnsTableView.getItems().add(Column.fromRow(rs));
				}
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
		Connection conn, String table) {

        ObservableList<Column> columns = columnsTableView.getItems();

        String sql = null;

		if (table != null) {
			sql = SELECT_AND_SPACE;

			for (Column column : columns) {
				sql += column.getName() + StringPool.COMMA_AND_SPACE;
			}
			sql = sql.substring(0, sql.length() - 2);

			sql += StringPool.SPACE + FROM_AND_SPACE + table;

			// TODO Limit or Paginate
		}

	    populateTableView(conn, dataTableView, sql);
	}

    private void populateTableView(
        Connection conn, TableView tableView, String sql) {

        // EMPTY OLD DATA
        tableView.getItems().clear();
        tableView.getColumns().clear();

        try {
			if (sql != null) {

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

					tableView.getColumns().addAll(tableColumn);
				}

				while (rs.next()) {
					//				tableView.getItems().add(rs);
					ObservableList<String> row =
						FXCollections.observableArrayList();
					for (int i = 1; i <= rs.getMetaData().getColumnCount();
						 i++) {
						row.add(rs.getString(i));
					}
					//System.out.println(row);
					tableView.getItems().add(row);
				}
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
