package it.maucel89.dbclient.connection;

import javafx.scene.control.Dialog;
import javafx.stage.Stage;

/**
 * @author Mauro Celani
 */
public enum ConnectionType {

	MySQL(3306),
	Oracle(1521);

	private int defaultPort;

	ConnectionType(int defaultPort) {
		this.defaultPort = defaultPort;
	}

	public int getDefaultPort() {
		return defaultPort;
	}

	public Dialog getAddDialog() {

		switch (this) {

			case Oracle:
				return new AddOracleConnectionDialog();

			default:
			case MySQL:
				return new AddMysqlConnectionDialog();
		}
	}
}
