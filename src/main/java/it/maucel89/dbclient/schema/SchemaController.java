package it.maucel89.dbclient.schema;

import it.maucel89.dbclient.DbConnection;
import it.maucel89.dbclient.util.AbsController;
import javafx.scene.control.Alert.AlertType;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Mauro Celani
 */
public class SchemaController extends AbsController {

	public void initData(DbConnection dbConnection, Connection conn) {
		try {
			DatabaseMetaData dbmd = conn.getMetaData();

			String[] types = {"TABLE"};

			ResultSet rs = dbmd.getTables(
				dbConnection.getSchema(), null, "%", types);

			while (rs.next()) {
				System.out.println(rs.getString("TABLE_NAME"));
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


}
