package hexlet.code;

import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.controllers.UrlsController;
import hexlet.code.controllers.UrlCheckController;
import hexlet.code.db.DataBase;
import hexlet.code.db.Migration;
import hexlet.code.repository.UrlRepository;
import hexlet.code.repository.UrlCheckRepository;
import io.javalin.Javalin;
import io.javalin.rendering.template.JavalinJte;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class App {

    private static final int INTERNAL_SERVER_ERROR = 500;

    private static int getPort() {
        String port = System.getenv().getOrDefault("PORT", "7070");
        return Integer.parseInt(port);
    }

    public static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        return TemplateEngine.create(codeResolver, ContentType.Html);
    }

    public static Javalin getApp() {
        var dataSource = DataBase.getDataSource();
        var urlRepository = new UrlRepository(dataSource);
        var urlCheckRepository = new UrlCheckRepository(dataSource);

        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });

        app.get("/", ctx -> {
            String flash = ctx.consumeSessionAttribute("flash");
            String flashType = ctx.consumeSessionAttribute("flashType");

            Map<String, Object> model = new HashMap<>();
            model.put("flash", flash);
            model.put("flashType", flashType);

            try {
                ctx.render("index.jte", model);
            } catch (Exception e) {
                ctx.status(INTERNAL_SERVER_ERROR).result("Template rendering error: " + e.getMessage());
                e.printStackTrace();
            }
        });

        var urlController = new UrlsController(urlRepository);
        var urlCheckController = new UrlCheckController(urlRepository, urlCheckRepository);

        app.get("/urls", urlController::index);
        app.get("/urls/{id}", urlController::show);
        app.post("/urls", urlController::create);
        app.post("/urls/{id}", urlCheckController::check); // <--- добавлено

        return app;
    }

    public static void main(String[] args) throws Exception {
        DataBase.init();
        Migration.run(DataBase.getDataSource());

        var app = getApp();
        app.start(getPort());
        log.info("App started on port {}", getPort());
    }
}
