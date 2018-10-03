package it.maucel89.dbclient;

import com.liferay.petra.string.StringPool;
import it.maucel89.dbclient.connection.ConnectionType;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Mauro Celani
 */
public class DbConnection {

	private String name;
	private String hostname;
	private int port;
	private String schema;
	private String username;

	// TODO Encrypt
	private String password;

	public DbConnection(
		String name, String hostname, int port, String schema, String username,
		String password) {

		this.name = name;
		this.hostname = hostname;
		this.port = port;
		this.schema = schema;
		this.username = username;
		this.password = password;
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
		ConnectionType connType, List<DbConnection> connections) {

		JSONArray connectionsJSON = new JSONArray();

		for (DbConnection connection : connections) {
			connectionsJSON.put(connection.toJSON());
		}

		try {
			Files.write(
				Paths.get(DB_CONN_FILE), connectionsJSON.toString().getBytes());
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static List<DbConnection> readDbConncections(
		ConnectionType connType) {

		try {
			byte[] bytes = Files.readAllBytes(
				Paths.get(DB_CONN_FILE));

			String json = new String(bytes, StandardCharsets.UTF_8);

			JSONArray connectionsJSON = new JSONArray(json);

			List<DbConnection> retList = new ArrayList<>(
				connectionsJSON.length());

			for (Object connection : connectionsJSON) {
				JSONObject jsonObject = (JSONObject) connection;
				retList.add(DbConnection.fromJSON(jsonObject));
			}

			return retList;
		}
		catch (NoSuchFileException e) {
			// No file created yet
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return Collections.emptyList();
	}

	private static DbConnection fromJSON(JSONObject jsonObject) {

		return new DbConnection(
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

	public String getMySQLConnectionURL() {
		return "jdbc" + StringPool.COLON + "mysql" + StringPool.COLON +
			   StringPool.DOUBLE_SLASH + hostname + StringPool.COLON + port +
			   StringPool.SLASH + schema + StringPool.QUESTION +
			   "user" + StringPool.EQUAL + username + StringPool.AMPERSAND +
			   "password" + StringPool.EQUAL + password + StringPool.AMPERSAND +
			   "serverTimezone" + StringPool.EQUAL + StringPool.UTC +
			   StringPool.AMPERSAND + "useSSL" + StringPool.EQUAL + "false";
	}
}
