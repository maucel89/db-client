package it.maucel89.dbclient.connection.dialog;

import it.maucel89.dbclient.DbConnection;
import it.maucel89.dbclient.connection.ConnectionType;

/**
 * @author Mauro Celani
 */
public class MysqlConnectionDialog extends BaseConnectionDialog {

	public MysqlConnectionDialog(DbConnection selectedConnection) {
		super(selectedConnection, ConnectionType.MySQL);
	}

}
