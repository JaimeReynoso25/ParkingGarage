package objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ReservationTest {

    private Reservation reservation;

    @BeforeEach
    public void setup() {
        // Initialize a new Reservation object before each test
        reservation = new Reservation();
    }
//TC001
    @Test
    public void testSetAndGetEmail() {
        // Set email
        reservation.setEmail("test@testme.com");

        // Validate email
        assertEquals("test@testme.com", reservation.getEmail(), "Email should be set correctly");
    }
//TC002
    @Test
    public void testSetAndGetLicenseplate() {
        // Set license plate
        reservation.setLicenseplate("ABC123");

        // Validate license plate
        assertEquals("ABC123", reservation.getLicenseplate(), "License plate should be set correctly");
    }
//TC003
    @Test
    public void testSetAndGetStartDate() {
        // Set start date
        LocalDate startDate = LocalDate.of(2024, 11, 24);
        reservation.setStart_date(startDate);

        // Validate start date
        assertEquals(startDate, reservation.getStart_date(), "Start date should be set correctly");
    }
//TC004
    @Test
    public void testSetAndGetEndDate() {
        // Set end date
        LocalDate endDate = LocalDate.of(2024, 11, 30);
        reservation.setEnd_date(endDate);

        // Validate end date
        assertEquals(endDate, reservation.getEnd_date(), "End date should be set correctly");
    }
//TC005
    @Test
    public void testSetAndGetAlreadyParked() {
        // set alreadyParked to true
        reservation.setAlreadyParked(true);

        // assert alreadyParked
        assertTrue(reservation.alreadyParked(), "alreadyParked should return true");

        // set alreadyParked to false
        reservation.setAlreadyParked(false);

        // assert that it's false: alreadyParked
        assertFalse(reservation.alreadyParked(), "alreadyParked should return false");
    }
}
