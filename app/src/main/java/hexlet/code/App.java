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
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
public final class App {
    private static final String SQL_FILEPATH = "schema.sql";

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }

    public static String readResourceFile(String filePath) throws IOException {
        var inputStream = Optional.ofNullable(App.class.getClassLoader().getResourceAsStream(filePath));

        if (inputStream.isEmpty()) {
            throw new IOException();
        }
        var streamReader = new InputStreamReader(inputStream.get(), StandardCharsets.UTF_8);

        try (BufferedReader reader = new BufferedReader(streamReader)) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    public static Javalin getApp() throws IOException, SQLException {
        var app = Javalin.create(javalinConfig -> {
            javalinConfig.bundledPlugins.enableDevLogging();
            javalinConfig.fileRenderer(new JavalinJte(TemplateResolve.createTemplateEngine()));
            javalinConfig.staticFiles.add("/static");
        });

        var hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(getDBUrl());

        var dataSource = new HikariDataSource(hikariConfig);
        var sql = readResourceFile(App.SQL_FILEPATH);

        log.info(sql);
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }

        BaseRepository.setDataSource(dataSource);

        app.get(NamedRoutes.home(), RootController::home);
        app.post(NamedRoutes.urls(), RootController::addUrl);
        app.get(NamedRoutes.urls(), UrlsController::index);
        app.get(NamedRoutes.urlPath("{id}"), UrlCheckController::show);
        app.post(NamedRoutes.urlCheck("{id}"), UrlCheckController::check);

        return app;
    }

    public static String getDBUrl() {
        var dbUrl = "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;";
        return System.getenv().getOrDefault("JDBC_DATABASE_URL", dbUrl);
    }

    public static void main(String[] args) throws SQLException, IOException {
        var app = getApp();
        app.start(getPort());
    }
}
