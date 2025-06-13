package koketso;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RegistrationLoginTest {

    // Test data as per POE instructions
    private static final String VALID_USERNAME = "kyl_1";
    private static final String VALID_PASSWORD = "Passw0rd!";
    private static final String VALID_CELL_PHONE = "+27123456789";
    private static final String VALID_FIRST_NAME = "Koketso";
    private static final String VALID_LAST_NAME = "Modiselle"; // Corrected spelling
    private static final String INVALID_USERNAME = "kyle";
    private static final String INVALID_PASSWORD = "password";
    private static final String INVALID_CELL_PHONE = "12345";

    // Expected messages
    private static final String SUCCESS_MESSAGE = "Registration successful";
    private static final String ABORT_MESSAGE = "Registration aborted";
    private static final String USERNAME_ERROR = "Username is not correctly formatted";
    private static final String PASSWORD_ERROR = "Password is not correctly formatted";
    private static final String LOGIN_SUCCESS = "Welcome Koketso Modiselle,\nit is great to see you.";
    private static final String LOGIN_FAILURE = "Username & Password do not match our records, please try again.";

    // Helper method to create a valid user for testing login
    private RegistrationLogin registerValidUser() {
        RegistrationLogin regLogin = new RegistrationLogin();
        regLogin.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL_PHONE, VALID_FIRST_NAME, VALID_LAST_NAME);
        return regLogin;
    }

    // --- Registration Tests ---

    @Test
    void testSuccessfulRegistration() {
        RegistrationLogin regLogin = new RegistrationLogin();
        String feedback = regLogin.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL_PHONE, VALID_FIRST_NAME, VALID_LAST_NAME);
        assertTrue(feedback.contains(SUCCESS_MESSAGE), "Feedback should confirm successful registration");
        assertEquals(VALID_USERNAME, regLogin.getUserName());
    }

    @Test
    void testFailedRegistrationInvalidUsername() {
        RegistrationLogin regLogin = new RegistrationLogin();
        String feedback = regLogin.registerUser(INVALID_USERNAME, VALID_PASSWORD, VALID_CELL_PHONE, VALID_FIRST_NAME, VALID_LAST_NAME);
        assertTrue(feedback.contains(USERNAME_ERROR), "Feedback should contain username error");
        assertNull(regLogin.getUserName(), "Username should not be stored on failure");
    }

    @Test
    void testFailedRegistrationInvalidPassword() {
        RegistrationLogin regLogin = new RegistrationLogin();
        String feedback = regLogin.registerUser(VALID_USERNAME, INVALID_PASSWORD, VALID_CELL_PHONE, VALID_FIRST_NAME, VALID_LAST_NAME);
        assertTrue(feedback.contains(PASSWORD_ERROR), "Feedback should contain password error");
        assertNull(regLogin.getUserName(), "Username should not be stored on failure");
    }

    // --- Login Tests ---

    @Test
    void testSuccessfulLogin() {
        RegistrationLogin regLogin = registerValidUser();
        boolean loginResult = regLogin.loginUser(VALID_USERNAME, VALID_PASSWORD);
        assertTrue(loginResult, "Login should succeed with valid credentials");
        assertEquals(LOGIN_SUCCESS, regLogin.returnLoginStatus(), "Login status should welcome the user");
    }

    @Test
    void testFailedLoginInvalidUsername() {
        RegistrationLogin regLogin = registerValidUser();
        boolean loginResult = regLogin.loginUser(INVALID_USERNAME, VALID_PASSWORD);
        assertFalse(loginResult, "Login should fail with invalid username");
        assertEquals(LOGIN_FAILURE, regLogin.returnLoginStatus(), "Login status should indicate failure");
    }

    @Test
    void testFailedLoginInvalidPassword() {
        RegistrationLogin regLogin = registerValidUser();
        boolean loginResult = regLogin.loginUser(VALID_USERNAME, INVALID_PASSWORD);
        assertFalse(loginResult, "Login should fail with invalid password");
        assertEquals(LOGIN_FAILURE, regLogin.returnLoginStatus(), "Login status should indicate failure");
    }

    @Test
    void testLoginWithoutRegistration() {
        RegistrationLogin regLogin = new RegistrationLogin();
        boolean loginResult = regLogin.loginUser(VALID_USERNAME, VALID_PASSWORD);
        assertFalse(loginResult, "Login should fail if user object is not pre-populated with registered data");
    }
}
