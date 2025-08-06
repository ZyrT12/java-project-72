package hexlet.code.controllers;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class UrlsController {
    private static final int SERVER_ERROR_CODE = 500;
    private final UrlRepository urlRepository;
    private final UrlCheckRepository urlCheckRepository;

    public UrlsController(UrlRepository urlRepository, UrlCheckRepository urlCheckRepository) {
        this.urlRepository = urlRepository;
        this.urlCheckRepository = urlCheckRepository;
    }

    public void index(Context ctx) throws SQLException {
        var urls = urlRepository.findAll();
        var checks = urlCheckRepository.findLatestChecks();
        ctx.render("urls/index.jte", Map.of(
                "urls", urls,
                "checks", checks
        ));
    }

    public void show(Context ctx) throws SQLException {
        Long id = ctx.pathParamAsClass("id", Long.class).get();
        Url url = urlRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Url not found"));

        List<UrlCheck> checks = urlCheckRepository.findByUrlId(id);
        ctx.render("urls/show.jte", Map.of(
                "url", url,
                "checks", checks
        ));
    }

    public void create(Context ctx) throws SQLException {
        var input = ctx.formParam("url");
        if (input == null || input.isBlank()) {
            setFlashMessage(ctx, "URL cannot be empty", "danger");
            ctx.redirect("/");
            return;
        }

        try {
            URL parsedUrl = URI.create(input).toURL();
            String normalizedUrl = parsedUrl.getProtocol() + "://" + parsedUrl.getAuthority();

            Optional<Url> existingUrl = urlRepository.findByName(normalizedUrl);

            if (existingUrl.isPresent()) {
                setFlashMessage(ctx, "Page already exists", "info");
            } else {
                var url = new Url(normalizedUrl);
                urlRepository.save(url);
                setFlashMessage(ctx, "Page successfully added", "success");
            }
            ctx.redirect("/urls");
        } catch (MalformedURLException | IllegalArgumentException e) {
            setFlashMessage(ctx, "Invalid URL", "danger");
            ctx.redirect("/");
        }
    }

    private void setFlashMessage(Context ctx, String message, String type) {
        ctx.sessionAttribute("flash", message);
        ctx.sessionAttribute("flashType", type);
    }
}
