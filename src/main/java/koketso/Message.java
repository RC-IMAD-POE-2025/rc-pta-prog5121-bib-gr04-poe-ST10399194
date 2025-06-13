package koketso;

import org.json.simple.JSONObject;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Message {

    // Details for each message
    private final String MESSAGE_ID;
    private final String MESSAGE_SENDER;
    private final String MESSAGE_RECIPIENT;
    private final String MESSAGE_PAYLOAD;
    private int MESSAGE_INDEX;
    private String MESSAGE_HASH;
    private String messageStatus; // "Sent", "Stored", "Disregarded"
    
    // New flags for Part 3 to track delivery and reading
    private boolean isReceived;
    private boolean isRead;

    private static int messageCounter = 0;
    private static String lastSentMessage = "";

    /**
     * Constructor for creating a new message.
     */
    public Message(final String sender, final String recipient, final String payload) {
        this.MESSAGE_ID = String.format("%010d", (long) (Math.random() * 10000000000L));
        this.MESSAGE_SENDER = sender;
        this.MESSAGE_RECIPIENT = recipient;
        this.MESSAGE_PAYLOAD = payload;
        this.MESSAGE_INDEX = 0;
        this.MESSAGE_HASH = "";
        this.messageStatus = "Stored";
        this.isReceived = false; // A new message has not been received yet
        this.isRead = false;     // Or read yet
    }

    /**
     * This constructor is used when loading a message from a file.
     */
    Message(String id, String sender, String recipient, String payload, int index, String hash) {
        this.MESSAGE_ID = id;
        this.MESSAGE_SENDER = sender;
        this.MESSAGE_RECIPIENT = recipient;
        this.MESSAGE_PAYLOAD = payload;
        this.MESSAGE_INDEX = index;
        this.MESSAGE_HASH = hash;
        this.messageStatus = (index == 0) ? "Stored" : "Sent";
        this.isReceived = false; // Default to false, will be updated from file
        this.isRead = false;     // Default to false
    }

    /**
     * Tries to send the message. It does all the checks first.
     */
    public String sentMessage() {
        // ... (validation checks remain the same)
        if (!checkMessageID(MESSAGE_ID)) return "Failed to send message: Invalid message ID";
        if (checkRecipientCell(MESSAGE_RECIPIENT) != 1) return "Failed to send message: Invalid recipient";
        if (checkRecipientCell(MESSAGE_SENDER) != 1) return "Failed to send message: Invalid sender";
        if (MESSAGE_PAYLOAD == null || MESSAGE_PAYLOAD.trim().isEmpty()) return "Failed to send message: Message content cannot be empty";
        if (MESSAGE_PAYLOAD.length() > 250) return "Failed to send message: Payload too long";

        messageCounter++;
        this.MESSAGE_INDEX = messageCounter;
        this.MESSAGE_HASH = createMessageHash(MESSAGE_ID, MESSAGE_INDEX, MESSAGE_PAYLOAD);
        this.messageStatus = "Sent";
        
        lastSentMessage = "ID: " + MESSAGE_ID + ", To: " + MESSAGE_RECIPIENT + ", Message: " + MESSAGE_PAYLOAD;
        
        storeMessage(); // Save the message to a file after sending
        return "Message sent successfully";
    }

    /**
     * Saves the message's details into a JSON file, including the new statuses.
     */
    @SuppressWarnings("unchecked")
    public void storeMessage() {
        JSONObject json = new JSONObject();
        json.put("MESSAGE_ID", MESSAGE_ID);
        json.put("MESSAGE_SENDER", MESSAGE_SENDER);
        json.put("MESSAGE_RECIPIENT", MESSAGE_RECIPIENT);
        json.put("MESSAGE_PAYLOAD", MESSAGE_PAYLOAD);
        json.put("MESSAGE_INDEX", MESSAGE_INDEX);
        json.put("MESSAGE_HASH", MESSAGE_HASH);
        json.put("MESSAGE_STATUS", messageStatus);
        json.put("IS_RECEIVED", isReceived); // Save the received status
        json.put("IS_READ", isRead);         // Save the read status

        File directory = new File("messages");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        String fileName = "messages/message_" + MESSAGE_ID + ".json";
        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json.toJSONString());
        } catch (IOException e) {
            System.err.println("Error trying to save message file: " + e.getMessage());
        }
    }
    
    // --- Other methods like checkMessageID, checkRecipientCell, etc. remain the same ---

    public boolean checkMessageID(final String id) {
        if (id == null || id.length() != 10) return false;
        for (char c : id.toCharArray()) if (!Character.isDigit(c)) return false;
        return true;
    }

    public int checkRecipientCell(final String cellNumber) {
        if (cellNumber != null && cellNumber.matches("^\\+27\\d{9}$")) return 1;
        return 403;
    }

    public String createMessageHash(final String id, int index, final String payload) {
        String firstTwo = (id != null && id.length() >= 2) ? id.substring(0, 2) : "XX";
        String[] words = payload.trim().split("\\s+");
        String firstWord = words.length > 0 ? words[0] : "";
        String lastWord = words.length > 1 ? words[words.length - 1] : firstWord;
        String combinedWords = firstWord.equalsIgnoreCase(lastWord) ? firstWord : firstWord + lastWord;
        return (firstTwo + ":" + index + ":" + combinedWords).toUpperCase();
    }
    
    // --- Getter and Setter Methods ---
    
    public String getId() { return MESSAGE_ID; }
    public String getSender() { return MESSAGE_SENDER; }
    public String getRecipient() { return MESSAGE_RECIPIENT; }
    public String getPayload() { return MESSAGE_PAYLOAD; }
    public String getHash() { return MESSAGE_HASH; }
    public String getStatus() { return messageStatus; }
    public boolean isReceived() { return isReceived; }
    public boolean isRead() { return isRead; }

    public void setStatus(String status) { this.messageStatus = status; }
    public void setReceived(boolean received) { this.isReceived = received; }
    public void setRead(boolean read) { this.isRead = read; }
}
