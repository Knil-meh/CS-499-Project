import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

public class ContactPersistenceTest {

    @Test
    public void contactsReloadFromDatabaseFile(@TempDir Path tempDir) {
        Path dbFile = tempDir.resolve("persist.db");
        String jdbcUrl = "jdbc:sqlite:" + dbFile.toAbsolutePath();

        ContactService first = new ContactService(new ContactRepository(jdbcUrl));
        first.addContact(new Contact("9", "Pat", "Lee", "9999999999", "9 Oak Ln"));

        ContactService second = new ContactService(new ContactRepository(jdbcUrl));
        assertAll("data survives new ContactService",
                () -> assertEquals("Pat", second.getContact("9").getFirstName()),
                () -> assertEquals("Lee", second.getContact("9").getLastName())
        );
    }
}
