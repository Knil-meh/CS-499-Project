import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;

public class ContactService {
    // Attributes
    private Map<String, Contact> contacts = new HashMap<>(); // HashMap to store the contacts
    private NavigableMap<String, Set<String>> lastNameIndex = new TreeMap<>();
    private final ContactRepository repository;

    public ContactService() {
        this(ContactRepository.defaultFile());
    }

    public ContactService(ContactRepository repository) {
        this.repository = repository;
        reloadFromDatabase();
    }

    private void reloadFromDatabase() {
        contacts.clear();
        lastNameIndex.clear();
        for (Contact c : repository.findAll()) {
            contacts.put(c.getContactId(), c);
            index(c);
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private void index(Contact contact) {
        lastNameIndex.computeIfAbsent(contact.getLastName(), k -> new HashSet<>()).add(contact.getContactId());
    }

    private void deindex(Contact contact) {
        Set<String> ids = lastNameIndex.get(contact.getLastName());
        if (ids == null) {
            return;
        }
        ids.remove(contact.getContactId());
        if (ids.isEmpty()) {
            lastNameIndex.remove(contact.getLastName());
        }
    }

    private void updateContact(String contactId, Function<Contact, Contact> updater) {
        if (isBlank(contactId)) {
            throw new IllegalArgumentException("contactId must not be blank");
        }
        Contact contact = contacts.get(contactId);
        if (contact == null) {
            throw new ContactNotFoundException(contactId);
        }
        Contact updated = updater.apply(contact);
        repository.update(updated);
        deindex(contact);
        contacts.put(contactId, updated);
        index(updated);
    }

    public Contact getContact(String contactId) {
        if (isBlank(contactId)) {
            throw new IllegalArgumentException("contactId must not be blank");
        }
        Contact contact = contacts.get(contactId);
        if (contact == null) {
            throw new ContactNotFoundException(contactId);
        }
        return contact;
    }

    public List<Contact> getAllContacts() {
        List<Contact> list = new ArrayList<>(contacts.values());
        list.sort(Comparator.comparing(Contact::getContactId));
        return Collections.unmodifiableList(list);
    }

    // Add a new contact
    public void addContact(Contact contact) {
        if (contact == null) {
            throw new IllegalArgumentException("contact must not be null");
        }
        if (contacts.containsKey(contact.getContactId())) {
            throw new DuplicateContactIdException(contact.getContactId());
        }
        repository.insert(contact);
        contacts.put(contact.getContactId(), contact);
        index(contact);
    }

    // Delete a contact by ID
    public void deleteContact(String contactId) {
        if (isBlank(contactId)) {
            throw new IllegalArgumentException("contactId must not be blank");
        }
        if (!contacts.containsKey(contactId)) {
            throw new ContactNotFoundException(contactId);
        }
        Contact removed = contacts.get(contactId);
        repository.deleteById(contactId);
        contacts.remove(contactId);
        deindex(removed);
    }

    public List<Contact> findByLastNamePrefix(String prefix) {
        if (isBlank(prefix)) {
            throw new IllegalArgumentException("prefix must not be blank");
        }

        String fromKey = prefix;
        String toKey = prefix + Character.MAX_VALUE;

        List<Contact> results = new ArrayList<>();
        for (Set<String> ids : lastNameIndex.subMap(fromKey, true, toKey, true).values()) {
            for (String id : ids) {
                results.add(getContact(id));
            }
        }

        results.sort(Comparator
                .comparing(Contact::getLastName)
                .thenComparing(Contact::getFirstName)
                .thenComparing(Contact::getContactId));
        return results;
    }

    // Update first name by contact ID
    public void updateFirstName(String contactId, String newFirstName) {
        updateContact(contactId, contact -> new Contact(
                contact.getContactId(),
                newFirstName,
                contact.getLastName(),
                contact.getPhoneNumber(),
                contact.getAddress()
        ));
    }

    // Update last name by contact ID
    public void updateLastName(String contactId, String newLastName) {
        updateContact(contactId, contact -> new Contact(
                contact.getContactId(),
                contact.getFirstName(),
                newLastName,
                contact.getPhoneNumber(),
                contact.getAddress()
        ));
    }

    // Update phone number by contact ID
    public void updatePhoneNumber(String contactId, String newPhoneNumber) {
        updateContact(contactId, contact -> new Contact(
                contact.getContactId(),
                contact.getFirstName(),
                contact.getLastName(),
                newPhoneNumber,
                contact.getAddress()
        ));
    }

    // Update address by contact ID
    public void updateAddress(String contactId, String newAddress) {
        updateContact(contactId, contact -> new Contact(
                contact.getContactId(),
                contact.getFirstName(),
                contact.getLastName(),
                contact.getPhoneNumber(),
                newAddress
        ));
    }


}
