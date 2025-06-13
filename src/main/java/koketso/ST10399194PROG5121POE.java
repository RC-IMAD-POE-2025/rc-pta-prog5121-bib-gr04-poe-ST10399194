package koketso;

import javax.swing.JOptionPane;
import java.util.ArrayList;

public class ST10399194PROG5121POE {
    
    private static UserManager userManager = new UserManager();
    private static RegistrationLogin currentUser = null;
    private static ArrayList<Message> allMessages = new ArrayList<>();

    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null, "Welcome to QuickChat by Koketso Modiselle");
        allMessages = MessageManager.loadAllMessages();
        while (true) {
            showMainMenu();
        }
    }

    public static void showMainMenu() {
        String[] options;
        if (currentUser == null) {
            options = new String[]{"Register", "Login", "Exit"};
        } else {
            options = new String[]{"Send Message", "View Inbox", "View Sent Reports", "Logout", "Quit"};
        }

        int choice = JOptionPane.showOptionDialog(null, "Choose an action:", "QuickChat Menu",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);

        if (currentUser == null) {
            handleLoggedOutUser(choice);
        } else {
            handleLoggedInUser(choice);
        }
    }

    public static void handleLoggedOutUser(int choice) {
        switch (choice) {
            case 0: doRegistration(); break;
            case 1: doLogin(); break;
            default: System.exit(0);
        }
    }

    public static void handleLoggedInUser(int choice) {
        switch (choice) {
            case 0: doSendMessage(); break;
            case 1: showInbox(); break;
            case 2: showSentReportsMenu(); break;
            case 3: doLogout(); break;
            default: System.exit(0);
        }
    }

    public static void showInbox() {
        StringBuilder inboxContent = new StringBuilder("--- Your Inbox ---\n");
        ArrayList<Message> userInbox = new ArrayList<>();

        for (Message msg : allMessages) {
            if (msg.getRecipient().equals(currentUser.getCellPhoneNumber())) {
                userInbox.add(msg);
                if (!msg.isReceived()) {
                    msg.setReceived(true);
                    msg.storeMessage();
                }
            }
        }

        if (userInbox.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Your inbox is empty.");
            return;
        }

        for (Message msg : userInbox) {
            String status = msg.isRead() ? "(READ)" : "(DELIVERED)";
            inboxContent.append(status).append(" From: ").append(msg.getSender()).append("\n");
            inboxContent.append("    ID: ").append(msg.getId()).append("\n\n");
        }
        
        inboxContent.append("\nEnter a message ID to read it, or click Cancel to go back.");
        String idToRead = JOptionPane.showInputDialog(null, inboxContent.toString());

        if (idToRead != null && !idToRead.trim().isEmpty()) {
            Message messageToRead = null;
            for (Message msg : userInbox) {
                if (msg.getId().equals(idToRead.trim())) {
                    messageToRead = msg;
                    break;
                }
            }

            if (messageToRead != null) {
                messageToRead.setRead(true);
                messageToRead.storeMessage();
                JOptionPane.showMessageDialog(null, "From: " + messageToRead.getSender() + "\n\n" + messageToRead.getPayload());
            } else {
                JOptionPane.showMessageDialog(null, "Message with that ID not found in your inbox.");
            }
        }
    }

    public static void showSentReportsMenu() {
        String[] reportOptions = {"Display Sent Sender/Recipient", "Display Longest Sent Message", "Delete a Sent Message", "Display Full Sent Report", "Back"};
        int choice = JOptionPane.showOptionDialog(null, "Sent Message Reports", "Reports Menu",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, reportOptions, reportOptions[0]);

        switch (choice) {
            case 0: displaySenderAndRecipient(); break;
            case 1: displayLongestMessage(); break;
            case 2: deleteByHash(); break;
            case 3: displayFullReport(); break;
            default: return;
        }
    }
    
    public static void doRegistration() {
        String username = JOptionPane.showInputDialog("Enter username:");
        if (username == null) return;
        String password = JOptionPane.showInputDialog("Enter password:");
        if (password == null) return;
        String cellphone = JOptionPane.showInputDialog("Enter cellphone (+27...):");
        if (cellphone == null) return;
        String firstName = JOptionPane.showInputDialog("Enter first name:");
        if (firstName == null) return;
        String lastName = JOptionPane.showInputDialog("Enter last name:");
        if (lastName == null) return;

        RegistrationLogin newUser = new RegistrationLogin();
        String feedback = userManager.registerUser(newUser, username, password, cellphone, firstName, lastName);
        JOptionPane.showMessageDialog(null, feedback);
    }

    public static void doLogin() {
        String username = JOptionPane.showInputDialog("Enter username:");
        if (username == null) return;
        String password = JOptionPane.showInputDialog("Enter password:");
        if (password == null) return;

        RegistrationLogin user = userManager.findUser(username);
        if (user != null && user.loginUser(username, password)) {
            currentUser = user;
            allMessages = MessageManager.loadAllMessages();
            JOptionPane.showMessageDialog(null, user.returnLoginStatus());
        } else {
            JOptionPane.showMessageDialog(null, "Login failed.");
        }
    }
    
    public static void doSendMessage() {
        String recipient = JOptionPane.showInputDialog("Recipient's cell number:");
        if (recipient == null) return;
        String payload = JOptionPane.showInputDialog("Your message (max 250 chars):");
        if (payload == null) return;

        while (payload.length() > 250) {
            payload = JOptionPane.showInputDialog("Too long! Message (max 250 chars):");
            if (payload == null) return;
        }

        Message message = new Message(currentUser.getCellPhoneNumber(), recipient, payload);
        String[] actions = {"Send", "Disregard", "Store for later"};
        int choice = JOptionPane.showOptionDialog(null, "Action?", "Message Action",
                JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, actions, actions[0]);

        switch(choice) {
            case 0:
                String result = message.sentMessage();
                if (result.equals("Message sent successfully")) allMessages.add(message);
                JOptionPane.showMessageDialog(null, result);
                break;
            case 1:
                JOptionPane.showMessageDialog(null, "Message disregarded.");
                break;
            case 2:
                message.setStatus("Stored");
                message.storeMessage();
                allMessages.add(message);
                JOptionPane.showMessageDialog(null, "Message stored.");
                break;
        }
    }

    public static void doLogout() {
        currentUser = null;
        allMessages.clear();
        JOptionPane.showMessageDialog(null, "Logged out.");
    }
    
    public static void displaySenderAndRecipient() {
        StringBuilder report = new StringBuilder("--- Your Sent Messages ---\n");
        boolean found = false;
        for (Message msg : allMessages) {
            if (msg.getSender().equals(currentUser.getCellPhoneNumber()) && "Sent".equals(msg.getStatus())) {
                report.append("To: ").append(msg.getRecipient()).append("\n");
                found = true;
            }
        }
        JOptionPane.showMessageDialog(null, found ? report.toString() : "You haven't sent any messages.");
    }

    public static void displayLongestMessage() {
        Message longestMsg = null;
        for (Message msg : allMessages) {
            if (msg.getSender().equals(currentUser.getCellPhoneNumber()) && "Sent".equals(msg.getStatus())) {
                if (longestMsg == null || msg.getPayload().length() > longestMsg.getPayload().length()) {
                    longestMsg = msg;
                }
            }
        }
        if (longestMsg != null) JOptionPane.showMessageDialog(null, "Longest sent message:\n" + longestMsg.getPayload());
        else JOptionPane.showMessageDialog(null, "No sent messages found.");
    }

    public static void deleteByHash() {
        String hash = JOptionPane.showInputDialog("Enter hash of the SENT message to delete:");
        if (hash == null) return;
        Message toDelete = null;
        for (Message msg : allMessages) {
            if (hash.equals(msg.getHash()) && msg.getSender().equals(currentUser.getCellPhoneNumber())) {
                toDelete = msg;
                break;
            }
        }
        if (toDelete != null) {
            if (MessageManager.deleteMessageFile(toDelete.getId())) {
                allMessages.remove(toDelete);
                JOptionPane.showMessageDialog(null, "Message deleted: \"" + toDelete.getPayload() + "\"");
            } else {
                JOptionPane.showMessageDialog(null, "Error: Could not delete message file.");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Message with that hash not found in your sent items.");
        }
    }
    
    public static void displayFullReport() {
        StringBuilder report = new StringBuilder("--- Full Sent Items Report ---\n");
        boolean found = false;
        for (Message msg : allMessages) {
            if (msg.getSender().equals(currentUser.getCellPhoneNumber())) {
                report.append("Hash: ").append(msg.getHash()).append("\n");
                report.append("Recipient: ").append(msg.getRecipient()).append("\n");
                report.append("Message: ").append(msg.getPayload()).append("\n");
                report.append("Status: ").append(msg.getStatus()).append("\n------------------\n");
                found = true;
            }
        }
        JOptionPane.showMessageDialog(null, found ? report.toString() : "You have no sent or stored messages.");
    }
}
