package koketso;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class UserManager {
    private ArrayList<RegistrationLogin> users;
    private static final String USERS_FILE = "users.json";

    public UserManager() {
        users = new ArrayList<>();
        loadUsers();
    }

    /**
     * Registers a new user and saves to JSON.
     * @param regLogin The RegistrationLogin instance to register
     * @return Feedback from registration
     */
    public String registerUser(RegistrationLogin regLogin, String username, String password, 
                              String cellphone, String firstName, String lastName) {
        String feedback = regLogin.registerUser(username, password, cellphone, firstName, lastName);
        if (regLogin.isRegistered()) {
            users.add(regLogin);
            saveUsers();
        }
        return feedback;
    }

    /**
     * Finds a user by username.
     * @param username The username to search for
     * @return The RegistrationLogin instance or null if not found
     */
    public RegistrationLogin findUser(String username) {
        for (RegistrationLogin user : users) {
            if (user.getUserName() != null && user.getUserName().equals(username)) {
                return user;
            }
        }
        return null;
    }

    /**
     * Loads users from users.json into the users list.
     */
    @SuppressWarnings("unchecked")
    private void loadUsers() {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader(USERS_FILE)) {
            JSONArray usersArray = (JSONArray) parser.parse(reader);
            for (Object obj : usersArray) {
                JSONObject userJson = (JSONObject) obj;
                RegistrationLogin user = new RegistrationLogin();
                String feedback = user.registerUser(
                    (String) userJson.get("username"),
                    (String) userJson.get("password"),
                    (String) userJson.get("cellphone"),
                    (String) userJson.get("firstName"),
                    (String) userJson.get("lastName")
                );
                if (user.isRegistered()) {
                    users.add(user);
                }
            }
        } catch (Exception e) {
            // File doesn't exist or is empty, start with empty list
        }
    }

    /**
     * Saves users to users.json.
     */
    @SuppressWarnings("unchecked")
    private void saveUsers() {
        JSONArray usersArray = new JSONArray();
        for (RegistrationLogin user : users) {
            if (user.isRegistered()) {
                JSONObject userJson = new JSONObject();
                userJson.put("username", user.getUserName());
                userJson.put("password", user.getPassword());
                userJson.put("cellphone", user.getCellPhoneNumber());
                userJson.put("firstName", user.getFirstName());
                userJson.put("lastName", user.getLastName());
                usersArray.add(userJson);
            }
        }
        try (FileWriter file = new FileWriter(USERS_FILE)) {
            file.write(usersArray.toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}