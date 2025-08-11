package hexlet.code.controllers;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.http.Context;

import java.net.URI;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class UrlsController {

    private static final int BAD_REQUEST = 400;
    private UrlsController() { }

    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.getUrlsAndLastCheck();

        Map<String, Object> model = new HashMap<>();
        model.put("urls", urls);
        model.put("flash", ctx.consumeSessionAttribute("flash"));
        model.put("flashType", ctx.consumeSessionAttribute("flashType"));

        ctx.render("urls/index.jte", model);
    }

    public static void show(Context ctx) throws SQLException {
        long urlId = ctx.pathParamAsClass("id", Long.class).get();

        Optional<Url> urlOpt = UrlRepository.findById(urlId);
        if (urlOpt.isEmpty()) {
            ctx.status(404).result("URL not found");
            return;
        }

        var url = urlOpt.get();
        var urlChecks = UrlCheckRepository.getEntitiesByUrl(url);

        Map<String, Object> model = new HashMap<>();
        model.put("url", url);
        model.put("urlChecks", urlChecks);
        model.put("flash", ctx.consumeSessionAttribute("flash"));
        model.put("flashType", ctx.consumeSessionAttribute("flashType"));

        ctx.render("urls/show.jte", model);
    }

    public static void create(Context ctx) throws SQLException {

        var raw = ctx.formParamAsClass("url", String.class).getOrDefault("").trim();

        URI parsed;
        try {
            parsed = normalizeToUri(raw);
        } catch (IllegalArgumentException e) {
            var page = new hexlet.code.dto.BasePage();
            page.setFlash("Некорректный URL");
            page.setFlashType("danger");
            ctx.status(BAD_REQUEST);
            ctx.render("root.jte", java.util.Collections.singletonMap("page", page));
            return;
        }

        var normalized = buildNormalizedUrl(parsed);

        if (UrlRepository.isExist(normalized)) {
            flash(ctx, "Страница уже существует", "info");
            ctx.redirect(NamedRoutes.urls());
            return;
        }

        var url = new Url(normalized);
        url.setCreatedAt(LocalDateTime.now());
        UrlRepository.save(url);

        flash(ctx, "Страница успешно добавлена", "success");
        ctx.redirect(NamedRoutes.urls());
    }

    private static URI normalizeToUri(String input) {
        if (input == null || input.isBlank()) {
            throw new IllegalArgumentException("empty");
        }
        URI uri = URI.create(input.trim());
        if (uri.getScheme() == null) {
            uri = URI.create("http://" + input.trim());
        }
        var host = uri.getHost();
        if (!isAcceptableHost(host)) {
            throw new IllegalArgumentException("bad host");
        }
        return uri;
    }

    private static boolean isAcceptableHost(String host) {
        if (host == null || host.isBlank()) {
            return false;
        }
        if ("localhost".equalsIgnoreCase(host)) {
            return true;
        }
        if (host.matches("\\d+\\.\\d+\\.\\d+\\.\\d+")) {
            return true;
        }
        return host.contains(".");
    }

    private static String buildNormalizedUrl(URI uri) {
        var scheme = uri.getScheme().toLowerCase();
        var host = uri.getHost().toLowerCase();
        var port = (uri.getPort() == -1) ? "" : ":" + uri.getPort();
        return scheme + "://" + host + port;
    }

    private static void flash(Context ctx, String msg, String type) {
        ctx.sessionAttribute("flash", msg);
        ctx.sessionAttribute("flashType", type);
    }
}
