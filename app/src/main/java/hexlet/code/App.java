package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import hexlet.code.controllers.RootController;
import hexlet.code.controllers.UrlCheckController;
import hexlet.code.controllers.UrlsController;
import hexlet.code.repository.BaseRepository;
import hexlet.code.utils.NamedRoutes;
import hexlet.code.utils.TemplateResolve;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;

@Slf4j
public final class App {
    private static final String DEFAULT_PORT = "7070";
    private static final String DEFAULT_DB_URL = "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1";

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", DEFAULT_PORT);
        return Integer.parseInt(port);
    }

    public static String readResourceFile(String filePath) throws IOException {
        try (InputStream inputStream = App.class.getClassLoader().getResourceAsStream(filePath);
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }


    public static Javalin getApp() throws IOException, SQLException {

        log.info("Starting application initialization");

        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(TemplateResolve.createTemplateEngine()));
        });

        app.get("/style.css", ctx -> {
            ctx.contentType("text/css");
            InputStream cssStream = App.class.getResourceAsStream("/templates/style.css");
            if (cssStream != null) {
                ctx.result(cssStream);
            } else {
                ctx.status(404).result("CSS file not found");
            }
        });

        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getDBUrl());

        hikariConfig.setMaximumPoolSize(1);
        hikariConfig.setMinimumIdle(0);
        hikariConfig.setIdleTimeout(10_000);
        hikariConfig.setConnectionTimeout(5_000);

        log.debug("Database URL: {}", hikariConfig.getJdbcUrl());
        var dataSource = new HikariDataSource(hikariConfig);

        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            log.info("Executing database schema initialization");
            var sql = readResourceFile("db/schema.sql");
            statement.execute(sql);
            log.info("Database initialized successfully");
        } catch (SQLException e) {
            log.error("Database initialization failed", e);
            throw e;
        }

        BaseRepository.setDataSource(dataSource);

        app.get(NamedRoutes.home(), RootController::home);
        app.post(NamedRoutes.urls(), RootController::addUrl);
        app.get(NamedRoutes.urls(), UrlsController::index);
        app.get(NamedRoutes.urlPath("{id}"), UrlCheckController::show);
        app.post(NamedRoutes.urlCheck("{id}"), UrlCheckController::check);

        app.before(ctx -> {
            log.info("Request: {} {} | Body: {}", ctx.method(), ctx.path(), ctx.body());
        });

        app.after(ctx -> {
            log.info("Response: {} | Status: {}", ctx.path(), ctx.status());
        });

        app.exception(Exception.class, (e, ctx) -> {
            log.error("Unhandled exception", e);
            ctx.status(500);
        });

        log.info("Application initialized successfully");

        return app;
    }

    public static String getDBUrl() {
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", DEFAULT_DB_URL);
    }

    public static void main(String[] args) throws SQLException, IOException {
        var app = getApp();
        app.start(getPort());
    }
}
