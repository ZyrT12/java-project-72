package hexlet.code;

import hexlet.code.db.DataBase;
import hexlet.code.db.Migration;
import io.javalin.Javalin;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class App {

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }

    public static Javalin getApp() {
        return Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
        }).get("/", ctx -> {
            ctx.result("Hello World");
        });
    }

    public static void main(String[] args) {
        DataBase.init();
        Migration.run(DataBase.getDataSource());
        var app = getApp();
        app.start(getPort());
    }
}
