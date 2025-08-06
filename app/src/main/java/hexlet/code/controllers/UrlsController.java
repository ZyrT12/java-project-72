package hexlet.code.controllers;

import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;

import hexlet.code.dto.UrlsPage;

import java.sql.SQLException;
import java.util.Collections;
public final class UrlsController {
    public static void index(Context ctx) throws SQLException {
        var page = new UrlsPage(UrlRepository.getUrlsAndLastCheck());
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("urls/index.jte", Collections.singletonMap("page", page));
    }
}
