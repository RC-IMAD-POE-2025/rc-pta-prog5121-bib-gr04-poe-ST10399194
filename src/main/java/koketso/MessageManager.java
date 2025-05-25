package koketso;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MessageManager {
    /**
     * Loads messages from JSON files.
     */
    public static ArrayList<Message> loadUserMessages(String userCellPhone) {
        ArrayList<Message> userMessages = new ArrayList<>();
        JSONParser parser = new JSONParser();
        File directory = new File(".");

        // List all files that start with "message_" and end with ".json"
        File[] files = directory.listFiles((dir, name) -> name.startsWith("message_") && name.endsWith(".json"));

        if (files != null) {
            for (File file : files) {
                try (FileReader reader = new FileReader(file)) {
                    JSONObject jsonMessage = (JSONObject) parser.parse(reader);

                    String id = (String) jsonMessage.get("MESSAGE_ID");
                    String sender = (String) jsonMessage.get("MESSAGE_SENDER");
                    String recipient = (String) jsonMessage.get("MESSAGE_RECIPIENT");
                    String payload = (String) jsonMessage.get("MESSAGE_PAYLOAD");
                    
                    // JSONSimple parses numbers as Long by default
                    long indexLong = (Long) jsonMessage.getOrDefault("MESSAGE_INDEX", 0L);
                    int index = (int) indexLong;
                    
                    String hash = (String) jsonMessage.get("MESSAGE_HASH");

                    // Filter: load message if user is sender or recipient
                    if (userCellPhone != null && (userCellPhone.equals(sender) || userCellPhone.equals(recipient))) {
                        // Use the new package-private constructor to create Message object
                        Message message = new Message(id, sender, recipient, payload, index, hash);
                        userMessages.add(message);
                    }
                } catch (IOException | ParseException e) {
                    System.err.println("Error loading message from file " + file.getName() + ": " + e.getMessage());
                    
                } catch (Exception e) { // Catch any other unexpected errors during message parsing
                    System.err.println("Unexpected error processing file " + file.getName() + ": " + e.getMessage());
                  
                }
            }
        }
        return userMessages;
    }
}
