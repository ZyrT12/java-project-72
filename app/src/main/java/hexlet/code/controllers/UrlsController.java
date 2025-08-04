package hexlet.code.controllers;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;

import java.net.MalformedURLException;
import java.net.URI;
import java.sql.SQLException;
import java.util.Map;

/**
 * Controller for handling URL-related operations.
 * Provides endpoints for URL management.
 */
public final class UrlsController {
    private static final int SERVER_ERROR_CODE = 500;
    private final UrlRepository urlRepository;

    /**
     * Constructs new UrlsController instance.
     * @param urlRepositoryParam UrlRepository instance to use
     */
    public UrlsController(final UrlRepository urlRepositoryParam) {
        this.urlRepository = urlRepositoryParam;
    }

    /**
     * Handles request to display all URLs.
     * @param ctx Javalin context
     */
    public void index(final Context ctx) {
        try {
            var urls = urlRepository.findAll();
            ctx.render("urls/index.jte", Map.of("urls", urls));
        } catch (SQLException e) {
            ctx.status(SERVER_ERROR_CODE).result("Server error: Failed to retrieve URLs");
        }
    }

    /**
     * Handles request to display single URL details.
     * @param ctx Javalin context
     */
    public void show(final Context ctx) {
        try {
            var id = ctx.pathParamAsClass("id", Long.class).get();
            var url = urlRepository.findById(id)
                    .orElseThrow(() -> new NotFoundResponse("Url not found"));
            ctx.render("urls/show.jte", Map.of("url", url));
        } catch (SQLException e) {
            ctx.status(SERVER_ERROR_CODE).result("Server error: Failed to retrieve URL");
        }
    }

    /**
     * Handles URL creation request.
     * @param ctx Javalin context
     */
    public void create(final Context ctx) {
        var input = ctx.formParam("url");
        if (input == null || input.isBlank()) {
            setFlashMessage(ctx, "URL cannot be empty", "danger");
            ctx.redirect("/");
            return;
        }

        try {
            var parsedUrl = URI.create(input).toURL();
            var normalizedUrl = parsedUrl.getProtocol() + "://" + parsedUrl.getAuthority();

            var existingUrl = urlRepository.findByName(normalizedUrl);

            if (existingUrl.isPresent()) {
                setFlashMessage(ctx, "Page already exists", "info");
            } else {
                var url = new Url(normalizedUrl);
                urlRepository.save(url);
                setFlashMessage(ctx, "Page successfully added", "success");
            }
        } catch (MalformedURLException | IllegalArgumentException e) {
            setFlashMessage(ctx, "Invalid URL", "danger");
        } catch (SQLException e) {
            ctx.status(SERVER_ERROR_CODE).result("Server error: Failed to save URL");
            return;
        }
        ctx.redirect("/");
    }

    private void setFlashMessage(final Context ctx, final String message, final String type) {
        ctx.sessionAttribute("flash", message);
        ctx.sessionAttribute("flashType", type);
    }
}
