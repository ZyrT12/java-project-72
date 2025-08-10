package hexlet.code.controllers;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import lombok.extern.slf4j.Slf4j;
import java.sql.SQLException;

@Slf4j
public class UrlCheckController {
    private static final int ERROR_CLIENT = 400;

    public static void check(Context ctx) throws SQLException {
        long id = ctx.pathParamAsClass("id", Long.class).get();
        Url url = UrlRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse(String.format("Url with id = %d not found", id)));

        try {
            kong.unirest.HttpResponse<String> response = kong.unirest.Unirest.get(url.getName()).asString();

            String body = response.getBody();
            org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(body);

            String title = doc.title();
            org.jsoup.nodes.Element h1El = doc.selectFirst("h1");
            String h1 = h1El != null ? h1El.text() : null;

            org.jsoup.nodes.Element descEl = doc.selectFirst("meta[name=description]");
            String description = descEl != null ? descEl.attr("content") : null;

            hexlet.code.model.UrlCheck check = new hexlet.code.model.UrlCheck(url, title, h1, description);
            check.setStatusCode(response.getStatus());
            hexlet.code.repository.UrlCheckRepository.save(check, url);

            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flashType", "success");
            ctx.redirect(hexlet.code.utils.NamedRoutes.urlPath(String.valueOf(id)));

        } catch (Exception e) {
            log.warn("URL check failed for {}: {}", url.getName(), e.getMessage());
            ctx.sessionAttribute("flash", "Не удалось проверить страницу (таймаут)");
            ctx.sessionAttribute("flashType", "warning");
            ctx.redirect(hexlet.code.utils.NamedRoutes.urlPath(String.valueOf(id)));
        }
    }

    public static void show(Context ctx) throws SQLException {
        long urlId = ctx.pathParamAsClass("id", Long.class).get();

        java.util.Optional<Url> urlOptional = UrlRepository.findById(urlId);
        if (urlOptional.isEmpty()) {
            ctx.status(404).result("URL not found");
            return;
        }

        Url url = urlOptional.get();
        java.util.List<UrlCheck> urlChecks = UrlCheckRepository.getEntitiesByUrl(url);

        java.util.Map<String, Object> model = new java.util.HashMap<>();
        model.put("url", url);
        model.put("urlChecks", urlChecks);
        model.put("flash", ctx.consumeSessionAttribute("flash"));
        model.put("flashType", ctx.consumeSessionAttribute("flashType"));

        ctx.render("urls/show.jte", model);
    }

    public static void create(io.javalin.http.Context ctx) throws Exception {
        long id = Long.parseLong(ctx.pathParam("id"));

        var opt = hexlet.code.repository.UrlRepository.findById(id);
        if (opt.isEmpty()) {
            ctx.status(404);
            return;
        }
        var url = opt.get();

        var resp = kong.unirest.Unirest.get(url.getName()).asString();
        int status = resp.getStatus();
        String body = resp.getBody();

        org.jsoup.nodes.Document doc = org.jsoup.Jsoup.parse(body);
        String title = doc.title();

        var h1El = doc.selectFirst("h1");
        String h1 = h1El != null ? h1El.text() : null;

        var descEl = doc.selectFirst("meta[name=description]");
        String description = descEl != null ? descEl.attr("content") : null;

        var check = new hexlet.code.model.UrlCheck(url, title, h1, description);
        check.setStatusCode(status);
        hexlet.code.repository.UrlCheckRepository.save(check, url);

        ctx.sessionAttribute("flash", "Страница успешно проверена");
        ctx.sessionAttribute("flashType", "success");
        ctx.redirect("/urls/" + id);
    }

    static {
        kong.unirest.Unirest.config()
                .connectTimeout(2000)
                .socketTimeout(2000)
                .followRedirects(true);
    }

}
