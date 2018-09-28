package it.maucel89.dbclient.schema;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Mauro Celani
 */
public class Column {

	private String name;
	private int ordinalPosition;
	private String type;
	private String collation;
	private boolean unsigned;
	private boolean nullable;
	private String defaultValue;

	public Column(
		String name, int ordinalPosition, String type,
		String collation, boolean unsigned, boolean nullable,
		String defaultValue) {

		this.name = name;
		this.ordinalPosition = ordinalPosition;
		this.type = type;
		this.collation = collation;
		this.unsigned = unsigned;
		this.nullable = nullable;
		this.defaultValue = defaultValue;
	}

	public static Column fromRow(ResultSet rs) throws SQLException {

		String columnType = rs.getString("TYPE_NAME");

		boolean unsigned = false;
		if (columnType.contains("unsigned")) {
			unsigned = true;
			columnType = columnType.split("\n")[0];
		}

		return new Column(
			rs.getString("COLUMN_NAME"),
			rs.getInt("ORDINAL_POSITION"),
			columnType,
//			rs.getString("COLLATION_NAME"),
			"",
			unsigned,
			Boolean.parseBoolean(rs.getString("IS_NULLABLE")), //YES/NO
			rs.getString("COLUMN_DEF")
		);
	}

	public String getName() {
		return name;
	}

	public int getOrdinalPosition() {
		return ordinalPosition;
	}

	public String getCollation() {
		return collation;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public String getType() {
		return type;
	}

	public boolean isNullable() {
		return nullable;
	}

	public boolean isUnsigned() {
		return unsigned;
	}

	public String toString() {
		return getName();
	}

}
