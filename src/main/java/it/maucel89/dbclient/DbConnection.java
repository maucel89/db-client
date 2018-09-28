package it.maucel89.dbclient;

import org.json.JSONObject;

import java.util.List;

/**
 * @author Mauro Celani
 */
public class DbConnection {

	private String name;

	public DbConnection(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

	public static void storeDbConncections(List<DbConnection> connections) {

		for (DbConnection connection : connections) {
			JSONObject connectionJSON = connection.toJSON();
		}

	}

	private JSONObject toJSON() {
		JSONObject new JSONObject();
	}

}
