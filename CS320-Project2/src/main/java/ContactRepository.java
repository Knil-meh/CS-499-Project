import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ContactRepository {

    private final String jdbcUrl;

    public ContactRepository(String jdbcUrl) {
        this.jdbcUrl = jdbcUrl;
        ensureSchema();
    }

    public static ContactRepository defaultFile() {
        return new ContactRepository("jdbc:sqlite:contacts.db");
    }

    private Connection connect() throws SQLException {
        return DriverManager.getConnection(jdbcUrl);
    }

    private void ensureSchema() {
        String sql = """
                CREATE TABLE IF NOT EXISTS contacts (
                    contact_id TEXT PRIMARY KEY,
                    first_name TEXT NOT NULL,
                    last_name TEXT NOT NULL,
                    phone_number TEXT NOT NULL,
                    address TEXT NOT NULL
                )
                """;
        try (Connection conn = connect(); Statement st = conn.createStatement()) {
            st.execute(sql);
        } catch (SQLException e) {
            throw new IllegalStateException("Could not initialize contacts table", e);
        }
    }

    public void insert(Contact contact) {
        String sql = """
                INSERT INTO contacts (contact_id, first_name, last_name, phone_number, address)
                VALUES (?, ?, ?, ?, ?)
                """;
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contact.getContactId());
            ps.setString(2, contact.getFirstName());
            ps.setString(3, contact.getLastName());
            ps.setString(4, contact.getPhoneNumber());
            ps.setString(5, contact.getAddress());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Could not insert contact: " + contact.getContactId(), e);
        }
    }

    public void update(Contact contact) {
        String sql = """
                UPDATE contacts SET first_name = ?, last_name = ?, phone_number = ?, address = ?
                WHERE contact_id = ?
                """;
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contact.getFirstName());
            ps.setString(2, contact.getLastName());
            ps.setString(3, contact.getPhoneNumber());
            ps.setString(4, contact.getAddress());
            ps.setString(5, contact.getContactId());
            int rows = ps.executeUpdate();
            if (rows != 1) {
                throw new IllegalStateException("Update affected unexpected row count: " + rows);
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not update contact: " + contact.getContactId(), e);
        }
    }

    public void deleteById(String contactId) {
        String sql = "DELETE FROM contacts WHERE contact_id = ?";
        try (Connection conn = connect(); PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, contactId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("Could not delete contact: " + contactId, e);
        }
    }

    public List<Contact> findAll() {
        String sql = "SELECT contact_id, first_name, last_name, phone_number, address FROM contacts ORDER BY contact_id";
        List<Contact> list = new ArrayList<>();
        try (Connection conn = connect();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Contact(
                        rs.getString("contact_id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("phone_number"),
                        rs.getString("address")
                ));
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Could not load contacts", e);
        }
        return list;
    }
}
