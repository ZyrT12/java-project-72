package hexlet.code.db;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

public class Migration {
    public static void run(DataSource dataSource) {
        String sql = "CREATE TABLE IF NOT EXISTS urls ("
                + "id BIGSERIAL PRIMARY KEY,"
                + "name VARCHAR(255) NOT NULL,"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")";
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
