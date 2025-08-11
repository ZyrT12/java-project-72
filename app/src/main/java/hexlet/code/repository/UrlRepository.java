package hexlet.code.repository;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlRepository extends BaseRepository {

    public static void save(Url url) throws SQLException {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        LocalDateTime now = url.getCreatedAt() != null ? url.getCreatedAt() : LocalDateTime.now();

        try (var conn = BaseRepository.getDataSource().getConnection();
             var ps = conn.prepareStatement(sql, java.sql.Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, url.getName());
            ps.setTimestamp(2, Timestamp.valueOf(now));
            ps.executeUpdate();

            try (var keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    url.setId(keys.getLong(1));
                }
            }
        }
    }

    public static boolean isExist(String name) throws SQLException {
        String sql = "SELECT id FROM urls WHERE name = ?";
        try (var conn = BaseRepository.getDataSource().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            try (var rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public static Optional<Url> findById(Long id) throws SQLException {
        String sql = "SELECT id, name, created_at FROM urls WHERE id = ?";

        Url url = null;
        try (var conn = BaseRepository.getDataSource().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) {
                    url = map(rs);
                }
            }
        }
        if (url == null) {
            return Optional.empty();
        }

        var lastOpt = UrlCheckRepository.findLatestByUrlId(url.getId());
        lastOpt.ifPresent(url::setLastCheck);

        return Optional.of(url);
    }

    public static List<Url> getUrlsAndLastCheck() throws SQLException {
        String sql = """
            SELECT u.id          AS url_id,
                   u.name        AS name,
                   u.created_at  AS url_created_at,
                   c.status_code AS status_code,
                   c.created_at  AS check_created_at
            FROM urls u
            LEFT JOIN (
                SELECT url_id, status_code, created_at,
                       ROW_NUMBER() OVER (PARTITION BY url_id ORDER BY created_at DESC) AS rn
                FROM url_checks
            ) c ON c.url_id = u.id AND c.rn = 1
            ORDER BY u.id
            """;

        List<Url> urls = new ArrayList<>();
        try (var conn = BaseRepository.getDataSource().getConnection();
             var ps = conn.prepareStatement(sql);
             var rs = ps.executeQuery()) {

            while (rs.next()) {
                Url url = new Url(rs.getString("name"));
                url.setId(rs.getLong("url_id"));
                var urlTs = rs.getTimestamp("url_created_at");
                url.setCreatedAt(urlTs != null ? urlTs.toLocalDateTime() : null);

                var statusObj = rs.getObject("status_code");
                if (statusObj != null) {
                    var uc = new UrlCheck();
                    uc.setStatusCode((Integer) statusObj);
                    var checkTs = rs.getTimestamp("check_created_at");
                    uc.setCreatedAt(checkTs != null ? checkTs.toLocalDateTime() : null);
                    url.setLastCheck(uc);
                }
                urls.add(url);
            }
        }
        return urls;
    }

    public static List<Url> getEntities() throws SQLException {
        String sql = "SELECT id, name FROM urls ORDER BY id";
        try (var conn = BaseRepository.getDataSource().getConnection();
             var ps = conn.prepareStatement(sql);
             var rs = ps.executeQuery()) {
            List<Url> result = new ArrayList<>();
            while (rs.next()) {
                Url url = new Url(rs.getString("name"));
                url.setId(rs.getLong("id"));
                result.add(url);
            }
            return result;
        }
    }

    private static Url map(ResultSet rs) throws SQLException {
        var url = new Url(rs.getString("name"));
        url.setId(rs.getLong("id"));
        var ts = rs.getTimestamp("created_at");
        url.setCreatedAt(ts != null ? ts.toLocalDateTime() : null);
        return url;
    }
}
