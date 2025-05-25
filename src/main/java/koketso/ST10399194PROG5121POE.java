package koketso;

import javax.swing.JOptionPane;
import java.util.ArrayList;

public class ST10399194PROG5121POE 
{
    // Instance variables for managing users and messages
    private static UserManager userManager = new UserManager();           // Manages user registration and lookup
    private static RegistrationLogin currentUser = null;                  // Tracks the currently logged-in user
    private static ArrayList<Message> userMessages = new ArrayList<>();   // Stores the current user's messages

    public static void main(String[] args) {
        // Display a welcome message to the user
        JOptionPane.showMessageDialog(null, "Welcome to QuickChat by Koketso Modisella");

        // Main loop that runs the application until the user exits
        while (true) {
            // Set menu options based on login state
            String[] options = {"Register", "Login", "Exit"};
            if (currentUser != null) {
                options = new String[]{"Send Messages", "Show Inbox", "Show Sent Messages", "Logout", "Quit"};
            }

            // Show the menu and capture the user's choice
            int choice = JOptionPane.showOptionDialog(null, "Choose an action:", "QuickChat Menu",
                    JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

            // Handle actions when the user is not logged in
            if (currentUser == null) {
                if (choice == 0) { // Register
                    // Collect registration details from the user
                    String username = JOptionPane.showInputDialog("Enter your username:");
                    if (username == null) continue; // Skip if user cancels
                    String password = JOptionPane.showInputDialog("Enter your password:");
                    if (password == null) continue;
                    String cellphone = JOptionPane.showInputDialog("Enter your cellphone number:");
                    if (cellphone == null) continue;
                    String firstName = JOptionPane.showInputDialog("Enter your first name:");
                    if (firstName == null) continue;
                    String lastName = JOptionPane.showInputDialog("Enter your last name:");
                    if (lastName == null) continue;

                    // Register the new user and provide feedback
                    RegistrationLogin newUser = new RegistrationLogin();
                    String feedback = userManager.registerUser(newUser, username, password, cellphone, firstName, lastName);
                    JOptionPane.showMessageDialog(null, feedback);
                } 
                else if (choice == 1) { // Login
                    // Prompt for login credentials
                    String username = JOptionPane.showInputDialog("Enter your username to log in:");
                    if (username == null) continue;
                    String password = JOptionPane.showInputDialog("Enter your password to log in:");
                    if (password == null) continue;

                    // Attempt to log in the user
                    RegistrationLogin user = userManager.findUser(username);
                    if (user != null && user.loginUser(username, password)) {
                        currentUser = user;
                        // Load the user's messages from storage
                        userMessages = MessageManager.loadUserMessages(currentUser.getCellPhoneNumber());
                        JOptionPane.showMessageDialog(null, user.returnLoginStatus());
                    } else {
                        JOptionPane.showMessageDialog(null, "Login failed: Invalid credentials.");
                    }
                } 
                else { // Exit
                    break; // Exit the application
                }
            } 
            // Handle actions when the user is logged in
            else {
                if (choice == 0) { // Send Messages
                    // Allow the user to send multiple messages
                    boolean sending = true;
                    while (sending) {
                        // Get the number of messages to send
                        String numMessagesStr = JOptionPane.showInputDialog("How many messages do you want to send?");
                        if (numMessagesStr == null) break;

                        int numMessages;
                        try {
                            numMessages = Integer.parseInt(numMessagesStr);
                            if (numMessages < 0) throw new NumberFormatException();
                        } catch (NumberFormatException e) {
                            JOptionPane.showMessageDialog(null, "Invalid number. Please enter a non-negative integer.");
                            continue;
                        }

                        // Process each message individually
                        for (int i = 0; i < numMessages; i++) {
                            String recipient = JOptionPane.showInputDialog("Enter recipient's cell number:");
                            if (recipient == null) continue;

                            String payload = JOptionPane.showInputDialog("Enter message payload (max 250 chars):");
                            if (payload == null) continue;

                            // Ensure the message payload is within the 250-character limit
                            while (payload.length() > 250) {
                                JOptionPane.showMessageDialog(null, "Message too long.");
                                payload = JOptionPane.showInputDialog("Enter message payload (max 250 chars):");
                                if (payload == null) break;
                            }
                            if (payload == null) continue;

                            // Create a new message object
                            Message message = new Message(currentUser.getCellPhoneNumber(), recipient, payload);

                            // Prompt the user for an action on the message
                            String[] actions = {"Send", "Disregard", "Store for later"};
                            int actionChoice = JOptionPane.showOptionDialog(null, "What do you want to do with this message?",
                                    "Message Action", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, actions, actions[0]);

                            if (actionChoice == 0) { // Send the message
                                String result = message.sentMessage();
                                JOptionPane.showMessageDialog(null, result);
                                if (result.equals("Message sent successfully")) {
                                    userMessages.add(message);
                                    message.storeMessage();
                                    JOptionPane.showMessageDialog(null, message.printMessages());
                                }
                            } 
                            else if (actionChoice == 1) { // Disregard the message
                                JOptionPane.showMessageDialog(null, "Message disregarded.");
                            } 
                            else if (actionChoice == 2) { // Store the message for later
                                message.storeMessage();
                                userMessages.add(message);
                                JOptionPane.showMessageDialog(null, "Message stored for later.");
                            }
                        }
                        // Display the total number of messages sent
                        JOptionPane.showMessageDialog(null, "Total messages sent: " + Message.returnTotalMessages());
                        break;
                    }
                } 
                else if (choice == 1) { // Show Inbox
                    // Collect messages where the current user is the recipient
                    ArrayList<Message> inboxMessages = new ArrayList<>();
                    for (Message msg : userMessages) {
                        if (msg.getRecipient().equals(currentUser.getCellPhoneNumber())) {
                            inboxMessages.add(msg);
                        }
                    }

                    // Display the inbox or a message if it's empty
                    if (inboxMessages.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No messages in your inbox.");
                    } else {
                        StringBuilder messageList = new StringBuilder("Your inbox:\n");
                        for (Message msg : inboxMessages) {
                            String status = msg.getIndex() == 0 ? "Stored" : "Received";
                            messageList.append(String.format("%s: ID: %s, From: %s, To: %s, Payload: %s, Hash: %s, Index: %d\n",
                                    status, msg.getId(), msg.getSender(), msg.getRecipient(), msg.getPayload(), msg.getHash(), msg.getIndex()));
                        }
                        JOptionPane.showMessageDialog(null, messageList.toString());
                    }
                } 
                else if (choice == 2) { // Show Sent Messages
                    // Collect messages sent by the current user
                    ArrayList<Message> sentMessages = new ArrayList<>();
                    for (Message msg : userMessages) {
                        if (msg.getSender().equals(currentUser.getCellPhoneNumber())) {
                            sentMessages.add(msg);
                        }
                    }

                    // Display sent messages or a message if there are none
                    if (sentMessages.isEmpty()) {
                        JOptionPane.showMessageDialog(null, "No sent or stored messages.");
                    } else {
                        StringBuilder messageList = new StringBuilder("Your sent messages:\n");
                        for (Message msg : sentMessages) {
                            String status = msg.getIndex() == 0 ? "Stored" : "Sent";
                            messageList.append(String.format("%s: ID: %s, From: %s, To: %s, Payload: %s, Hash: %s, Index: %d\n",
                                    status, msg.getId(), msg.getSender(), msg.getRecipient(), msg.getPayload(), msg.getHash(), msg.getIndex()));
                        }
                        JOptionPane.showMessageDialog(null, messageList.toString());
                    }
                } 
                else if (choice == 3) { // Logout
                    // Clear the current user and their messages
                    currentUser = null;
                    userMessages.clear();
                    JOptionPane.showMessageDialog(null, "Logged out successfully.");
                } 
                else { // Quit
                    break; // Exit the application
                }
            }
        }
    }
}