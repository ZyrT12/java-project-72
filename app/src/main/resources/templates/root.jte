@param String flash
@param String flashType


<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Анализатор страниц</title>
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
            <h1>Анализатор страниц</h1>
            <p>Бесплатно проверяйте сайты на SEO-пригодность</p>

            @if (flash != null && flashType != null)
                <div class="alert alert-${flashType}">${flash}</div>
            @endif

            <form action="/urls" method="post" class="row">
                <div class="col-8">
                    <input type="text" name="url"
                           placeholder="https://www.example.com"
                           class="form-control form-control-lg"
                           required>
                </div>
                <div class="col-2">
                    <button type="submit" class="btn btn-primary btn-lg">
                        Проверить
                    </button>
                </div>
            </form>
        </div>
    </main>
</body>
</html>
