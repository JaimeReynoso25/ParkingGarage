package objects;

import java.time.LocalDate;

/**
 * represents a reservation object for the application
 */

public class Reservation {
	
	private String email;
	private String licenseplate;
	private LocalDate start_date;
	private LocalDate end_date;
	private boolean alreadyParked;
	
	/**
	 * getters and setters
	 * @return requested fields
	 */
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getLicenseplate() {
		return licenseplate;
	}
	public void setLicenseplate(String licenseplate) {
		this.licenseplate = licenseplate;
	}
	public LocalDate getStart_date() {
		return start_date;
	}
	public void setStart_date(LocalDate start_date) {
		this.start_date = start_date;
	}
	public LocalDate getEnd_date() {
		return end_date;
	}
	public void setEnd_date(LocalDate end_date) {
		this.end_date = end_date;
	}

	public boolean alreadyParked() {
		return alreadyParked;
	}
	public void setAlreadyParked(boolean alreadyParked) {
		this.alreadyParked = alreadyParked;
	}
}