package it.maucel89.dbclient.connection;

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
	
}
