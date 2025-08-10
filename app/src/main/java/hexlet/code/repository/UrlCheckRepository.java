package hexlet.code.repository;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UrlCheckRepository extends BaseRepository {

    public static void save(UrlCheck urlCheck, Url url) throws SQLException {
        String sql = "INSERT INTO url_checks (url_id, status_code, title, h1, description, created_at) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        var now = new java.sql.Timestamp(System.currentTimeMillis());

        try (var conn = BaseRepository.getDataSource().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, url.getId());

            if (urlCheck.getStatusCode() == null) {
                ps.setNull(2, java.sql.Types.INTEGER);
            } else {
                ps.setInt(2, urlCheck.getStatusCode());
            }

            ps.setString(3, urlCheck.getTitle());
            ps.setString(4, urlCheck.getH1());
            ps.setString(5, urlCheck.getDescription());
            ps.setTimestamp(6, now);
            ps.executeUpdate();
        }
    }

    public static UrlCheck findLastByUrlId(long urlId) throws SQLException {
        var sql = """
        SELECT id, url_id, status_code, title, h1, description, created_at
        FROM url_checks
        WHERE url_id = ?
        ORDER BY created_at DESC
        LIMIT 1
        """;

        try (var conn = BaseRepository.getDataSource().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, urlId);

            try (var rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }


                var url = new hexlet.code.model.Url();
                url.setId(urlId);

                var title = rs.getString("title");
                var h1 = rs.getString("h1");
                var description = rs.getString("description");

                var uc = new hexlet.code.model.UrlCheck(url, title, h1, description);
                uc.setId(rs.getLong("id"));
                uc.setStatusCode(rs.getInt("status_code"));
                uc.setCreatedAt(rs.getTimestamp("created_at"));
                return uc;
            }
        }
    }

    public static List<UrlCheck> getEntitiesByUrl(Url url) throws SQLException {
        var sql = "SELECT id, status_code, title, h1, description, created_at "
                + "FROM url_checks "
                + "WHERE url_id = ? "
                + "ORDER BY id DESC";
        try (var conn = BaseRepository.getDataSource().getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setLong(1, url.getId());
            try (var rs = ps.executeQuery()) {
                var urlChecks = new ArrayList<UrlCheck>();
                while (rs.next()) {
                    var uc = new UrlCheck(
                            url,
                            rs.getString("title"),
                            rs.getString("h1"),
                            rs.getString("description")
                    );
                    uc.setId(rs.getLong("id"));
                    uc.setStatusCode(rs.getInt("status_code"));
                    uc.setCreatedAt(rs.getTimestamp("created_at"));
                    urlChecks.add(uc);
                }
                return urlChecks;
            }
        }
    }
}
