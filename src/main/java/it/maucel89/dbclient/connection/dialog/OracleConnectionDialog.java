package it.maucel89.dbclient.connection.dialog;

import it.maucel89.dbclient.DbConnection;
import it.maucel89.dbclient.connection.ConnectionType;

/**
 * @author Mauro Celani
 */
public class OracleConnectionDialog extends BaseConnectionDialog {

	public OracleConnectionDialog(DbConnection selectedConnection) {
		super(selectedConnection, ConnectionType.Oracle);
	}

}
