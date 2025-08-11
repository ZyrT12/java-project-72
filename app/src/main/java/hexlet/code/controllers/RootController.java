package hexlet.code.controllers;

import hexlet.code.dto.BasePage;
import io.javalin.http.Context;
import java.util.Collections;

public final class RootController {

    private RootController() { }

    public static void home(Context ctx) {
        var page = new BasePage();
        page.setFlash(ctx.consumeSessionAttribute("flash"));
        page.setFlashType(ctx.consumeSessionAttribute("flashType"));
        ctx.render("root.jte", Collections.singletonMap("page", page));
    }
}
