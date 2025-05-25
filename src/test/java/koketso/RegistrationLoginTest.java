package koketso;

import koketso.RegistrationLogin;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class RegistrationLoginTest {

    // Test data
    private static final String VALID_USERNAME = "kyl_1";
    private static final String VALID_PASSWORD = "Passw0rd!";
    private static final String VALID_CELL_PHONE = "+27123456789";
    private static final String VALID_FIRST_NAME = "Koketso";
    private static final String VALID_LAST_NAME = "Modisella";
    private static final String INVALID_USERNAME = "kyle";
    private static final String INVALID_PASSWORD = "pass";
    private static final String INVALID_CELL_PHONE = "12345";
    private static final String INVALID_FIRST_NAME = "";
    private static final String INVALID_LAST_NAME = "";

    // Expected messages
    private static final String SUCCESS_MESSAGE = "Registration successful";
    private static final String ABORT_MESSAGE = "Registration aborted";
    private static final String USERNAME_ERROR = "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.";
    private static final String PASSWORD_ERROR = "Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
    private static final String CELLPHONE_ERROR = "Cellphone number is incorrectly formatted or does not contain an international code, please correct the number and try again.";
    private static final String FIRST_NAME_ERROR = "First name is invalid, please ensure it is not empty.";
    private static final String LAST_NAME_ERROR = "Last name is invalid, please ensure it is not empty.";
    private static final String LOGIN_SUCCESS = "Welcome Koketso Modisella,\nit is great to see you.";
    private static final String LOGIN_FAILURE = "Username & Password do not match our records, please try again.";

    // Helper method to register a valid user
    private RegistrationLogin registerValidUser() {
        RegistrationLogin regLogin = new RegistrationLogin();
        regLogin.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL_PHONE, VALID_FIRST_NAME, VALID_LAST_NAME);
        return regLogin;
    }

    // Registration Tests

    /**
     * Tests successful registration with all valid inputs.
     * Verifies the feedback string and stored user data.
     */
    @Test
    void testSuccessfulRegistration() {
        RegistrationLogin regLogin = new RegistrationLogin();
        String feedback = regLogin.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL_PHONE, VALID_FIRST_NAME, VALID_LAST_NAME);

        // Check feedback string
        assertTrue(feedback.contains("Username successfully captured"), "Feedback should confirm username capture");
        assertTrue(feedback.contains("Password successfully captured"), "Feedback should confirm password capture");
        assertTrue(feedback.contains("Cellphone number successfully captured"), "Feedback should confirm cellphone capture");
        assertTrue(feedback.contains("First name successfully captured"), "Feedback should confirm first name capture");
        assertTrue(feedback.contains("Last name successfully captured"), "Feedback should confirm last name capture");
        assertTrue(feedback.contains(SUCCESS_MESSAGE), "Feedback should confirm successful registration");

        // Check stored data
        assertEquals(VALID_USERNAME, regLogin.getUserName(), "Stored username should match input");
        assertEquals(VALID_PASSWORD, regLogin.getPassword(), "Stored password should match input");
        assertEquals(VALID_CELL_PHONE, regLogin.getCellPhoneNumber(), "Stored cellphone should match input");
        assertEquals(VALID_FIRST_NAME, regLogin.getFirstName(), "Stored first name should match input");
        assertEquals(VALID_LAST_NAME, regLogin.getLastName(), "Stored last name should match input");
    }

    /**
     * Tests failed registration with an invalid username.
     * Verifies error message and that no data is stored.
     */
    @Test
    void testFailedRegistrationInvalidUsername() {
        RegistrationLogin regLogin = new RegistrationLogin();
        String feedback = regLogin.registerUser(INVALID_USERNAME, VALID_PASSWORD, VALID_CELL_PHONE, VALID_FIRST_NAME, VALID_LAST_NAME);

        // Check feedback string
        assertTrue(feedback.contains(USERNAME_ERROR), "Feedback should contain username error");
        assertTrue(feedback.contains(ABORT_MESSAGE), "Feedback should indicate registration aborted");
        assertFalse(feedback.contains("Username successfully captured"), "Feedback should not confirm username capture");

        // Check no data is stored
        assertNull(regLogin.getUserName(), "Username should not be stored on failure");
    }

    /**
     * Tests failed registration with an invalid password.
     */
    @Test
    void testFailedRegistrationInvalidPassword() {
        RegistrationLogin regLogin = new RegistrationLogin();
        String feedback = regLogin.registerUser(VALID_USERNAME, INVALID_PASSWORD, VALID_CELL_PHONE, VALID_FIRST_NAME, VALID_LAST_NAME);

        assertTrue(feedback.contains(PASSWORD_ERROR), "Feedback should contain password error");
        assertTrue(feedback.contains(ABORT_MESSAGE), "Feedback should indicate registration aborted");
        assertNull(regLogin.getUserName(), "Username should not be stored on failure");
    }

    /**
     * Tests failed registration with an invalid cellphone number.
     */
    @Test
    void testFailedRegistrationInvalidCellPhone() {
        RegistrationLogin regLogin = new RegistrationLogin();
        String feedback = regLogin.registerUser(VALID_USERNAME, VALID_PASSWORD, INVALID_CELL_PHONE, VALID_FIRST_NAME, VALID_LAST_NAME);

        assertTrue(feedback.contains(CELLPHONE_ERROR), "Feedback should contain cellphone error");
        assertTrue(feedback.contains(ABORT_MESSAGE), "Feedback should indicate registration aborted");
        assertNull(regLogin.getUserName(), "Username should not be stored on failure");
    }

    /**
     * Tests failed registration with an invalid (empty) first name.
     */
    @Test
    void testFailedRegistrationInvalidFirstName() {
        RegistrationLogin regLogin = new RegistrationLogin();
        String feedback = regLogin.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL_PHONE, INVALID_FIRST_NAME, VALID_LAST_NAME);

        assertTrue(feedback.contains(FIRST_NAME_ERROR), "Feedback should contain first name error");
        assertTrue(feedback.contains(ABORT_MESSAGE), "Feedback should indicate registration aborted");
        assertNull(regLogin.getUserName(), "Username should not be stored on failure");
    }

    /**
     * Tests failed registration with an invalid (empty) last name.
     */
    @Test
    void testFailedRegistrationInvalidLastName() {
        RegistrationLogin regLogin = new RegistrationLogin();
        String feedback = regLogin.registerUser(VALID_USERNAME, VALID_PASSWORD, VALID_CELL_PHONE, VALID_FIRST_NAME, INVALID_LAST_NAME);

        assertTrue(feedback.contains(LAST_NAME_ERROR), "Feedback should contain last name error");
        assertTrue(feedback.contains(ABORT_MESSAGE), "Feedback should indicate registration aborted");
        assertNull(regLogin.getUserName(), "Username should not be stored on failure");
    }

    /**
     * Tests failed registration with all fields invalid.
     * Verifies all error messages are present.
     */
    @Test
    void testFailedRegistrationMultipleInvalidFields() {
        RegistrationLogin regLogin = new RegistrationLogin();
        String feedback = regLogin.registerUser(INVALID_USERNAME, INVALID_PASSWORD, INVALID_CELL_PHONE, INVALID_FIRST_NAME, INVALID_LAST_NAME);

        assertTrue(feedback.contains(USERNAME_ERROR), "Feedback should contain username error");
        assertTrue(feedback.contains(PASSWORD_ERROR), "Feedback should contain password error");
        assertTrue(feedback.contains(CELLPHONE_ERROR), "Feedback should contain cellphone error");
        assertTrue(feedback.contains(FIRST_NAME_ERROR), "Feedback should contain first name error");
        assertTrue(feedback.contains(LAST_NAME_ERROR), "Feedback should contain last name error");
        assertTrue(feedback.contains(ABORT_MESSAGE), "Feedback should indicate registration aborted");
        assertNull(regLogin.getUserName(), "Username should not be stored on failure");
    }

    // Login Tests

    /**
     * Tests successful login with valid credentials after registration.
     */
    @Test
    void testSuccessfulLogin() {
        RegistrationLogin regLogin = registerValidUser();
        boolean loginResult = regLogin.loginUser(VALID_USERNAME, VALID_PASSWORD);

        assertTrue(loginResult, "Login should succeed with valid credentials");
        assertEquals(LOGIN_SUCCESS, regLogin.returnLoginStatus(), "Login status should welcome the user");
    }

    /**
     * Tests failed login with an invalid username.
     */
    @Test
    void testFailedLoginInvalidUsername() {
        RegistrationLogin regLogin = registerValidUser();
        boolean loginResult = regLogin.loginUser(INVALID_USERNAME, VALID_PASSWORD);

        assertFalse(loginResult, "Login should fail with invalid username");
        assertEquals(LOGIN_FAILURE, regLogin.returnLoginStatus(), "Login status should indicate failure");
    }

    /**
     * Tests failed login with an invalid password.
     */
    @Test
    void testFailedLoginInvalidPassword() {
        RegistrationLogin regLogin = registerValidUser();
        boolean loginResult = regLogin.loginUser(VALID_USERNAME, INVALID_PASSWORD);

        assertFalse(loginResult, "Login should fail with invalid password");
        assertEquals(LOGIN_FAILURE, regLogin.returnLoginStatus(), "Login status should indicate failure");
    }

    /**
     * Tests failed login with both invalid username and password.
     */
    @Test
    void testFailedLoginBothInvalid() {
        RegistrationLogin regLogin = registerValidUser();
        boolean loginResult = regLogin.loginUser(INVALID_USERNAME, INVALID_PASSWORD);

        assertFalse(loginResult, "Login should fail with both invalid credentials");
        assertEquals(LOGIN_FAILURE, regLogin.returnLoginStatus(), "Login status should indicate failure");
    }

    /**
     * Tests login attempt without prior registration.
     */
    @Test
    void testLoginWithoutRegistration() {
        RegistrationLogin regLogin = new RegistrationLogin();
        boolean loginResult = regLogin.loginUser(VALID_USERNAME, VALID_PASSWORD);

        assertFalse(loginResult, "Login should fail without registration");
        assertEquals(LOGIN_FAILURE, regLogin.returnLoginStatus(), "Login status should indicate failure");
    }

    /**
     * Tests login with a null username.
     */
    @Test
    void testLoginWithNullUsername() {
        RegistrationLogin regLogin = registerValidUser();
        boolean loginResult = regLogin.loginUser(null, VALID_PASSWORD);

        assertFalse(loginResult, "Login should fail with null username");
        assertEquals(LOGIN_FAILURE, regLogin.returnLoginStatus(), "Login status should indicate failure");
    }

    /**
     * Tests login with a null password.
     */
    @Test
    void testLoginWithNullPassword() {
        RegistrationLogin regLogin = registerValidUser();
        boolean loginResult = regLogin.loginUser(VALID_USERNAME, null);

        assertFalse(loginResult, "Login should fail with null password");
        assertEquals(LOGIN_FAILURE, regLogin.returnLoginStatus(), "Login status should indicate failure");
    }

    /**
     * Tests login with both username and password as null.
     */
    @Test
    void testLoginWithBothNull() {
        RegistrationLogin regLogin = registerValidUser();
        boolean loginResult = regLogin.loginUser(null, null);

        assertFalse(loginResult, "Login should fail with both null credentials");
        assertEquals(LOGIN_FAILURE, regLogin.returnLoginStatus(), "Login status should indicate failure");
    }
}