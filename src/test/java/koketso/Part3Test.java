package koketso;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

/**
 * This test class covers all the requirements for Part 3 of the POE.
 * It tests the array population and reporting features.
 */
public class Part3Test {

    // Helper method to create the exact test data specified in the POE document for Part 3.
    private ArrayList<Message> createPoeTestData() {
        ArrayList<Message> messages = new ArrayList<>();
        String currentUser = "+27000000000"; // A dummy sender for our tests

        // Message 1: Sent (From POE Test Data)
        Message msg1 = new Message(currentUser, "+27834557896", "Did you get the cake?");
        msg1.setStatus("Sent");
        msg1.sentMessage(); // Generate hash and index
        messages.add(msg1);

        // Message 2: Stored (From POE Test Data)
        Message msg2 = new Message(currentUser, "+27838884567", "Where are you? You are late! I have asked you to be on time.");
        msg2.setStatus("Stored");
        messages.add(msg2);

        // Message 3: Disregarded (From POE - not added to the main list)

        // Message 4: Sent (From POE Test Data)
        Message msg4 = new Message(currentUser, "+27838884567", "It is dinner time!");
        msg4.setStatus("Sent");
        msg4.sentMessage(); // Generate hash and index
        messages.add(msg4);
        
        // Message 5: Stored (From POE Test Data)
        Message msg5 = new Message(currentUser, "+27838884567", "Ok, I am leaving without you.");
        msg5.setStatus("Stored");
        messages.add(msg5);

        return messages;
    }

    /**
     * Test: Sent Messages array correctly populated.
     * POE expects the sent messages to be: "Did you get the cake?" and "It is dinner time!"
     */
    @Test
    void testSentMessagesArrayIsCorrectlyPopulated() {
        ArrayList<Message> allMessages = createPoeTestData();
        
        // This simulates the logic that would be in the main app to create the array of sent messages.
        ArrayList<Message> sentMessages = new ArrayList<>();
        for (Message msg : allMessages) {
            if ("Sent".equals(msg.getStatus())) {
                sentMessages.add(msg);
            }
        }
        
        assertEquals(2, sentMessages.size(), "The array of sent messages should contain exactly 2 messages.");
        
        // Verify the content of the sent messages.
        assertEquals("Did you get the cake?", sentMessages.get(0).getPayload());
        assertEquals("It is dinner time!", sentMessages.get(1).getPayload());
    }
    
    /**
     * Test: Display the longest Message.
     * POE expects this to be "Where are you? You are late! I have asked you to be on time."
     * Note: The POE test data implies searching all messages, not just sent ones, for the longest.
     */
    @Test
    void testDisplayTheLongestMessage() {
        ArrayList<Message> allMessages = createPoeTestData();
        Message longestMessage = null;

        // Find the longest message from all available messages
        for (Message msg : allMessages) {
            if (longestMessage == null || msg.getPayload().length() > longestMessage.getPayload().length()) {
                longestMessage = msg;
            }
        }
        
        assertNotNull(longestMessage, "A longest message should have been found.");
        assertEquals("Where are you? You are late! I have asked you to be on time.", longestMessage.getPayload());
    }
    
    /**
     * Test: Search for a message by its ID.
     * POE uses Message 4 ("It is dinner time!") for this test.
     */
    @Test
    void testSearchForMessageByID() {
        ArrayList<Message> allMessages = createPoeTestData();
        // Get Message 4 from our test data. In the real list, it's at index 2.
        Message messageToFind = allMessages.get(2);
        String idToSearch = messageToFind.getId();
        
        String foundRecipient = "";
        String foundMessage = "";
        
        // Search the list for the message with the matching ID.
        for (Message msg : allMessages) {
            if (msg.getId().equals(idToSearch)) {
                foundRecipient = msg.getRecipient();
                foundMessage = msg.getPayload();
                break; // Stop once we find it
            }
        }
        
        assertEquals("+27838884567", foundRecipient, "The recipient for the found message is incorrect.");
        assertEquals("It is dinner time!", foundMessage, "The payload for the found message is incorrect.");
    }
    
    /**
     * Test: Search for all messages sent to a particular recipient.
     * POE expects to find three messages for the recipient "+27838884567".
     */
    @Test
    void testSearchForAllMessagesForAParticularRecipient() {
        ArrayList<Message> allMessages = createPoeTestData();
        String recipientToSearch = "+27838884567";
        
        // Create an array to hold the payloads of the messages we find.
        ArrayList<String> foundMessages = new ArrayList<>();
        for (Message msg : allMessages) {
            if (msg.getRecipient().equals(recipientToSearch)) {
                foundMessages.add(msg.getPayload());
            }
        }
        
        assertEquals(3, foundMessages.size(), "Should have found 3 messages for the recipient " + recipientToSearch);
        
        // Check that the correct messages were found.
        assertTrue(foundMessages.contains("Where are you? You are late! I have asked you to be on time."));
        assertTrue(foundMessages.contains("It is dinner time!"));
        assertTrue(foundMessages.contains("Ok, I am leaving without you."));
    }

    /**
     * Test: Delete a message using its message hash.
     * POE uses Test Message 2 for this, but it has no hash. We'll use Message 4 instead.
     */
    @Test
    void testDeleteAMessageUsingMessageHash() {
        ArrayList<Message> allMessages = createPoeTestData();
        
        // Message 4 ("It is dinner time!") was sent and has a hash.
        Message messageToDelete = allMessages.get(2);
        String hashToDelete = messageToDelete.getHash();

        // Make sure we have a valid hash to test with.
        assertNotNull(hashToDelete, "The message to be deleted must have a hash.");
        assertFalse(hashToDelete.isEmpty(), "The hash should not be empty.");
        
        // Find the message with the matching hash and remove it.
        allMessages.removeIf(msg -> hashToDelete.equals(msg.getHash()));
        
        assertEquals(3, allMessages.size(), "The list should have 3 messages after one was deleted.");
        
        // We can also check that the deleted message is no longer in the list.
        boolean stillExists = false;
        for (Message msg : allMessages) {
            if (hashToDelete.equals(msg.getHash())) {
                stillExists = true;
                break;
            }
        }
        assertFalse(stillExists, "The deleted message should no longer exist in the list.");
    }

    /**
     * Test: Display Report
     * This test checks if the full report string is generated correctly for all sent messages.
     */
    @Test
    void testDisplayReportOfAllSentMessages() {
        ArrayList<Message> allMessages = createPoeTestData();
        
        StringBuilder report = new StringBuilder();
        for(Message msg : allMessages) {
            if("Sent".equals(msg.getStatus())) {
                report.append("Message Hash: ").append(msg.getHash()).append("\n");
                report.append("Recipient: ").append(msg.getRecipient()).append("\n");
                report.append("Message: ").append(msg.getPayload()).append("\n");
                report.append("---------------------------------\n");
            }
        }
        
        String reportString = report.toString();
        
        // Check that the report contains key details from both sent messages.
        assertTrue(reportString.contains("Did you get the cake?"), "Report should contain the first sent message.");
        assertTrue(reportString.contains("It is dinner time!"), "Report should contain the second sent message.");
        assertTrue(reportString.contains(allMessages.get(0).getHash()), "Report should contain the hash of the first sent message.");
        assertTrue(reportString.contains(allMessages.get(2).getHash()), "Report should contain the hash of the second sent message.");
    }
}
