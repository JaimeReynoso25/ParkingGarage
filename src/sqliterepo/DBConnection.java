package sqliterepo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * class that manages database connection to a SQLite database. static method to
 * get a connection to databaase
 */

public class DBConnection {

	private static Connection connection;

	/**
	 * default constructor
	 */
	public DBConnection() {
	}

	/**
	 * returns a connection to the SQLite database. if the connection is already
	 * open, it reuses the existing connection,otherwise, it will init a new
	 * connection.
	 *
	 * @return the connection to the SQLite database
	 * @throws ClassNotFoundException if SQLite JDBC driver is not found
	 * @throws SQLException
	 */

	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		if (connection == null || connection.isClosed()) {
			try {
				Class.forName("org.sqlite.JDBC");
				String url = "jdbc:sqlite:garage_system.db";
				connection = DriverManager.getConnection(url);
				System.out.println("Connection to SQLite has been established.");
			} catch (SQLException e) {
				System.out.println("SQL Exception: " + e.getMessage());
			} catch (ClassNotFoundException e) {
				System.out.println("JDBC Class not found: " + e.getMessage());
			}
		}
		return connection;
	}

}
