package hexlet.code.controllers;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.UrlRepository;
import hexlet.code.repository.UrlCheckRepository;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;

public final class UrlCheckController {
    private final UrlRepository urlRepository;
    private final UrlCheckRepository urlCheckRepository;

    public UrlCheckController(UrlRepository urlRepository, UrlCheckRepository urlCheckRepository) {
        this.urlRepository = urlRepository;
        this.urlCheckRepository = urlCheckRepository;
    }

    public void check(Context ctx) throws SQLException {
        long id = ctx.pathParamAsClass("id", Long.class).get();

        Url url = urlRepository.findById(id)
                .orElseThrow(() -> new NotFoundResponse("URL not found"));

        try {
            Document document = Jsoup.connect(url.getName()).get();
            int statusCode = 200;
            String title = document.title();
            String h1 = document.selectFirst("h1") != null ? document.selectFirst("h1").text() : "";
            String description = document.selectFirst("meta[name=description]") != null
                    ? document.selectFirst("meta[name=description]").attr("content") : "";

            UrlCheck check = new UrlCheck(
                    url.getId(),
                    statusCode,
                    title,
                    h1,
                    description,
                    LocalDateTime.now()
            );
            urlCheckRepository.save(check);

            ctx.sessionAttribute("flash", "Проверка прошла успешно");
            ctx.sessionAttribute("flashType", "success");
        } catch (IOException e) {
            ctx.sessionAttribute("flash", "Проверка не удалась: " + e.getMessage());
            ctx.sessionAttribute("flashType", "danger");
        }

        ctx.redirect("/urls/" + id);
    }
}
