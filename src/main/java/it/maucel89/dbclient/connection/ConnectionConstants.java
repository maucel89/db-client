package it.maucel89.dbclient.connection;

/**
 * @author Mauro Celani
 */
public interface ConnectionConstants {

	// TODO Check if is an Oracle SID or Service
	public static final String ORACLE_CONNECTION_URL_PATTERN =
		"jdbc:oracle:thin:{0}/{1}@{2}:{3}:{4}";

	public static final String MYSQL_CONNECTION_URL_PATTERN =
		"jdbc:mysql://{0}:{1}/{2}?user={3}&password={4}&serverTimezone=UTC&useSSL=false";

}
