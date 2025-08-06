package hexlet.code.repository;

import hexlet.code.model.UrlCheck;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

public final class UrlCheckRepository {
    private final DataSource dataSource;

    public UrlCheckRepository(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(UrlCheck check) throws SQLException {
        String sql = """
            INSERT INTO url_checks (url_id, status_code, title, h1, description, created_at)
            VALUES (?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, check.getUrlId());
            stmt.setInt(2, check.getStatusCode());
            stmt.setString(3, check.getTitle());
            stmt.setString(4, check.getH1());
            stmt.setString(5, check.getDescription());
            stmt.setTimestamp(6, Timestamp.valueOf(check.getCreatedAt()));
            stmt.executeUpdate();
        }
    }

    public List<UrlCheck> findByUrlId(long urlId) throws SQLException {
        String sql = """
            SELECT * FROM url_checks
            WHERE url_id = ?
            ORDER BY created_at DESC
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, urlId);
            ResultSet rs = stmt.executeQuery();

            List<UrlCheck> checks = new ArrayList<>();
            while (rs.next()) {
                checks.add(new UrlCheck(
                        rs.getLong("id"),
                        rs.getLong("url_id"),
                        rs.getInt("status_code"),
                        rs.getString("title"),
                        rs.getString("h1"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
            return checks;
        }
    }

    public Map<Long, UrlCheck> findLatestChecks() throws SQLException {
        String sql = """
            SELECT DISTINCT ON (url_id) *
            FROM url_checks
            ORDER BY url_id, created_at DESC
        """;

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();

            Map<Long, UrlCheck> checks = new HashMap<>();
            while (rs.next()) {
                checks.put(rs.getLong("url_id"), new UrlCheck(
                        rs.getLong("id"),
                        rs.getLong("url_id"),
                        rs.getInt("status_code"),
                        rs.getString("title"),
                        rs.getString("h1"),
                        rs.getString("description"),
                        rs.getTimestamp("created_at").toLocalDateTime()
                ));
            }
            return checks;
        }
    }
}
