package it.maucel89.dbclient.connection;

import com.liferay.petra.string.StringPool;
import it.maucel89.dbclient.DbConnection;
import it.maucel89.dbclient.connection.dialog.DialogMode;
import it.maucel89.dbclient.connection.dialog.MysqlConnectionDialog;
import it.maucel89.dbclient.connection.dialog.OracleConnectionDialog;
import javafx.scene.control.Dialog;

/**
 * @author Mauro Celani
 */
public enum ConnectionType {

	MySQL(
		3306, ""),

	Oracle(
		1521, "");

	private int defaultPort;
	private String connectionURL;

	ConnectionType(int defaultPort, String connectionURL) {
		this.defaultPort = defaultPort;
		this.connectionURL = connectionURL;
	}

	public int getDefaultPort() {
		return defaultPort;
	}

	public Dialog getDialog(DialogMode dialogMode) {

		switch (this) {

			case Oracle:
				return new OracleConnectionDialog(dialogMode);

			default:
			case MySQL:
				return new MysqlConnectionDialog(dialogMode);
		}
	}

	public String getConnectionURL(DbConnection dbConnection) {

		switch (this) {

			// TODO Check if is an Oracle SID or Service
			// https://docs.oracle.com/cd/E11882_01/appdev.112/e13995/oracle/jdbc/OracleDriver.html
			case Oracle:
				return "jdbc:oracle:thin:" + dbConnection.getUsername() +
					   StringPool.SLASH + dbConnection.getPassword() +
					   StringPool.AT + dbConnection.getHostname() +
					   StringPool.COLON + dbConnection.getPort() +
					   StringPool.COLON + dbConnection.getSchema();

			default:
			case MySQL:
				return "jdbc" + StringPool.COLON + "mysql" + StringPool.COLON +
					   StringPool.DOUBLE_SLASH + dbConnection.getHostname() +
					   StringPool.COLON + dbConnection.getPort() +
					   StringPool.SLASH + dbConnection.getSchema() +
					   StringPool.QUESTION + "user" + StringPool.EQUAL +
					   dbConnection.getUsername() + StringPool.AMPERSAND +
					   "password" + StringPool.EQUAL +
					   dbConnection.getPassword() + StringPool.AMPERSAND +
					   "serverTimezone" + StringPool.EQUAL + StringPool.UTC +
					   StringPool.AMPERSAND + "useSSL" + StringPool.EQUAL +
					   "false";
		}

	}
}
