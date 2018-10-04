package it.maucel89.dbclient.connection.dialog;

import it.maucel89.dbclient.connection.ConnectionType;

/**
 * @author Mauro Celani
 */
public class OracleConnectionDialog extends BaseConnectionDialog {

	public OracleConnectionDialog(DialogMode dialogMode) {
		super(dialogMode, ConnectionType.Oracle);
	}

}
