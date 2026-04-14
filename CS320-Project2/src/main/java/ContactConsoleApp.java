import java.util.Scanner;

public class ContactConsoleApp {

    public static void main(String[] args) {
        ContactService service = new ContactService();
        Scanner scanner = new Scanner(System.in);

        System.out.println("Contact manager (data saved to contacts.db in this folder)");
        while (true) {
            System.out.println();
            System.out.println("1) Create a contact");
            System.out.println("2) Edit a contact");
            System.out.println("3) Delete a contact");
            System.out.println("4) List all contacts");
            System.out.println("5) Exit");
            System.out.print("Choice: ");
            String line = scanner.nextLine().trim();
            switch (line) {
                case "1" -> addContactFlow(service, scanner);
                case "2" -> editContactFlow(service, scanner);
                case "3" -> deleteContactFlow(service, scanner);
                case "4" -> listContacts(service);
                case "5" -> {
                    System.out.println("Goodbye.");
                    return;
                }
                default -> System.out.println("Invalid choice. Enter 1 through 5.");
            }
        }
    }

    private static void addContactFlow(ContactService service, Scanner scanner) {
        try {
            System.out.print("Contact ID (max 10 chars, unique): ");
            String id = scanner.nextLine().trim();
            System.out.print("First name (max 10 chars): ");
            String first = scanner.nextLine().trim();
            System.out.print("Last name (max 10 chars): ");
            String last = scanner.nextLine().trim();
            System.out.print("Phone (exactly 10 digits): ");
            String phone = scanner.nextLine().trim();
            System.out.print("Address (max 30 chars): ");
            String address = scanner.nextLine().trim();

            Contact contact = new Contact(id, first, last, phone, address);
            service.addContact(contact);
            System.out.println("Contact saved.");
        } catch (DuplicateContactIdException e) {
            System.out.println("That contact ID is already in use.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid input: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.out.println("Could not save to database: " + e.getMessage());
        }
    }

    private static void editContactFlow(ContactService service, Scanner scanner) {
        System.out.print("Contact ID to edit: ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println("ID cannot be empty.");
            return;
        }
        try {
            service.getContact(id);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid ID: " + e.getMessage());
            return;
        } catch (ContactNotFoundException e) {
            System.out.println("No contact with that ID.");
            return;
        }

        while (true) {
            System.out.println();
            System.out.println("Edit contact \"" + id + "\"");
            System.out.println("  1) First name");
            System.out.println("  2) Last name");
            System.out.println("  3) Phone number");
            System.out.println("  4) Address");
            System.out.println("  5) Done editing");
            System.out.print("Choice: ");
            String choice = scanner.nextLine().trim();
            try {
                switch (choice) {
                    case "1" -> {
                        System.out.print("New first name (max 10 chars): ");
                        service.updateFirstName(id, scanner.nextLine().trim());
                        System.out.println("Updated.");
                    }
                    case "2" -> {
                        System.out.print("New last name (max 10 chars): ");
                        service.updateLastName(id, scanner.nextLine().trim());
                        System.out.println("Updated.");
                    }
                    case "3" -> {
                        System.out.print("New phone (exactly 10 digits): ");
                        service.updatePhoneNumber(id, scanner.nextLine().trim());
                        System.out.println("Updated.");
                    }
                    case "4" -> {
                        System.out.print("New address (max 30 chars): ");
                        service.updateAddress(id, scanner.nextLine().trim());
                        System.out.println("Updated.");
                    }
                    case "5" -> {
                        return;
                    }
                    default -> System.out.println("Enter 1 through 5.");
                }
            } catch (ContactNotFoundException e) {
                System.out.println("Contact no longer exists.");
                return;
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid value: " + e.getMessage());
            } catch (IllegalStateException e) {
                System.out.println("Could not update database: " + e.getMessage());
            }
        }
    }

    private static void deleteContactFlow(ContactService service, Scanner scanner) {
        System.out.print("Contact ID to delete: ");
        String id = scanner.nextLine().trim();
        if (id.isEmpty()) {
            System.out.println("ID cannot be empty.");
            return;
        }
        System.out.print("Type DELETE to confirm: ");
        if (!"DELETE".equals(scanner.nextLine().trim())) {
            System.out.println("Cancelled.");
            return;
        }
        try {
            service.deleteContact(id);
            System.out.println("Contact deleted.");
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid ID: " + e.getMessage());
        } catch (ContactNotFoundException e) {
            System.out.println("No contact with that ID.");
        } catch (IllegalStateException e) {
            System.out.println("Could not update database: " + e.getMessage());
        }
    }

    private static void listContacts(ContactService service) {
        var all = service.getAllContacts();
        if (all.isEmpty()) {
            System.out.println("(No contacts stored.)");
            return;
        }
        for (Contact c : all) {
            System.out.println("---");
            System.out.println(c);
        }
    }
}
