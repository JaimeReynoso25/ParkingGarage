package sqliterepo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import objects.CurrentUser;
import objects.Reservation;

/**
 * repository class for managing database operations
 * methods for user registration, authentication, reservation management,
 * and updating the garage table.
 */

public class SQLRepository {
	/**
	 * Constructor to pass the database connection
	 * @param connection
	 */
	
	public SQLRepository(Connection connection) {
		this.connection = connection;
	}
	
	private static Connection connection;

	  /**
     * registers a new user in the database.
     *
     * @param email    
     * @param password 
     * @return a message stating whether registration was successful or not
     * @throws ClassNotFoundException if the SQLite JDBC driver is not found
     */
	public static String registerUser(String email, String password) throws ClassNotFoundException {
		
        try {
            String sql = "INSERT INTO users (email, password) VALUES (?, ?)";
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            pstmt.executeUpdate();

            return "Registration successful!";
        } catch (SQLException e) {
            e.printStackTrace();
            return "Error: " + e.getMessage(); 
        }		
	}
	
	  /**
     * checks if an email is unique in the database
     *
     * @param email
     * @return true for unique email, false otherwise
     * @throws ClassNotFoundException if the SQLite JDBC driver is not found
     */
	
	public static boolean isEmailUnique(String email) throws ClassNotFoundException {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
            	//return true if 0
                return rs.getInt(1) == 0; 
            }
        } catch (SQLException e) {
            System.out.println("Error checking email uniqueness: " + e.getMessage());
        }
        return false; // If there's an error, email is not unique
    } 
	
    /**
     * authenticates a user by verifying their email and password.
     *
     * @param email    
     * @param password 
     * @return true for suuccessful authentication, false otherwise
     * @throws ClassNotFoundException if the SQLite JDBC driver is not found
     */
	
    public static boolean authenticateUser(String email, String password) throws ClassNotFoundException {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try {
            PreparedStatement pstmt = connection.prepareStatement(sql);
            pstmt.setString(1, email);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();

            // If a result is returned, authentication is successful
            return rs.next(); 
        } catch (SQLException e) {
            e.printStackTrace();
            return false; 
        }
    }
    
    
    /**
     * retrieves user details for a given email and sets the current user.
     *
     * @param email the user's email
     * @return a CurrentUser object containing user details, or null 
     * @throws ClassNotFoundException if the SQLite JDBC driver is not found
     */
    
    public static CurrentUser setUserDetails(String email) throws ClassNotFoundException {
        String query = "SELECT email, balance FROM users WHERE email = ?";  

        try {
        	PreparedStatement pstmt = connection.prepareStatement(query);
        	pstmt.setString(1, email); 
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String userEmail = rs.getString("email");  
                double accountBalance = rs.getDouble("balance");

                // return a populated CurrentUser object with email and balance
                return new CurrentUser(userEmail, accountBalance);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // If no user is found or there's an error
        return null;
    }
    
    /**
     * updates the user's account balance by adding or subtracting funds.
     *
     * @param currentUser 
     * @param addedFunds  can be pos or neg
     * @return the updated CurrentUser object
     * @throws ClassNotFoundException if the SQLite JDBC driver is not found
     */
    
    public static CurrentUser updateUserBalance(CurrentUser currentUser, double addedFunds) throws ClassNotFoundException {
    	
    	String query = "UPDATE users SET balance = balance + ? WHERE email = ?";
        try {
        	PreparedStatement pstmt = connection.prepareStatement(query);
            
            pstmt.setDouble(1, addedFunds);
            pstmt.setString(2, currentUser.getEmail());
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return currentUser = setUserDetails(currentUser.getEmail());
    }
    
    /**
     * check if spot is available
     * 
     * 
     * @return true if available, false if not 
     */
    
    public boolean spotAvailable() {
    	
        String query = "SELECT 1 FROM Reservations WHERE available = 1 LIMIT 1";
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            
//             there is an available spot
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    /**
     * get the id of an available parking spot.
     *
     * @return the id of the available spot, or null if no spot
     */
    
    public Integer getAvailableSpotId() {
        String query = "SELECT id FROM Reservations WHERE available = 1 LIMIT 1";
        try (PreparedStatement pstmt = connection.prepareStatement(query);
             ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt("id"); 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; 
    }
    
    /**
     * makes a new reservation in the database.
     *
     * @param reserve 
     * @throws ClassNotFoundException if the SQLite JDBC driver is not found
     */
    
    public void makeReservation(Reservation reserve) throws ClassNotFoundException {
    	String query = "INSERT INTO Reservations (email, license_plate, start_date, end_date, alreadyParked) VALUES (?, ?, ?, ?, ?)";
    	try  {
	    	PreparedStatement pstmt = connection.prepareStatement(query);
	    	
	    	pstmt.setString(1, reserve.getEmail());
	    	pstmt.setString(2, reserve.getLicenseplate());
	    	pstmt.setString(3, reserve.getStart_date().toString());
	        pstmt.setString(4, reserve.getEnd_date().toString());
	        pstmt.setInt(5, 0);
	        
	        pstmt.executeUpdate();
    	} catch (SQLException e) {
	        e.printStackTrace();
	    }
    }
    
    /**
     * checks if a license plate is unique in the reservations table.
     *
     * @param licensePlate
     * @return true if unique. else false
     */
    
    public boolean checkLicenseUnique(String licensePlate) {
        String query = "SELECT COUNT(*) FROM Reservations WHERE license_plate = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(query)) {
            pstmt.setString(1, licensePlate);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
            	// license plate is unique
                return rs.getInt(1) == 0; 
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
     // not unique
        return false; 
    }

    /**
     * finds the parking spot for a license plate.
     *
     * @param license 
     * @return the parking spot number
     */
    
    public static Integer findByLicensePlate(String license) {  	
    	
    	  String query = "SELECT parkingSpot FROM garage WHERE license_plate = ?";
    	    try (PreparedStatement pstmt = connection.prepareStatement(query)) {
    	    	// set the license plate value in the query
    	        pstmt.setString(1, license); 
    	        ResultSet rs = pstmt.executeQuery();
    	        
    	        if (rs.next()) {
    	            return rs.getInt("parkingSpot"); 
    	        }
    	    } catch (SQLException e) {
    	        e.printStackTrace();
    	    } 
    	    return null; 
    	
    }
    
    
    /**
     *method that checks the reservation table, and updates the garage table 
     *with the users who have a spot reserved for the day
     */
    
    public void updateGarageTable () {
    	LocalDate currentDate = LocalDate.now();
    	// Define the date format you expect
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    	
    	
    	try {
            // returns all reservations
            String query = "SELECT * FROM reservations";
            Statement statement = connection.createStatement();
            ResultSet reservations = statement.executeQuery(query);
            
            while (reservations.next()) {
            	
            	
                String licensePlate = reservations.getString("license_plate");
                String email = reservations.getString("email");
                LocalDate startDate = LocalDate.parse(reservations.getString("start_date"), formatter);
                LocalDate endDate = LocalDate.parse(reservations.getString("end_date"), formatter);
                int alreadyParked = reservations.getInt("alreadyParked");
                
             // check if reservation is expired i.e. endDate < currentDate
                if (endDate.isBefore(currentDate)) {
                    // update the garage to free the spot
                    String clearGarageQuery = "UPDATE garage SET occupied = 0, email = NULL, license_plate = NULL WHERE license_plate = ?";
                    PreparedStatement clearGarageStmt = connection.prepareStatement(clearGarageQuery);
                    clearGarageStmt.setString(1, licensePlate);
                    clearGarageStmt.executeUpdate();
                    
                    // delete the expired reservation
                    String deleteReservationQuery = "DELETE FROM reservations WHERE license_plate = ?";
                    PreparedStatement deleteReservationStmt = connection.prepareStatement(deleteReservationQuery);
                    deleteReservationStmt.setString(1, licensePlate);
                    deleteReservationStmt.executeUpdate();

                }
                //If the reservation is for today and alreadyParked == 0,
                //meaning that the car still has not been added to the Garage table
                else if ((startDate.isEqual(currentDate) || startDate.isBefore(currentDate)) && alreadyParked == 0) {
                	
                    // update the reservation to indicate the car is now parked
                    String updateReservationQuery = "UPDATE reservations SET alreadyParked = 1 WHERE license_plate = ?";
                    PreparedStatement updateReservationStmt = connection.prepareStatement(updateReservationQuery);
                    updateReservationStmt.setString(1, licensePlate);
                    updateReservationStmt.executeUpdate();

                    // find the first available spot in the garage and update it with user info
                    String findAvailableSpotQuery = "SELECT * FROM garage WHERE occupied = 0 LIMIT 1";
                    Statement availableSpotStmt = connection.createStatement();
                    ResultSet availableSpot = availableSpotStmt.executeQuery(findAvailableSpotQuery);

                    if (availableSpot.next()) {
                        int parkingSpot = availableSpot.getInt("parkingSpot"); // 
                        String updateGarageQuery = "UPDATE garage SET occupied = 1, email = ?, license_plate = ? WHERE parkingSpot = ?";
                        PreparedStatement updateGarageStmt = connection.prepareStatement(updateGarageQuery);
                        updateGarageStmt.setString(1, email);
                        updateGarageStmt.setString(2, licensePlate);
                        updateGarageStmt.setInt(3, parkingSpot);
                        updateGarageStmt.executeUpdate();

                    } else {
                        System.out.println("No available spots in the garage.");
                    }
                }
            }
            
    	} catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    
    /*
     *  This method automatically loads up a list of all of the user's
     *  reservations in order by their start date
     */
    public List<Reservation> loadReservations(CurrentUser currentUser) throws SQLException {
    	String email = currentUser.getEmail();
    	List<Reservation> reservationList = new ArrayList<>();
    	
    	//pulls all of a user's reservations based off of email with an sql query
    	PreparedStatement stmt = connection.prepareStatement("SELECT start_date, end_date, license_plate FROM reservations " +
    			                                             "WHERE email = ? ORDER BY start_date, end_date");
    	stmt.setString(1, email);
    	ResultSet rs = stmt.executeQuery();
    	
    	//grabs the three parameters that will be needed for the List
    	while (rs.next()) {
            String startDateString = rs.getString("start_date");
            LocalDate startDate = LocalDate.parse(startDateString);
            
            String endDateString = rs.getString("end_date");
            LocalDate endDate = LocalDate.parse(endDateString);
            
            String licenseplate = rs.getString("license_plate");
           
            //the string can be blank since it won't be needed in the toString() method
            //and we already verified that they are from the currentUser's email
            Reservation reservation = new Reservation(" ", licenseplate, startDate, endDate);
            
            reservationList.add(reservation); 
        }
    	return reservationList;
    }

	public void deleteReservation(String licensePlate) throws SQLException {
		
		//checks to see if the car is already parked in the garage. If it is, remove it
		String clearGarageQuery = "UPDATE garage SET occupied = 0, email = NULL, license_plate = NULL WHERE license_plate = ?";
        PreparedStatement clearGarageStmt = connection.prepareStatement(clearGarageQuery);
        clearGarageStmt.setString(1, licensePlate);
        clearGarageStmt.executeUpdate();
		
		// delete the user's reservation
        String deleteReservationQuery = "DELETE FROM reservations WHERE license_plate = ?";
        PreparedStatement deleteReservationStmt = connection.prepareStatement(deleteReservationQuery);
        deleteReservationStmt.setString(1, licensePlate);
        deleteReservationStmt.executeUpdate();
		
	}
    
    
}
	
	