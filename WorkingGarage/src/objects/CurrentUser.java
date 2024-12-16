package objects;

/**
 * represents the user of the app as an object
 * loads user details, email etc. 
 */

public class CurrentUser {
	/**
	 * default constructor
	 */
	public CurrentUser() {}
	
	private String email;
	private double accountBalance;
	

    /**
     * constructor for currentuser has parameters
     *
     * @param  email of the user
     * @param account balance of the user
     */
	
	public CurrentUser(String email, double accountBalance) {
		this.email = email;
		this.accountBalance = accountBalance;
		System.out.println("Test Constructor: email = " + email + ", balance = " + accountBalance);
	}
	

	
	/**method to delete currentUser when you log out
	 * 
	 */
	public void clear() {
		this.email = null;
		this.accountBalance = 0;
		System.out.println("clear() email = " + email + ", act bal = " + accountBalance);
	}
	
	/**
	 * getters and setters for currentUser
	 * @param email  accountBalance 
	 */
	
	public void setEmail(String email) {
		this.email = email;
		System.out.println("setEmail: " + email);
	}
	
	public void setaccountBalance(double accountBalance) {
		this.accountBalance = accountBalance;
		System.out.println("setBalance: " + accountBalance);
	}
	

	public String getEmail() {
		return email;
	}
	
	public double getAccountBalance() {
		return accountBalance;
	}
	
	

}


