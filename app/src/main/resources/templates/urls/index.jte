@import hexlet.code.model.Url
@import java.util.List
@import hexlet.code.util.FormatTimestamp
@param List<Url> urls
@param String flash
@param String flashType



<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Список сайтов</title>
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
            <h1>Список сайтов</h1>

            @if (flash != null)
                <div class="alert alert-${flashType}">${flash}</div>
            @endif

            <table class="table table-bordered table-hover">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Сайт</th>
                        <th>Дата создания</th>
                    </tr>
                </thead>
                <tbody>
                @for (var url : urls)
                    <tr>
                        <td>${url.getId()}</td>
                        <td>
                            <a href="/urls/${url.getId()}">${url.getName()}</a>
                        </td>
                        <td>${FormatTimestamp.convert(url.getCreatedAt())}
                        </td>
                    </tr>
                @endfor

                </tbody>
            </table>
        </div>
    </main>

    <footer class="border-top py-3 mt-5 flex-shrink-0">
        <div class="container-lg">
            <div class="text-center">
                <a href="https://hexlet.io" target="_blank">Hexlet</a>
            </div>
        </div>
    </footer>
</body>
</html>
