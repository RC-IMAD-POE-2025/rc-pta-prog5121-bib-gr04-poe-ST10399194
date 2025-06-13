package koketso;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

public class MessageTest {

    // Test data from the POE PDF for Part 3
    // We create a dummy set of messages to test our logic
    private ArrayList<Message> createPoeTestData() {
        ArrayList<Message> messages = new ArrayList<>();
        String currentUser = "+27000000000";

        // Message 1: Sent
        Message msg1 = new Message(currentUser, "+27834557896", "Did you get the cake?");
        msg1.setStatus("Sent");
        messages.add(msg1);

        // Message 2: Stored
        Message msg2 = new Message(currentUser, "+27838884567", "Where are you? You are late! I have asked you to be on time.");
        msg2.setStatus("Stored");
        messages.add(msg2);

        // Message 3: Disregarded (not added to list as it's not saved)
        
        // Message 4: Sent
        Message msg4 = new Message(currentUser, "+27838884567", "It is dinner time!");
        msg4.setStatus("Sent");
        msg4.sentMessage(); // To generate a hash for the delete test
        messages.add(msg4);
        
        // Message 5: Stored
        Message msg5 = new Message(currentUser, "+27838884567", "Ok, I am leaving without you.");
        msg5.setStatus("Stored");
        messages.add(msg5);

        return messages;
    }

    @Test
    void testSentMessagesArrayPopulation() {
        ArrayList<Message> testData = createPoeTestData();
        ArrayList<Message> sentMessages = new ArrayList<>();
        
        for(Message msg : testData) {
            if("Sent".equals(msg.getStatus())) {
                sentMessages.add(msg);
            }
        }
        
        assertEquals(2, sentMessages.size());
        assertEquals("Did you get the cake?", sentMessages.get(0).getPayload());
        assertEquals("It is dinner time!", sentMessages.get(1).getPayload());
    }
    
    @Test
    void testDisplayLongestMessage() {
        ArrayList<Message> testData = createPoeTestData();
        Message longestMsg = null;

        for (Message msg : testData) {
            // POE asks for longest SENT message
            if ("Sent".equals(msg.getStatus())) {
                if (longestMsg == null || msg.getPayload().length() > longestMsg.getPayload().length()) {
                    longestMsg = msg;
                }
            }
        }
        // Based on the "Sent" messages in the test data, this one is the longest
        assertEquals("Did you get the cake?", longestMsg.getPayload());
    }
    
    @Test
    void testSearchForMessageId() {
        ArrayList<Message> testData = createPoeTestData();
        Message msgToFind = testData.get(2); // This is "It is dinner time!"
        String idToFind = msgToFind.getId();
        
        String foundDetails = "";
        for(Message msg : testData) {
            if(msg.getId().equals(idToFind)) {
                foundDetails = msg.getRecipient() + "\n" + msg.getPayload();
                break;
            }
        }
        
        assertEquals(testData.get(2).getRecipient() + "\n" + testData.get(2).getPayload(), foundDetails);
    }
    
    @Test
    void testSearchMessagesByRecipient() {
        ArrayList<Message> testData = createPoeTestData();
        String recipientToFind = "+27838884567";
        ArrayList<String> foundPayloads = new ArrayList<>();
        
        for(Message msg : testData) {
            if(msg.getRecipient().equals(recipientToFind)) {
                foundPayloads.add(msg.getPayload());
            }
        }
        
        assertEquals(3, foundPayloads.size());
        assertTrue(foundPayloads.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue(foundPayloads.contains("It is dinner time!"));
        assertTrue(foundPayloads.contains("Ok, I am leaving without you."));
    }

    @Test
    void testDeleteMessageByHash() {
        ArrayList<Message> testData = createPoeTestData();
        Message msgToDelete = testData.get(2); // "It is dinner time!"
        String hashToDelete = msgToDelete.getHash();

        // Make sure the hash isn't empty before we test
        assertNotNull(hashToDelete);
        assertFalse(hashToDelete.isEmpty());
        
        testData.removeIf(msg -> hashToDelete.equals(msg.getHash()));
        
        assertEquals(3, testData.size(), "The message list should have 3 items after deletion.");
    }
}
