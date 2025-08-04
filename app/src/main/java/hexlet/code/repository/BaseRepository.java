package hexlet.code.repository;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Abstract base class for all repository implementations.
 * Provides common database operations and connection management.
 */
public abstract class BaseRepository {
    private final DataSource dataSource;

    /**
     * Constructs a new BaseRepository instance.
     * @param source the DataSource to use for database connections
     */
    protected BaseRepository(final DataSource source) {
        this.dataSource = source;
    }

    /**
     * Gets a database connection from the data source.
     * Caller is responsible for properly closing the connection.
     * @return a new database Connection
     * @throws SQLException if a database access error occurs
     */
    protected final Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    /**
     * Gets the data source used by this repository.
     * @return the DataSource instance
     */
    protected final DataSource getDataSource() {
        return dataSource;
    }
}