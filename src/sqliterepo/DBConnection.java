package sqliterepo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * class that manages database connection to a SQLite database. static method to
 * get a connection to database
 */

public class DBConnection {

	private static Connection connection;

	//returns a connection to the SQLite database. 
	public static Connection getConnection() throws ClassNotFoundException, SQLException {
		if (connection == null || connection.isClosed()) {
			connection = DriverManager.getConnection("jdbc:sqlite:garage_system.db");
		}
		return connection;
	}

}
