package objects;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class CurrentUserTest {

    private CurrentUser currentUser;

    @BeforeEach
    public void setup() {
        // initialize CurrentUser object before each test
        currentUser = new CurrentUser("test@testme.com", 100.0);
    }
//TC001
    @Test
    public void testConstructor() {
        // assert initial state
        assertEquals("test@testme.com", currentUser.getEmail(), "Email should match the constructor argument");
        assertEquals(100.0, currentUser.getAccountBalance(), 0.01, "Account balance should match the constructor argument");
    }
//TC002
    @Test
    public void testSetEmail() {
        // change email
        currentUser.setEmail("newtest@testme.com");

        // assert updated email
        assertEquals("newtest@testme.com", currentUser.getEmail(), "Email should be updated");
    }
//TC003
    @Test
    public void testSetAccountBalance() {
        // change balance
        currentUser.setaccountBalance(200.0);

        //assert updated state
        assertEquals(200.0, currentUser.getAccountBalance(), 0.01, "Account balance should be updated");
    }
//TC004
    @Test
    public void testClear() {
        // clear the user
        currentUser.clear();

        // assert cleared state
        assertNull(currentUser.getEmail(), "Email should be null after clearing");
        assertEquals(0.0, currentUser.getAccountBalance(), 0.01, "Account balance should be reset to 0 after clearing");
    }
}
