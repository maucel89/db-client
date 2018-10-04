package it.maucel89.dbclient.connection.dialog;

import it.maucel89.dbclient.connection.ConnectionType;

/**
 * @author Mauro Celani
 */
public class MysqlConnectionDialog extends BaseConnectionDialog {

	public MysqlConnectionDialog(DialogMode dialogMode) {
		super(dialogMode, ConnectionType.MySQL);
	}

}
