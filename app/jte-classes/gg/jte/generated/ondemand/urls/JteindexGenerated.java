package gg.jte.generated.ondemand.urls;
import hexlet.code.model.Url;
import java.util.List;
import hexlet.code.utils.FormatTimestamp;
public final class JteindexGenerated {
	public static final String JTE_NAME = "urls/index.jte";
	public static final int[] JTE_LINE_INFO = {0,0,1,2,3,3,3,26,26,26,27,27,27,27,27,27,27,28,28,41,41,42,42,44,44,44,46,46,46,46,46,46,46,48,48,48,50,50,51,51,51,52,52,55,55,56,56,56,57,57,60,60,61,61,65,65,80,80,80,3,4,5,5,5,5};
	public static void render(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, List<Url> urls, String flash, String flashType) {
		jteOutput.writeContent("\r\n<!DOCTYPE html>\r\n<html lang=\"ru\">\r\n<head>\r\n    <meta charset=\"UTF-8\">\r\n    <title>Список сайтов</title>\r\n    <link href=\"https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css\" rel=\"stylesheet\">\r\n</head>\r\n<body class=\"min-vh-100 d-flex flex-column\">\r\n<header class=\"flex-shrink-0\">\r\n    <nav class=\"navbar navbar-expand-md navbar-dark bg-dark px-3\">\r\n        <a class=\"navbar-brand\" href=\"/\">Анализатор страниц</a>\r\n        <a class=\"nav-link\" href=\"/urls\">Сайты</a>\r\n    </nav>\r\n</header>\r\n\r\n<main class=\"flex-grow-1\">\r\n    <div class=\"container-lg mt-3\">\r\n        <h1>Список сайтов</h1>\r\n\r\n        ");
		if (flash != null) {
			jteOutput.writeContent("\r\n            <div class=\"alert alert-");
			jteOutput.setContext("div", "class");
			jteOutput.writeUserContent(flashType);
			jteOutput.setContext("div", null);
			jteOutput.writeContent("\">");
			jteOutput.setContext("div", null);
			jteOutput.writeUserContent(flash);
			jteOutput.writeContent("</div>\r\n        ");
		}
		jteOutput.writeContent("\r\n\r\n        <table class=\"table table-bordered table-hover\">\r\n            <thead>\r\n            <tr>\r\n                <th>ID</th>\r\n                <th>Сайт</th>\r\n                <th>Дата создания</th>\r\n                <th>Последняя проверка</th>\r\n                <th>Код ответа</th>\r\n            </tr>\r\n            </thead>\r\n            <tbody>\r\n            ");
		if (urls != null && !urls.isEmpty()) {
			jteOutput.writeContent("\r\n                ");
			for (Url url : urls) {
				jteOutput.writeContent("\r\n                    <tr>\r\n                        <td>");
				jteOutput.setContext("td", null);
				jteOutput.writeUserContent(url.getId());
				jteOutput.writeContent("</td>\r\n                        <td>\r\n                            <a href=\"/urls/");
				jteOutput.setContext("a", "href");
				jteOutput.writeUserContent(url.getId());
				jteOutput.setContext("a", null);
				jteOutput.writeContent("\">");
				jteOutput.setContext("a", null);
				jteOutput.writeUserContent(url.getName());
				jteOutput.writeContent("</a>\r\n                        </td>\r\n                        <td>");
				jteOutput.setContext("td", null);
				jteOutput.writeUserContent(FormatTimestamp.convert(url.getCreatedAt()));
				jteOutput.writeContent("</td>\r\n                        <td>\r\n                            ");
				if (url.getLastCheck() != null) {
					jteOutput.writeContent("\r\n                                ");
					jteOutput.setContext("td", null);
					jteOutput.writeUserContent(FormatTimestamp.convert(url.getLastCheck().getCreatedAt()));
					jteOutput.writeContent("\r\n                            ");
				}
				jteOutput.writeContent("\r\n                        </td>\r\n                        <td>\r\n                            ");
				if (url.getLastCheck() != null) {
					jteOutput.writeContent("\r\n                                ");
					jteOutput.setContext("td", null);
					jteOutput.writeUserContent(url.getLastCheck().getStatusCode());
					jteOutput.writeContent("\r\n                            ");
				}
				jteOutput.writeContent("\r\n                        </td>\r\n                    </tr>\r\n                ");
			}
			jteOutput.writeContent("\r\n            ");
		} else {
			jteOutput.writeContent("\r\n                <tr>\r\n                    <td colspan=\"5\" class=\"text-center\">Нет добавленных сайтов</td>\r\n                </tr>\r\n            ");
		}
		jteOutput.writeContent("\r\n            </tbody>\r\n        </table>\r\n    </div>\r\n</main>\r\n\r\n<footer class=\"border-top py-3 mt-5 flex-shrink-0\">\r\n    <div class=\"container-lg\">\r\n        <div class=\"text-center\">\r\n            <a href=\"https://hexlet.io\" target=\"_blank\">Hexlet</a>\r\n        </div>\r\n    </div>\r\n</footer>\r\n</body>\r\n</html>\r\n");
	}
	public static void renderMap(gg.jte.html.HtmlTemplateOutput jteOutput, gg.jte.html.HtmlInterceptor jteHtmlInterceptor, java.util.Map<String, Object> params) {
		List<Url> urls = (List<Url>)params.get("urls");
		String flash = (String)params.get("flash");
		String flashType = (String)params.get("flashType");
		render(jteOutput, jteHtmlInterceptor, urls, flash, flashType);
	}
}
