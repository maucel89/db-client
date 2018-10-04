package it.maucel89.dbclient;

import com.liferay.petra.string.StringPool;
import it.maucel89.dbclient.connection.ConnectionType;
import javafx.collections.ObservableList;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Mauro Celani
 */
public class DbConnection {

	private ConnectionType connType;
	private String name;
	private String hostname;
	private int port;
	private String schema;
	private String username;

	// TODO Encrypt
	private String password;

	public DbConnection(
		ConnectionType connType, String name, String hostname, int port,
		String schema, String username, String password) {

		this.connType = connType;
		this.name = name;
		this.hostname = hostname;
		this.port = port;
		this.schema = schema;
		this.username = username;
		this.password = password;
	}

	public ConnectionType getConnType() {
		return connType;
	}

	public String getName() {
		return name;
	}

	public String getHostname() {
		return hostname;
	}

	public int getPort() {
		return port;
	}

	public String getSchema() {
		return schema;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	@Override
	public String toString() {
		return getName();
	}

	public static void storeDbConncections(
		Map<ConnectionType, ObservableList<DbConnection>> connections) {

		JSONObject connectionsJSON = new JSONObject();

		for (ConnectionType connType : connections.keySet()) {

			JSONArray dbConnectionsJSON = new JSONArray();

			for (DbConnection dbConnection : connections.get(connType)) {
				dbConnectionsJSON.put(dbConnection.toJSON());
			}

			connectionsJSON.put(connType.name(), dbConnectionsJSON);
		}
		try {
			Files.write(
				Paths.get(DB_CONN_FILE), connectionsJSON.toString().getBytes());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Map<ConnectionType, List<DbConnection>> readDbConncections() {

		try {
			byte[] bytes = Files.readAllBytes(
				Paths.get(DB_CONN_FILE));

			String json = new String(bytes, StandardCharsets.UTF_8);

			JSONObject connectionsJSON = new JSONObject(json);

			Map<ConnectionType, List<DbConnection>> retMap = new HashMap<>();

			for (String connTypeStr : connectionsJSON.keySet()) {

				ConnectionType connType = ConnectionType.valueOf(connTypeStr);

				JSONArray dbConnectionsJSON =
					connectionsJSON.getJSONArray(connTypeStr);

				List<DbConnection> retList = new ArrayList<>(
					dbConnectionsJSON.length());

				for (Object connection : dbConnectionsJSON) {
					JSONObject jsonObject = (JSONObject) connection;
					retList.add(DbConnection.fromJSON(connType, jsonObject));
				}

				retMap.put(connType, retList);
			}

			return retMap;
		}
		catch (NoSuchFileException e) {
			// No file created yet
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return Collections.emptyMap();
	}

	private static DbConnection fromJSON(
		ConnectionType connType, JSONObject jsonObject) {

		return new DbConnection(
			connType,
			jsonObject.getString("name"),
			jsonObject.getString("hostname"),
			jsonObject.getInt("port"),
			jsonObject.getString("schema"),
			jsonObject.getString("username"),
			jsonObject.getString("password")
		);
	}

	private static final String DB_CONN_FILE =
		"." + File.separator + "conf.dat";

	private JSONObject toJSON() {
		JSONObject json = new JSONObject();

		json.put("name", getName());
		json.put("hostname", getHostname());
		json.put("port", getPort());
		json.put("schema",  getSchema());
		json.put("username",  getUsername());
		json.put("password", getPassword());

		return json;
	}

	public String getConnectionURL() {
		return connType.getConnectionURL(this);
	}

	public ResultSet getTables(DatabaseMetaData dbmd) throws SQLException {

		String[] types = { "TABLE" };

		switch (connType) {

			case Oracle:
				return dbmd.getTables(
					getSchema(), getUsername(), StringPool.PERCENT, types);

			default:
			case MySQL:
				return  dbmd.getTables(
					getSchema(), null, StringPool.PERCENT, types);
		}
	}
}
