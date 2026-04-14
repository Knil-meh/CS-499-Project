public class DuplicateContactIdException extends RuntimeException {
    public DuplicateContactIdException(String contactId) {
        super("Contact with ID already exists: " + contactId);
    }
}