package hexlet.code.repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Base repository class providing common database operations.
 * Designed for extension with proper documentation.
 */
public class BaseRepository {
    private final DataSource dataSource;

    /**
     * Constructs a BaseRepository with specified data source.
     * @param source the DataSource to be used for connections
     */
    public BaseRepository(final DataSource source) {
        this.dataSource = source;
    }

    /**
     * Gets the data source instance.
     * @return the configured DataSource
     */
    public DataSource getDataSource() {
        return dataSource;
    }

    /**
     * Gets a database connection from the data source.
     * Caller is responsible for closing the connection.
     * @return a new database Connection
     * @throws SQLException if a database access error occurs
     */
    public Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
