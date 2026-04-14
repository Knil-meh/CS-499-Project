public class Contact {
    // Attributes
    private String contactId;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;

    private static boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    // Constructor
    public Contact(String contactId, String firstName, String lastName, String phoneNumber, String address) {
        // Attribte's restrictions
        // Each ensure that the attribute is not null and that the length is within the required range
        if (isBlank(contactId) || contactId.length() > 10) {
            throw new IllegalArgumentException("Contact ID must be less than 10 characters");
        }
        if (isBlank(firstName) || firstName.length() > 10) {
            throw new IllegalArgumentException("First name must be less than 10 characters");
        }
        if (isBlank(lastName) || lastName.length() > 10) {
            throw new IllegalArgumentException("Last name must be less than 10 characters");
        }
        if (phoneNumber == null || !phoneNumber.matches("\\d{10}")) {
            throw new IllegalArgumentException("Phone number must be 10 digits");
        }
        if (isBlank(address) || address.length() > 30) {
            throw new IllegalArgumentException("Address must be less than 30 characters");
        }
        // Set the attributes
        this.contactId = contactId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    // Getters
    public String getContactId() {
        return contactId;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getAddress() {
        return address;
    }
    
    // toString method
    // Returns a string representation of the contact
    public String toString() {
        return "Contact ID: " + contactId + "\n" +
               "First Name: " + firstName + "\n" +
               "Last Name: " + lastName + "\n" +
               "Phone Number: " + phoneNumber + "\n" +
               "Address: " + address;
    }   
}
