package hexlet.code.controllers;

import hexlet.code.dto.UrlChecksPage;
import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class UrlCheckController {
    private static final int ERROR_CLIENT = 400;

    public static void check(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse(String.format("Url with id = %d not found", id)));

        try {
            HttpResponse<String> response = Unirest.get(url.getName()).asString();
            Document doc = Jsoup.parse(response.getBody());

            var title = doc.title();
            var h1Element = doc.selectFirst("h1");
            var descriptionElement = doc.selectFirst("meta[name=description]");

            var urlCheck = new UrlCheck(
                    url,
                    title,
                    h1Element != null ? h1Element.text() : "",
                    descriptionElement != null ? descriptionElement.attr("content") : ""
            );

            urlCheck.setStatusCode(response.getStatus());
            UrlCheckRepository.save(urlCheck, url);

            ctx.redirect(NamedRoutes.urlPath(String.valueOf(id)));
        } catch (Exception e) {
            log.error("Error during URL check: {}", e.getMessage());
            var page = new UrlChecksPage(url, List.of());
            page.setFlash("Некорректный адрес");
            page.setFlashType("danger");
            ctx.status(ERROR_CLIENT);
            ctx.render("urls/show.jte", Collections.singletonMap("page", page));
        }
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParam("id");
        log.info("Showing URL with id: {}", id);

        try {
            long urlId = Long.parseLong(id);
            Optional<Url> urlOptional = UrlRepository.findById(urlId);

            if (urlOptional.isEmpty()) {
                log.warn("URL with id {} not found", urlId);
                ctx.status(404).result("URL not found");
                return;
            }

            Url url = urlOptional.get();
            log.debug("Found URL: {}", url.getName());
            ctx.render("urls/show.jte", Map.of("url", url));
        } catch (NumberFormatException e) {
            log.error("Invalid URL ID format: {}", id, e);
            ctx.status(400).result("Invalid URL ID format");
        }
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

}
