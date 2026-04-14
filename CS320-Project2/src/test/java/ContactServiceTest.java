import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

public class ContactServiceTest {
    @TempDir
    Path tempDir;

    private ContactService service;
    private Contact contact1;
    private Contact contact2;

    @BeforeEach // Set up the test environment
    public void setUp() {
        String jdbcUrl = "jdbc:sqlite:" + tempDir.resolve("contacts.db").toAbsolutePath();
        service = new ContactService(new ContactRepository(jdbcUrl));
        contact1 = new Contact("1", "Alice", "Smith", "1111111111", "1 road St");
        contact2 = new Contact("2", "Bob", "Jones", "2222222222", "2 street Ave");
    }

    @Test // Test adding a contact with a unique ID
    public void testAddContactUniqueId() {
        assertAll("Adding contacts with unique IDs",
            () -> assertDoesNotThrow(() -> service.addContact(contact1)),
            () -> assertDoesNotThrow(() -> service.addContact(contact2))
        );
    }

    @Test // Test adding a contact with a duplicate ID
    public void testAddContactDuplicateId() {
        assertAll("Adding contacts with duplicate IDs",
            () -> assertDoesNotThrow(() -> service.addContact(contact1)),
            () -> {
                Contact duplicate = new Contact("1", "Charlie", "Brown", "3333333333", "3 Cherry Ave");
                assertThrows(DuplicateContactIdException.class, () -> service.addContact(duplicate));
            }
        );
    }

    @Test // Test deleting a contact
    public void testDeleteContact() {
        service.addContact(contact1);
        assertAll("Deleting contacts",
            () -> assertDoesNotThrow(() -> service.deleteContact("1")),
            () -> assertThrows(ContactNotFoundException.class, () -> service.deleteContact("1")), // Already deleted
            () -> assertThrows(ContactNotFoundException.class, () -> service.deleteContact("999")) // Non-existent
        );
    }

    @Test // Test updating the first name of a contact
    public void testUpdateFirstName() {
        service.addContact(contact1);
        assertAll("Updating first names",
            () -> assertDoesNotThrow(() -> service.updateFirstName("1", "Eve")),
            () -> assertEquals("Eve", service.getContact("1").getFirstName()),
            () -> assertThrows(ContactNotFoundException.class, () -> service.updateFirstName("999", "Eve")), // Non-existent
            () -> assertThrows(IllegalArgumentException.class, () -> service.updateFirstName("1", null)), // Invalid
            () -> assertThrows(IllegalArgumentException.class, () -> service.updateFirstName("1", "TestingToolongName")) // Invalid
        );
    }

    @Test // Test updating the last name of a contact
    public void testUpdateLastName() {
        service.addContact(contact1);
        assertAll("Updating last names",
            () -> assertDoesNotThrow(() -> service.updateLastName("1", "White")),
            () -> assertEquals("White", service.getContact("1").getLastName()),
            () -> assertThrows(ContactNotFoundException.class, () -> service.updateLastName("999", "White")), // Non-existent
            () -> assertThrows(IllegalArgumentException.class, () -> service.updateLastName("1", null)), // Invalid
            () -> assertThrows(IllegalArgumentException.class, () -> service.updateLastName("1", "TestingToolongName")) // Invalid
        );
    }

    @Test // Test updating the phone number of a contact
    public void testUpdatePhoneNumber() {
        service.addContact(contact1);
        assertAll("Updating phone numbers",
            () -> assertDoesNotThrow(() -> service.updatePhoneNumber("1", "1234567890")),
            () -> assertEquals("1234567890", service.getContact("1").getPhoneNumber()),
            () -> assertThrows(ContactNotFoundException.class, () -> service.updatePhoneNumber("999", "1234567890")), // Non-existent
            () -> assertThrows(IllegalArgumentException.class, () -> service.updatePhoneNumber("1", null)), // Invalid
            () -> assertThrows(IllegalArgumentException.class, () -> service.updatePhoneNumber("1", "123")), // Invalid
            () -> assertThrows(IllegalArgumentException.class, () -> service.updatePhoneNumber("1", "12345678901")) // Invalid
        );
    }

    @Test // Test updating the address of a contact 
    public void testUpdateAddress() {
        service.addContact(contact1); //
        assertAll("Updating addresses",
            () -> assertDoesNotThrow(() -> service.updateAddress("1", "New Address")),
            () -> assertEquals("New Address", service.getContact("1").getAddress()),
            () -> assertThrows(ContactNotFoundException.class, () -> service.updateAddress("999", "New Address")), // Non-existent
            () -> assertThrows(IllegalArgumentException.class, () -> service.updateAddress("1", null)), // Invalid
            () -> assertThrows(IllegalArgumentException.class, () -> service.updateAddress("1", "This address is long to be valid for the field")) // Invalid
        );
    }

    @Test
    public void testGetContactRejectsBlankIds() {
        assertAll("getContact rejects blank/null IDs",
            () -> assertThrows(IllegalArgumentException.class, () -> service.getContact(null)),
            () -> assertThrows(IllegalArgumentException.class, () -> service.getContact("   "))
        );
    }

    @Test
    public void testDeleteContactRejectsBlankIds() {
        service.addContact(contact1);
        assertAll("deleteContact rejects blank/null IDs",
            () -> assertThrows(IllegalArgumentException.class, () -> service.deleteContact(null)),
            () -> assertThrows(IllegalArgumentException.class, () -> service.deleteContact("   "))
        );
    }

    @Test
    public void testFindByLastNamePrefixFindsMatches() {
        service.addContact(contact1); // Smith
        service.addContact(contact2); // Jones
        service.addContact(new Contact("3", "Cara", "Smythe", "3333333333", "3 lane Rd"));

        assertAll("prefix search returns matching last names",
            () -> assertEquals(2, service.findByLastNamePrefix("Sm").size()),
            () -> assertEquals("Smith", service.findByLastNamePrefix("Sm").get(0).getLastName())
        );
    }

    @Test
    public void testFindByLastNamePrefixUpdatesIndexOnRename() {
        service.addContact(contact1); // Smith
        assertEquals(1, service.findByLastNamePrefix("Sm").size());

        service.updateLastName("1", "Jones");
        assertAll("index updates on last name change",
            () -> assertEquals(0, service.findByLastNamePrefix("Sm").size()),
            () -> assertEquals(1, service.findByLastNamePrefix("Jo").size())
        );
    }

    @Test
    public void testFindByLastNamePrefixRejectsBlankPrefix() {
        assertAll("prefix search rejects blank prefix",
            () -> assertThrows(IllegalArgumentException.class, () -> service.findByLastNamePrefix(null)),
            () -> assertThrows(IllegalArgumentException.class, () -> service.findByLastNamePrefix("   "))
        );
    }

    @Test
    public void testFindByLastNamePrefixRemovesOnDelete() {
        service.addContact(contact1); // Smith
        assertEquals(1, service.findByLastNamePrefix("Sm").size());

        service.deleteContact("1");
        assertEquals(0, service.findByLastNamePrefix("Sm").size());
    }

    @Test
    public void testFindByLastNamePrefixIncludesMultipleSameLastName() {
        service.addContact(contact1); // Alice Smith
        service.addContact(new Contact("3", "Aaron", "Smith", "3333333333", "3 lane Rd"));

        assertEquals(2, service.findByLastNamePrefix("Smi").size());
    }
}
