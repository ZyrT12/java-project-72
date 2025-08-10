package hexlet.code.controllers;

import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class UrlsController {
    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.getUrlsAndLastCheck();

        var flash = ctx.consumeSessionAttribute("flash");
        var flashType = ctx.consumeSessionAttribute("flashType");

        Map<String, Object> model = new HashMap<>();
        model.put("urls", urls);
        model.put("flash", flash);
        model.put("flashType", flashType);

        ctx.render("urls/index.jte", model);
    }
}
