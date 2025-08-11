package hexlet.code.controllers;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;
import java.time.LocalDateTime;

@Slf4j
public final class UrlCheckController {

    private UrlCheckController() { }

    public static void check(Context ctx) throws SQLException {
        long id = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("Url with id = " + id + " not found"));

        try {
            var response = kong.unirest.Unirest.get(url.getName()).asString();

            var body = response.getBody();
            var doc = org.jsoup.Jsoup.parse(body);

            String title = doc.title();
            var h1El = doc.selectFirst("h1");
            String h1 = h1El != null ? h1El.text() : null;

            var descEl = doc.selectFirst("meta[name=description]");
            String description = descEl != null ? descEl.attr("content") : null;

            var check = new UrlCheck(url, title, h1, description);
            check.setStatusCode(response.getStatus());
            check.setCreatedAt(LocalDateTime.now());

            UrlCheckRepository.save(check, url);

            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flashType", "success");
            ctx.redirect(NamedRoutes.urlPath(String.valueOf(id)));

        } catch (Exception e) {
            log.warn("URL check failed for {}: {}", url.getName(), e.getMessage());
            ctx.sessionAttribute("flash", "Не удалось проверить страницу (таймаут)");
            ctx.sessionAttribute("flashType", "warning");
            ctx.redirect(NamedRoutes.urlPath(String.valueOf(id)));
        }
    }

    static {
        kong.unirest.Unirest.config()
                .connectTimeout(2000)
                .socketTimeout(2000)
                .followRedirects(true);
    }
}
