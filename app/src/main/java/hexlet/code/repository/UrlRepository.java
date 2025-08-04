package hexlet.code.repository;

import hexlet.code.model.Url;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Repository class for handling URL entities in database.
 * Provides CRUD operations for URLs.
 */
public final class UrlRepository extends BaseRepository {

    /**
     * Constructs new UrlRepository instance.
     * @param dataSource DataSource for database connections
     */
    public UrlRepository(final DataSource dataSource) {
        super(dataSource);
    }

    /**
     * Saves URL entity to database.
     * @param url URL to save
     * @throws SQLException if database error occurs
     */
    public void save(final Url url) throws SQLException {
        final String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, url.getName());
            stmt.setTimestamp(2, url.getCreatedAt());
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    url.setId(generatedKeys.getLong(1));
                }
            }
        }
    }

    /**
     * Finds URL by its ID.
     * @param id ID of URL to find
     * @return Optional containing URL if found
     * @throws SQLException if database error occurs
     */
    public Optional<Url> findById(final Long id) throws SQLException {
        final String sql = "SELECT * FROM urls WHERE id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Url(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getTimestamp("created_at")
                    ));
                }
                return Optional.empty();
            }
        }
    }

    /**
     * Retrieves all URLs from database ordered by ID descending.
     * @return List of all URLs
     * @throws SQLException if database error occurs
     */
    public List<Url> findAll() throws SQLException {
        final String sql = "SELECT * FROM urls ORDER BY id DESC";
        final List<Url> urls = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                urls.add(new Url(
                        rs.getLong("id"),
                        rs.getString("name"),
                        rs.getTimestamp("created_at")
                ));
            }
        }
        return urls;
    }

    /**
     * Finds URL by its name (normalized URL).
     * @param name Normalized URL name to search for
     * @return Optional containing URL if found
     * @throws SQLException if database error occurs
     */
    public Optional<Url> findByName(final String name) throws SQLException {
        final String sql = "SELECT * FROM urls WHERE name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(new Url(
                            rs.getLong("id"),
                            rs.getString("name"),
                            rs.getTimestamp("created_at")
                    ));
                }
                return Optional.empty();
            }
        }
    }
}
