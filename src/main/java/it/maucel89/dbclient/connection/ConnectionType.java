package it.maucel89.dbclient.connection;

import it.maucel89.dbclient.DbConnection;
import it.maucel89.dbclient.connection.dialog.MysqlConnectionDialog;
import it.maucel89.dbclient.connection.dialog.OracleConnectionDialog;
import javafx.scene.control.Dialog;

import java.text.MessageFormat;

/**
 * @author Mauro Celani
 */
public enum ConnectionType implements ConnectionConstants {

	MySQL(3306, MYSQL_CONNECTION_URL_PATTERN),

	Oracle(1521, ORACLE_CONNECTION_URL_PATTERN);

	private int defaultPort;
	private String connectionURL;

	ConnectionType(int defaultPort, String connectionURL) {
		this.defaultPort = defaultPort;
		this.connectionURL = connectionURL;
	}

	public int getDefaultPort() {
		return defaultPort;
	}

    public Dialog getDialog() {
	    return getDialog(null);
    }

    public Dialog getDialog(DbConnection dbConnection) {

		switch (this) {

			case Oracle:
				return new OracleConnectionDialog(dbConnection);

			default:
			case MySQL:
				return new MysqlConnectionDialog(dbConnection);
		}
	}

	public String getConnectionURL(DbConnection dbConnection) {

		// TODO @see String.format to create url from connectionURL

		switch (this) {

			// https://docs.oracle.com/cd/E11882_01/appdev.112/e13995/oracle/jdbc/OracleDriver.html
			case Oracle:
				return MessageFormat.format(
					connectionURL, dbConnection.getUsername(),
					dbConnection.getPassword(), dbConnection.getHostname(),
					String.valueOf(dbConnection.getPort()),
					dbConnection.getSchema());

			default:
			case MySQL:
				return MessageFormat.format(
					connectionURL, dbConnection.getHostname(),
					String.valueOf(dbConnection.getPort()),
					dbConnection.getSchema(), dbConnection.getUsername(),
					dbConnection.getPassword());
		}

	}
}
