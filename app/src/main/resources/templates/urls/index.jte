@import hexlet.code.model.Url
@import java.util.List
@import hexlet.code.utils.FormatTimestamp
@param List<Url> urls
@param String flash
@param String flashType

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Список сайтов</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/style.css" rel="stylesheet">
</head>
<body class="min-vh-100 d-flex flex-column">

<header class="flex-shrink-0">
    <nav class="navbar navbar-dark bg-dark">
        <div class="container-lg d-flex align-items-center">
            <a class="navbar-brand mb-0 h1 me-4" href="/">Анализатор страниц</a>
            <ul class="navbar-nav flex-row">
                <li class="nav-item">
                    <!-- здесь active, чтобы была белой -->
                    <a class="nav-link active" href="/urls">Сайты</a>
                </li>
            </ul>
        </div>
    </nav>
</header>

<main class="flex-grow-1">
    <div class="container-lg mt-4">
        <h1 class="display-4 mb-3">Список сайтов</h1>

        @if (flash != null && flashType != null)
            <div class="alert alert-${flashType}">${flash}</div>
        @endif

        <table class="table table-bordered table-hover">
            <thead class="table-light">
            <tr>
                <th>ID</th>
                <th>Сайт</th>
                <th>Дата создания</th>
                <th>Последняя проверка</th>
                <th>Код ответа</th>
            </tr>
            </thead>
            <tbody>
            @if (urls != null && !urls.isEmpty())
                @for (Url url : urls)
                    <tr>
                        <td>${url.getId()}</td>
                        <td><a href="/urls/${url.getId()}">${url.getName()}</a></td>
                        <td>${FormatTimestamp.convert(url.getCreatedAt())}</td>
                        <td>
                            @if (url.getLastCheck() != null)
                                ${FormatTimestamp.convert(url.getLastCheck().getCreatedAt())}
                            @endif
                        </td>
                        <td>
                            @if (url.getLastCheck() != null)
                                ${url.getLastCheck().getStatusCode()}
                            @endif
                        </td>
                    </tr>
                @endfor
            @else
                <tr><td colspan="5" class="text-center">Нет добавленных сайтов</td></tr>
            @endif
            </tbody>
        </table>
    </div>
</main>

</body>
</html>
