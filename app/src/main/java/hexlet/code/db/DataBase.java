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

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);

        if (jdbcUrl.contains("postgresql")) {
            config.setDriverClassName("org.postgresql.Driver");
        } else {
            config.setDriverClassName("org.h2.Driver");
        }

        dataSource = new HikariDataSource(config);
    }

    public static DataSource getDataSource() {
        return dataSource;
    }
}
