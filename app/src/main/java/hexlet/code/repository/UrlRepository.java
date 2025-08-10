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
        var datetime = new Timestamp(System.currentTimeMillis());
        try (var conn = BaseRepository.getDataSource().getConnection();
             var preparedStatement = conn.prepareStatement(sql)) {
            preparedStatement.setString(1, url.getName());
            preparedStatement.setTimestamp(2, datetime);
            preparedStatement.executeUpdate();
        }
    }

    public static Optional<Url> findById(Long id) throws SQLException {
        String sql = "SELECT id, name, created_at FROM urls WHERE id = ?";
        try (var conn = BaseRepository.getDataSource().getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, id);
            var resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                var idd = resultSet.getLong(1);
                var name = resultSet.getString(2);
                var createdAt = resultSet.getTimestamp(3);
                var url = new Url(name);
                url.setId(idd);
                url.setCreatedAt(createdAt);

                return Optional.of(url);
            }
            return Optional.empty();
        }
    }

    public static boolean isExist(String name) throws SQLException {
        String sql = "SELECT id FROM urls WHERE name = ?";
        try (var conn = BaseRepository.getDataSource().getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            var resultSet = stmt.executeQuery();

            return resultSet.next();
        }
    }

    public static List<Url> getEntities() throws SQLException {
        var sql = "SELECT id, name FROM urls ORDER BY id";
        try (var conn = BaseRepository.getDataSource().getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var resultSet = stmt.executeQuery();
            var result = new ArrayList<Url>();
            while (resultSet.next()) {
                var id = resultSet.getLong("id");
                var name = resultSet.getString("name");
                var url = new Url(name);
                url.setId(id);
                result.add(url);
            }
            return result;
        }
    }

    public static List<Url> getUrlsAndLastCheck() throws SQLException {
        var sql = """
        SELECT DISTINCT ON (u.id)
            u.id AS url_id,
            u.name AS name,
            c.status_code AS status_code,
            c.created_at AS created_at
        FROM urls u
        LEFT JOIN url_checks c ON c.url_id = u.id
        ORDER BY u.id, c.created_at DESC NULLS LAST
        """;

        var urls = new ArrayList<Url>();
        try (var conn = BaseRepository.getDataSource().getConnection();
             var stmt = conn.prepareStatement(sql)) {
            var rs = stmt.executeQuery();
            while (rs.next()) {
                var url = new Url(rs.getString("name"));
                url.setId(rs.getLong("url_id"));

                var status = rs.getObject("status_code"); // может быть NULL
                if (status != null) {
                    var uc = new UrlCheck();
                    uc.setStatusCode((Integer) status);
                    uc.setCreatedAt(rs.getTimestamp("created_at"));
                    url.setLastCheck(uc);
                }
                urls.add(url);
            }
        }
        return urls;
    }
}
