@import hexlet.code.model.Url
@import hexlet.code.model.UrlCheck
@import java.util.List
@import hexlet.code.utils.FormatTimestamp

@param Url url
@param List<UrlCheck> urlChecks
@param String flash
@param String flashType

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Сайт ${url.getId()}</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="min-vh-100 d-flex flex-column">
<header class="flex-shrink-0">
    <nav class="navbar navbar-expand-md navbar-dark bg-dark px-3">
        <a class="navbar-brand" href="/">Анализатор страниц</a>
        <a class="nav-link" href="/urls">Сайты</a>
    </nav>
</header>

<main class="flex-grow-1">
    <div class="container-lg mt-3">
        <h1>Сайт: ${url.getName()}</h1>

        @if (flash != null)
            <div class="alert alert-${flashType}">${flash}</div>
        @endif

        <div class="table-responsive">
            <table class="table table-bordered table-hover">
                <thead class="table-light">
                <tr>
                    <th>ID</th>
                    <th>Имя</th>
                    <th>Дата создания</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>${url.getId()}</td>
                    <td>${url.getName()}</td>
                    <td>
                        @if (url.getCreatedAt() != null)
                            ${FormatTimestamp.convert(url.getCreatedAt())}
                        @else
                            —
                        @endif
                    </td>
                </tr>
                </tbody>
            </table>
        </div>

        <h2 class="mt-5">Проверки</h2>
        <form method="post" action="/urls/${url.getId()}/checks">
            <button type="submit" class="btn btn-primary">
                Запустить проверку
            </button>
        </form>

        <table class="table table-bordered table-hover mt-3">
            <thead class="table-light">
            <tr>
                <th>ID</th>
                <th>Код ответа</th>
                <th>Дата проверки</th>
            </tr>
            </thead>
            <tbody>
            @if (urlChecks != null && !urlChecks.isEmpty())
                @for (UrlCheck check : urlChecks)
                    <tr>
                        <td>${check.getId()}</td>
                        <td>
                            @if (check.getStatusCode() != null)
                                ${check.getStatusCode()}
                            @else
                                —
                            @endif
                        </td>
                        <td>
                            @if (check.getCreatedAt() != null)
                                ${FormatTimestamp.convert(check.getCreatedAt())}
                            @else
                                —
                            @endif
                        </td>
                    </tr>
                @endfor
            @else
                <tr>
                    <td colspan="3" class="text-center">Проверок пока нет</td>
                </tr>
            @endif
            </tbody>
        </table>
    </div>
</main>
</body>
</html>
