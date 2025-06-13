package koketso;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class MessageManager {
    private static final String MESSAGES_DIR = "messages";

    /**
     * This method reads all the .json files from the 'messages' folder
     * and loads them into a list.
     */
    public static ArrayList<Message> loadAllMessages() {
        ArrayList<Message> allMessages = new ArrayList<>();
        JSONParser parser = new JSONParser();
        File directory = new File(MESSAGES_DIR);

        if (!directory.exists()) {
            directory.mkdirs();
            return allMessages;
        }

        File[] files = directory.listFiles((dir, name) -> name.endsWith(".json"));

        if (files != null) {
            for (File file : files) {
                try (FileReader reader = new FileReader(file)) {
                    JSONObject jsonMessage = (JSONObject) parser.parse(reader);

                    String id = (String) jsonMessage.get("MESSAGE_ID");
                    String sender = (String) jsonMessage.get("MESSAGE_SENDER");
                    String recipient = (String) jsonMessage.get("MESSAGE_RECIPIENT");
                    String payload = (String) jsonMessage.get("MESSAGE_PAYLOAD");
                    long indexLong = (Long) jsonMessage.getOrDefault("MESSAGE_INDEX", 0L);
                    int index = (int) indexLong;
                    String hash = (String) jsonMessage.get("MESSAGE_HASH");
                    String status = (String) jsonMessage.getOrDefault("MESSAGE_STATUS", "Stored");
                    
                    boolean isReceived = (boolean) jsonMessage.getOrDefault("IS_RECEIVED", false);
                    boolean isRead = (boolean) jsonMessage.getOrDefault("IS_READ", false);

                    Message message = new Message(id, sender, recipient, payload, index, hash);
                    message.setStatus(status);
                    message.setReceived(isReceived);
                    message.setRead(isRead);
                    
                    allMessages.add(message);
                    
                } catch (IOException | ParseException e) {
                    System.err.println("Problem reading file " + file.getName() + ": " + e.getMessage());
                } catch (Exception e) { 
                    System.err.println("A weird error happened with file " + file.getName() + ": " + e.getMessage());
                }
            }
        }
        return allMessages;
    }

    /**
     * This method deletes the JSON file for a specific message.
     * @param messageId The ID of the message to delete.
     * @return true if it was deleted, false otherwise.
     */
    public static boolean deleteMessageFile(String messageId) {
        if (messageId == null || messageId.isEmpty()) {
            return false;
        }
        File fileToDelete = new File(MESSAGES_DIR + "/message_" + messageId + ".json");
        if (fileToDelete.exists()) {
            return fileToDelete.delete();
        }
        return false;
    }
}
