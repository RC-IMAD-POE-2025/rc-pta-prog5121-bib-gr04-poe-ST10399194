package koketso;

import koketso.Message;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

public class MessageTest {

    @BeforeEach
    void resetStaticFields() throws Exception {
        // Reset static fields using reflection to isolate tests
        Field counterField = Message.class.getDeclaredField("messageCounter");
        counterField.setAccessible(true);
        counterField.set(null, 0);

        Field lastSentField = Message.class.getDeclaredField("lastSentMessage");
        lastSentField.setAccessible(true);
        lastSentField.set(null, "");
    }

    // Test the public constructor
    @Test
    void testConstructor() {
        String sender = "+27123456789";
        String recipient = "+27987654321";
        String payload = "Hello";
        Message msg = new Message(sender, recipient, payload);

        assertEquals(10, msg.getId().length(), "Message ID should be 10 digits");
        assertTrue(msg.checkMessageID(msg.getId()), "Message ID should be valid");
        assertEquals(sender, msg.getSender(), "Sender should match input");
        assertEquals(recipient, msg.getRecipient(), "Recipient should match input");
        assertEquals(payload, msg.getPayload(), "Payload should match input");
        assertEquals(0, msg.getIndex(), "Index should be 0 initially");
        assertEquals("", msg.getHash(), "Hash should be empty initially");
    }

    // Test checkMessageID method
    @Test
    void testCheckMessageID() {
        Message msg = new Message("+27123456789", "+27987654321", "Test");

        assertTrue(msg.checkMessageID("1234567890"), "10-digit numeric ID should be valid");
        assertFalse(msg.checkMessageID("123456789"), "9-digit ID should be invalid");
        assertFalse(msg.checkMessageID("123456789a"), "ID with letters should be invalid");
        assertFalse(msg.checkMessageID(null), "Null ID should be invalid");
    }

    // Test checkRecipientCell method
    @Test
    void testCheckRecipientCell() {
        Message msg = new Message("+27123456789", "+27987654321", "Test");

        assertEquals(1, msg.checkRecipientCell("+27123456789"), "Valid SA cell number should return 1");
        assertEquals(403, msg.checkRecipientCell("+2712345678"), "Short number should return 403");
        assertEquals(403, msg.checkRecipientCell("27123456789"), "Number without + should return 403");
        assertEquals(403, msg.checkRecipientCell("+27abcdefghij"), "Non-numeric number should return 403");
        assertEquals(403, msg.checkRecipientCell(null), "Null number should return 403");
    }

    // Test createMessageHash method
    @Test
    void testCreateMessageHash() {
        Message msg = new Message("+27123456789", "+27987654321", "Hi Tonight");

        String hash1 = msg.createMessageHash("0012345678", 0, "Hi Tonight");
        assertEquals("00:0:HITONIGHT", hash1, "Hash should match expected format for 'Hi Tonight'");

        String hash2 = msg.createMessageHash("1234567890", 1, "Hello world");
        assertEquals("12:1:HELLOWORLD", hash2, "Hash should match expected format for 'Hello world'");

        String hash3 = msg.createMessageHash("9876543210", 2, "  Single  ");
        assertEquals("98:2:SINGLE", hash3, "Hash should handle single word with spaces");
    }

    // Test sentMessage with valid input
    @Test
    void testSentMessageValid() {
        Message msg = new Message("+27123456789", "+27987654321", "Hello");

        String result = msg.sentMessage();
        assertEquals("Message sent successfully", result, "Valid message should send successfully");
        assertEquals(1, msg.getIndex(), "Index should be 1 after sending");
        assertEquals(msg.createMessageHash(msg.getId(), 1, "Hello"), msg.getHash(), "Hash should be set correctly");
        assertEquals(1, Message.returnTotalMessages(), "Message counter should increment to 1");

        String expectedLastSent = "ID: " + msg.getId() + ", Sender: +27123456789, Recipient: +27987654321, Payload: Hello, Hash: " + msg.getHash() + ", Index: 1";
        assertEquals(expectedLastSent, msg.printMessages(), "Last sent message should match expected output");
    }

    // Test sentMessage with invalid ID using package-private constructor
    @Test
    void testSentMessageInvalidID() {
        Message msg = new Message("invalid", "+27123456789", "+27987654321", "Hello", 0, "");

        String result = msg.sentMessage();
        assertEquals("Failed to send message: Invalid message ID", result, "Invalid ID should fail");
        assertEquals(0, Message.returnTotalMessages(), "Message counter should not increment");
    }

    // Test sentMessage with invalid recipient
    @Test
    void testSentMessageInvalidRecipient() {
        Message msg = new Message("+27123456789", "invalid", "Hello");

        String result = msg.sentMessage();
        assertEquals("Failed to send message: Invalid recipient", result, "Invalid recipient should fail");
        assertEquals(0, Message.returnTotalMessages(), "Message counter should not increment");
    }

    // Test sentMessage with invalid sender
    @Test
    void testSentMessageInvalidSender() {
        Message msg = new Message("invalid", "+27987654321", "Hello");

        String result = msg.sentMessage();
        assertEquals("Failed to send message: Invalid sender", result, "Invalid sender should fail");
        assertEquals(0, Message.returnTotalMessages(), "Message counter should not increment");
    }

    // Test sentMessage with empty payload
    @Test
    void testSentMessageEmptyPayload() {
        Message msg = new Message("+27123456789", "+27987654321", "");

        String result = msg.sentMessage();
        assertEquals("Failed to send message: Message content cannot be empty", result, "Empty payload should fail");
        assertEquals(0, Message.returnTotalMessages(), "Message counter should not increment");
    }

    // Test sentMessage with payload exceeding 250 characters
    @Test
    void testSentMessagePayloadTooLong() {
        String longPayload = "a".repeat(251);
        Message msg = new Message("+27123456789", "+27987654321", longPayload);

        String result = msg.sentMessage();
        assertEquals("Failed to send message: Payload too long", result, "Long payload should fail");
        assertEquals(0, Message.returnTotalMessages(), "Message counter should not increment");
    }

    // Test multiple messages to verify counter and last sent message
    @Test
    void testMultipleSentMessages() {
        Message msg1 = new Message("+27123456789", "+27987654321", "First");
        assertEquals("Message sent successfully", msg1.sentMessage(), "First message should send");
        assertEquals(1, Message.returnTotalMessages(), "Counter should be 1");

        Message msg2 = new Message("+27123456789", "+27987654321", "Second");
        assertEquals("Message sent successfully", msg2.sentMessage(), "Second message should send");
        assertEquals(2, Message.returnTotalMessages(), "Counter should be 2");

        String expectedLastSent = "ID: " + msg2.getId() + ", Sender: +27123456789, Recipient: +27987654321, Payload: Second, Hash: " + msg2.getHash() + ", Index: 2";
        assertEquals(expectedLastSent, msg2.printMessages(), "Last sent message should be the second one");
    }

    // Test printMessages when no messages have been sent
    @Test
    void testPrintMessagesNoMessages() {
        Message msg = new Message("+27123456789", "+27987654321", "Test");
        assertEquals("No messages sent", msg.printMessages(), "Should indicate no messages sent initially");
    }

    // Test storeMessage (basic execution, not verifying file content)
    @Test
    void testStoreMessage() {
        Message msg = new Message("+27123456789", "+27987654321", "Hello");
        assertDoesNotThrow(msg::storeMessage, "storeMessage should execute without throwing exceptions");
    }
}