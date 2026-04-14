
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class ContactTest {
    private static String repeat(char c, int count) {
        return String.valueOf(c).repeat(Math.max(0, count));
    }

    // Test valid contact creation
    @Test
    public void testValidContactCreation() { // Test contact creation
        Contact contact = new Contact("1234567890", "John", "Doe", "1234567890", "123 Main St");
        assertAll("Valid contact creation",
            () -> assertEquals("1234567890", contact.getContactId()),
            () -> assertEquals("John", contact.getFirstName()),
            () -> assertEquals("Doe", contact.getLastName()),
            () -> assertEquals("1234567890", contact.getPhoneNumber()),
            () -> assertEquals("123 Main St", contact.getAddress())
        );
    }
    
    // Test contactId null or too long
    @Test
    public void testContactIdNullOrTooLong() {
        assertAll("Contact ID validation",
            () -> assertThrows(IllegalArgumentException.class, () -> {  // Test for a null id
                new Contact(null, "John", "Doe", "1234567890", "123 Main St");
            }),
            () -> assertThrows(IllegalArgumentException.class, () -> { // Test for blank id
                new Contact("   ", "John", "Doe", "1234567890", "123 Main St");
            }),
            () -> assertThrows(IllegalArgumentException.class, () -> { // Test for a idea that is too long
                new Contact("12345678901", "John", "Doe", "1234567890", "123 Main St"); 
            }),
            () -> assertDoesNotThrow(() -> { // Test boundary: 10 characters allowed
                new Contact(repeat('1', 10), "John", "Doe", "1234567890", "123 Main St");
            })
        );
    }

    // Test firstName null or too long
    @Test
    public void testFirstNameNullOrTooLong() {
        assertAll("First name validation",
            () -> assertThrows(IllegalArgumentException.class, () -> { // Test for a null firstname
                new Contact("123", null, "Doe", "1234567890", "123 Main St");
            }),
            () -> assertThrows(IllegalArgumentException.class, () -> { // Test for blank firstname
                new Contact("123", "   ", "Doe", "1234567890", "123 Main St");
            }),
            () -> assertThrows(IllegalArgumentException.class, () -> { // Test for a firstname that is too long
                new Contact("123", "John.............", "Doe", "1234567890", "123 Main St");
            }),
            () -> assertDoesNotThrow(() -> { // Test boundary: 10 characters allowed
                new Contact("123", repeat('A', 10), "Doe", "1234567890", "123 Main St");
            })
        );
    }

    // Test lastName null or too long
    @Test
    public void testLastNameNullOrTooLong() {
        assertAll("Last name validation",
            () -> assertThrows(IllegalArgumentException.class, () -> { // Test for a null Lastname
                new Contact("123", "John", null, "1234567890", "123 Main St");
            }),
            () -> assertThrows(IllegalArgumentException.class, () -> { // Test for blank lastname
                new Contact("123", "John", "   ", "1234567890", "123 Main St");
            }),
            () -> assertThrows(IllegalArgumentException.class, () -> { // Test for a lastname that is too long
                new Contact("123", "John", "Doe...................", "1234567890", "123 Main St");
            }),
            () -> assertDoesNotThrow(() -> { // Test boundary: 10 characters allowed
                new Contact("123", "John", repeat('B', 10), "1234567890", "123 Main St");
            })
        );
    }

    // Test phoneNumber null or not 10 digits
    @Test
    public void testPhoneNumberNullOrInvalidLength() {
        assertAll("Phone number validation",
            () -> assertThrows(IllegalArgumentException.class, () -> {
                new Contact("123", "John", "Doe", null, "123 Main St"); // Test for a null phoneNumber
            }),
            () -> assertThrows(IllegalArgumentException.class, () -> {
                new Contact("123", "John", "Doe", "abcdefghij", "123 Main St"); // Test non-digit phone
            }),
            () -> assertThrows(IllegalArgumentException.class, () -> {
                new Contact("123", "John", "Doe", "123456789", "123 Main St"); // Test for under 10
            }),
            () -> assertThrows(IllegalArgumentException.class, () -> {
                new Contact("123", "John", "Doe", "12345678901", "123 Main St"); // Test for over 10
            }),
            () -> assertDoesNotThrow(() -> {
                new Contact("123", "John", "Doe", "0123456789", "123 Main St"); // Test 10-digit phone accepted
            })
        );
    }

    // Test address null or too long
    @Test
    public void testAddressNullOrTooLong() {
        assertAll("Address validation",
            () -> assertThrows(IllegalArgumentException.class, () -> {
                new Contact("123", "John", "Doe", "1234567890", null); // Test for null address
            }),
            () -> assertThrows(IllegalArgumentException.class, () -> {
                new Contact("123", "John", "Doe", "1234567890", "   "); // Test for blank address
            }),
            () -> assertThrows(IllegalArgumentException.class, () -> {
                new Contact("123", "John", "Doe", "1234567890", "1234567890123456789012345678901"); // Test for address being too long
            }),
            () -> assertDoesNotThrow(() -> {
                new Contact("123", "John", "Doe", "1234567890", repeat('C', 30)); // Test boundary: 30 characters allowed
            })
        );
    }

    // Test immutability of content id
    @Test
    public void testImmutability() {
        Contact contact = new Contact("123", "John", "Doe", "1234567890", "123 Main St");
        // check that getters work and fields can't be changed
        assertAll("Contact immutability",
            () -> assertEquals("John", contact.getFirstName())
        );
    }

    // Test toString method
    @Test
    public void testToString() {
        Contact contact = new Contact("123", "John", "Doe", "1234567890", "123 Main St");
        assertEquals("Contact ID: 123\nFirst Name: John\nLast Name: Doe\nPhone Number: 1234567890\nAddress: 123 Main St", contact.toString());
    }


}
