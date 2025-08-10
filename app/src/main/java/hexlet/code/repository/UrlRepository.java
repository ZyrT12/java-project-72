package hexlet.code.repository;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class UrlRepository extends BaseRepository {

    public static void save(Url url) throws SQLException {
        String sql = "INSERT INTO urls (name, created_at) VALUES (?, ?)";
        var now = new Timestamp(System.currentTimeMillis());
        try (var conn = BaseRepository.getDataSource().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setString(1, url.getName());
            ps.setTimestamp(2, now);
            ps.executeUpdate();
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
                    url = new Url(rs.getString("name"));
                    url.setId(rs.getLong("id"));
                    url.setCreatedAt(rs.getTimestamp("created_at"));
                }
            }
        }

        if (url == null) {
            return Optional.empty();
        }

        var last = UrlCheckRepository.findLastByUrlId(url.getId());
        if (last != null) {
            url.setLastCheck(last);
        }
        return Optional.of(url);
    }

    // Для таблицы /urls — переносимый SQL (H2/PG), без DISTINCT ON
    public static List<Url> getUrlsAndLastCheck() throws SQLException {
        var sql = """
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

        var urls = new ArrayList<Url>();
        try (var conn = BaseRepository.getDataSource().getConnection();
             var ps = conn.prepareStatement(sql);
             var rs = ps.executeQuery()) {

            while (rs.next()) {
                var url = new Url(rs.getString("name"));
                url.setId(rs.getLong("url_id"));
                url.setCreatedAt(rs.getTimestamp("url_created_at"));

                var statusObj = rs.getObject("status_code");
                if (statusObj != null) {
                   var uc = new UrlCheck(null, null, null, null);
                    uc.setStatusCode((Integer) statusObj);
                    uc.setCreatedAt(rs.getTimestamp("check_created_at"));
                    url.setLastCheck(uc);
                }
                urls.add(url);
            }
        }
        return urls;
    }

    public static List<Url> getEntities() throws SQLException {
        var sql = "SELECT id, name FROM urls ORDER BY id";
        try (var conn = BaseRepository.getDataSource().getConnection();
             var ps = conn.prepareStatement(sql);
             var rs = ps.executeQuery()) {
            var result = new ArrayList<Url>();
            while (rs.next()) {
                var url = new Url(rs.getString("name"));
                url.setId(rs.getLong("id"));
                result.add(url);
            }
            return result;
        }
    }
}
