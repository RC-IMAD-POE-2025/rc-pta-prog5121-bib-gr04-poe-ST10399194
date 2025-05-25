package koketso;

import org.json.simple.JSONObject;
import java.io.FileWriter;
import java.io.IOException;

public class Message {
  
    private final String MESSAGE_ID;        // Unique identifier for each message
    private final String MESSAGE_SENDER;    // Sender's cell number
    private final String MESSAGE_RECIPIENT; // Recipient's cell number
    private final String MESSAGE_PAYLOAD;   // Content of the message
    private int MESSAGE_INDEX;              // Order of the sent message, set when sent
    private String MESSAGE_HASH;            // Hash of the message, set when sent
    private static int messageCounter = 0;  // Global counter for sent messages
    private static String lastSentMessage = ""; // Details of the last sent message

    /**
     * Creates a new message with a randomly generated 10-digit ID.
     *
     * @param sender    The sender's cell number (e.g., +27123456789)
     * @param recipient The recipient's cell number (e.g., +27123456789)
     * @param payload   The message content (max 250 characters)
     */
    public Message(final String sender, final String recipient, final String payload) {
        this.MESSAGE_ID = String.format("%010d", (long)(Math.random() * 10000000000L));
        this.MESSAGE_SENDER = sender;
        this.MESSAGE_RECIPIENT = recipient;
        this.MESSAGE_PAYLOAD = payload;
        this.MESSAGE_INDEX = 0;
        this.MESSAGE_HASH = "";
    }

    /**
     * Loads a message from persistent storage (JSON) using existing fields.
     *
     * @param id        The message ID from storage
     * @param sender    The sender's cell number from storage
     * @param recipient The recipient's cell number from storage
     * @param payload   The message content from storage
     * @param index     The message index from storage
     * @param hash      The message hash from storage
     */
    Message(String id, String sender, String recipient, String payload, int index, String hash) {
        this.MESSAGE_ID = id;
        this.MESSAGE_SENDER = sender;
        this.MESSAGE_RECIPIENT = recipient;
        this.MESSAGE_PAYLOAD = payload;
        this.MESSAGE_INDEX = index;
        this.MESSAGE_HASH = hash;
    }

    /**
     * Checks if the message ID is a valid 10-digit number.
     *
     * @param id The message ID to validate
     * @return true if valid, false otherwise
     */
    public boolean checkMessageID(final String id) {
        if (id == null || id.length() != 10) {
            return false;
        }
        for (int i = 0; i < id.length(); i++) {
            char c = id.charAt(i);
            if (c < '0' || c > '9') {
                return false;
            }
        }
        return true;
    }

    /**
     * Validates a cell number (must be +27 followed by 9 digits).
     *
     * @param cellNumber The cell number to check
     * @return 1 if valid, 403 if invalid
     */
    public int checkRecipientCell(final String cellNumber) {
        if (cellNumber == null || cellNumber.length() != 12) {
            return 403;
        }
        if (cellNumber.charAt(0) != '+' || cellNumber.charAt(1) != '2' || cellNumber.charAt(2) != '7') {
            return 403;
        }
        for (int i = 3; i < cellNumber.length(); i++) {
            char c = cellNumber.charAt(i);
            if (c < '0' || c > '9') {
                return 403;
            }
        }
        return 1;
    }

    /**
     * Generates a hash from the message ID, index, and payload.
     * Format: <first two digits of ID>:<index>:<first word><last word> (uppercase).
     * Uses only one word if payload has a single word.
     *
     * @param id      The message ID
     * @param index   The message index
     * @param payload The message content
     * @return The computed hash in uppercase
     */
    public String createMessageHash(final String id, int index, final String payload) {
        String firstTwo = (id != null && id.length() >= 2) ? id.substring(0, 2) : (id != null ? id : "");

        String firstWord = "";
        int i = 0;
        if (payload != null) {
            while (i < payload.length() && payload.charAt(i) == ' ') i++;
            while (i < payload.length() && payload.charAt(i) != ' ') {
                firstWord += payload.charAt(i++);
            }
        }

        String lastWord = "";
        if (payload != null) {
            int j = payload.length() - 1;
            while (j >= 0 && payload.charAt(j) == ' ') j--;
            while (j >= 0 && payload.charAt(j) != ' ') {
                lastWord = payload.charAt(j--) + lastWord;
            }
        }

        String combinedWords = firstWord.equals(lastWord) ? firstWord : firstWord + lastWord;
        String hash = firstTwo + ":" + index + ":" + combinedWords;
        return hash.toUpperCase();
    }

    /**
     * Sends the message after validation, updating index and hash.
     *
     * @return A success or failure message
     */
    public String sentMessage() {
        if (!checkMessageID(MESSAGE_ID)) {
            return "Failed to send message: Invalid message ID";
        }
        if (checkRecipientCell(MESSAGE_RECIPIENT) != 1) {
            return "Failed to send message: Invalid recipient";
        }
        if (checkRecipientCell(MESSAGE_SENDER) != 1) {
            return "Failed to send message: Invalid sender";
        }
        if (MESSAGE_PAYLOAD == null || MESSAGE_PAYLOAD.trim().isEmpty()) {
            return "Failed to send message: Message content cannot be empty";
        }
        if (MESSAGE_PAYLOAD.length() > 250) {
            return "Failed to send message: Payload too long";
        }

        messageCounter++;
        this.MESSAGE_INDEX = messageCounter;
        this.MESSAGE_HASH = createMessageHash(MESSAGE_ID, MESSAGE_INDEX, MESSAGE_PAYLOAD);
        lastSentMessage = "ID: " + MESSAGE_ID + ", Sender: " + MESSAGE_SENDER +
                          ", Recipient: " + MESSAGE_RECIPIENT + ", Payload: " + MESSAGE_PAYLOAD +
                          ", Hash: " + MESSAGE_HASH + ", Index: " + MESSAGE_INDEX;
        return "Message sent successfully";
    }

    /**
     * Retrieves details of the last sent message.
     *
     * @return Last sent message details or "No messages sent" if none exist
     */
    public String printMessages() {
        return (messageCounter == 0 || lastSentMessage.isEmpty()) ? "No messages sent" : lastSentMessage;
    }

    /**
     * Returns the total number of sent messages.
     *
     * @return Total sent messages
     */
    public static int returnTotalMessages() {
        return messageCounter;
    }

    /**
     * Stores message details in a JSON file.
     * Filename uses MESSAGE_INDEX if sent, otherwise MESSAGE_ID.
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

        String fileName = MESSAGE_INDEX != 0 ? "message_" + MESSAGE_INDEX + ".json" : "message_stored_" + MESSAGE_ID + ".json";

        try (FileWriter file = new FileWriter(fileName)) {
            file.write(json.toJSONString());
        } catch (IOException e) {
            System.err.println("Error storing message: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** @return The message ID */
    public String getId() { return MESSAGE_ID; }

    /** @return The sender's cell number */
    public String getSender() { return MESSAGE_SENDER; }

    /** @return The recipient's cell number */
    public String getRecipient() { return MESSAGE_RECIPIENT; }

    /** @return The message content */
    public String getPayload() { return MESSAGE_PAYLOAD; }

    /** @return The message index */
    public int getIndex() { return MESSAGE_INDEX; }

    /** @return The message hash */
    public String getHash() { return MESSAGE_HASH; }

    /** Sets the message index (e.g., when loaded from storage) */
    public void setIndex(int index) { this.MESSAGE_INDEX = index; }

    /** Sets the message hash (e.g., when loaded from storage) */
    public void setHash(String hash) { this.MESSAGE_HASH = hash; }
}