package koketso;

import java.util.regex.Pattern;

public class RegistrationLogin {
    private String storedUserName, storedPassword, storedCellPhoneNumber, storedFirstName, storedLastName;
    private boolean accessGranted;

    public String registerUser(String newUserName, String newPassword, String newCellPhoneNumber, 
                              String newFirstName, String newLastName) {
        String feedback = "";
        boolean isValid = true;

        if (checkUserName(newUserName)) {
            feedback += "Username successfully captured\n";
        } else {
            feedback += "Username is not correctly formatted, please ensure that your username contains an underscore and is no more than five characters in length.\n";
            isValid = false;
        }

        if (checkPasswordComplexity(newPassword)) {
            feedback += "Password successfully captured\n";
        } else {
            feedback += "Password is not correctly formatted, please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.\n";
            isValid = false;
        }

        if (checkCellPhoneNumber(newCellPhoneNumber)) {
            feedback += "Cellphone number successfully captured\n";
        } else {
            feedback += "Cellphone number is incorrectly formatted or does not contain an international code, please correct the number and try again.\n";
            isValid = false;
        }

        if (isNameValid(newFirstName)) {
            feedback += "First name successfully captured\n";
        } else {
            feedback += "First name is invalid, please ensure it is not empty.\n";
            isValid = false;
        }

        if (isNameValid(newLastName)) {
            feedback += "Last name successfully captured\n";
        } else {
            feedback += "Last name is invalid, please ensure it is not empty.\n";
            isValid = false;
        }

        if (isValid) {
            this.storedUserName = newUserName;
            this.storedPassword = newPassword;
            this.storedCellPhoneNumber = newCellPhoneNumber;
            this.storedFirstName = newFirstName;
            this.storedLastName = newLastName;
            feedback += "Registration successful";
        } else {
            feedback += "Registration aborted";
        }

        return feedback;
    }

    /**
     * Attempts to log in the user with the provided credentials.
     * @param userNameAttempt The username to check
     * @param passwordAttempt The password to check
     * @return true if login is successful, false otherwise
     */
    public boolean loginUser(String userNameAttempt, String passwordAttempt) {
        accessGranted = userNameAttempt != null && passwordAttempt != null &&
                        userNameAttempt.equals(storedUserName) &&
                        passwordAttempt.equals(storedPassword);
        return accessGranted;
    }

    /**
     * Returns a message based on the current login status.
     * @return A welcome message if logged in, or an error message if not
     */
    public String returnLoginStatus() {
        return accessGranted ?
                String.format("Welcome %s %s,\nit is great to see you.", storedFirstName, storedLastName) :
                "Username & Password do not match our records, please try again.";
    }

    /**
     * Checks if a user is registered.
     * @return true if a user is registered, false otherwise
     */
    public boolean isRegistered() {
        return storedUserName != null;
    }

    // Getter methods
    public String getUserName() {
        return storedUserName;
    }

    public String getPassword() {
        return storedPassword;
    }

    public String getCellPhoneNumber() {
        return storedCellPhoneNumber;
    }

    public String getFirstName() {
        return storedFirstName;
    }

    public String getLastName() {
        return storedLastName;
    }

    // Private validation methods
    private boolean checkUserName(String userName) {
        return userName != null && userName.length() <= 5 && userName.contains("_");
    }

    private boolean checkPasswordComplexity(String password) {
        if (password == null || password.length() < 8) return false;
        return password.matches(".*[A-Z].*") &&
               password.matches(".*[0-9].*") &&
               password.matches(".*[!@#$%^&*()].*");
    }

    private boolean checkCellPhoneNumber(String cellPhoneNumber) {
        return cellPhoneNumber != null && Pattern.matches("^\\+27[0-9]{9}$", cellPhoneNumber);
    }

    private boolean isNameValid(String name) {
        return name != null && !name.trim().isEmpty() && name.matches("^[a-zA-Z]+$");
    }
}