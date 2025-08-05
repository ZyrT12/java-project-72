package hexlet.code.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import javax.sql.DataSource;

public class DataBase {
    private static HikariDataSource dataSource;

    public static void init() {
        String jdbcUrl = System.getenv().getOrDefault(
                "JDBC_DATABASE_URL",
                "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1"
        );

        try {
            // Явная регистрация драйвера
            if (jdbcUrl.contains("postgresql")) {
                Class.forName("org.postgresql.Driver");
            } else {
                Class.forName("org.h2.Driver");
            }
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Database driver not found", e);
        }

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);

        if (jdbcUrl.contains("postgresql")) {
            config.setUsername(System.getenv().getOrDefault("DB_USER", "postgres"));
            config.setPassword(System.getenv().getOrDefault("DB_PASSWORD", "postgres"));
            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        }

        dataSource = new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        if (dataSource == null) {
            init();
        }
        return dataSource;
    }
}
