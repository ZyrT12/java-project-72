package hexlet.code.utils;

import gg.jte.TemplateEngine;
import gg.jte.ContentType;
import gg.jte.resolve.ResourceCodeResolver;

public final class TemplateResolve {
    private TemplateResolve() { }

    public static TemplateEngine createTemplateEngine() {
        var resolver = new ResourceCodeResolver("templates");
        return TemplateEngine.create(resolver, ContentType.Html);
    }
}