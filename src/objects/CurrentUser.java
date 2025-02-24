package objects;

/**
 * represents the user of the app as an object
 * loads user details, email etc. 
 */

public class CurrentUser {

    /**
     * constructor for currentuser has parameters
     *
     * @param  email of the user
     * @param account balance of the user
     */
	
	public CurrentUser(String email, double accountBalance) {
		this.email = email;
		this.accountBalance = accountBalance;
	}
	
	private String email;
	private double accountBalance;

	
	/**method to delete currentUser when you log out
	 * 
	 */
	public void clear() {
		this.email = null;
		this.accountBalance = 0;
	}
	
	/**
	 * getters and setters for currentUser
	 * @param email  accountBalance 
	 */
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public void setaccountBalance(double accountBalance) {
		this.accountBalance = accountBalance;
	}
	

	public String getEmail() {
		return email;
	}
	
	public double getAccountBalance() {
		return accountBalance;
	}
}