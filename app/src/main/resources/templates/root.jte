@param String flash
@param String flashType

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Анализатор страниц</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/style.css" rel="stylesheet">
</head>
<body class="cover-page d-flex h-100 text-white">

<div class="cover-container d-flex w-100 h-100 p-3 mx-auto flex-column">
    <header class="mb-auto">
        <div class="d-flex align-items-center justify-content-between">
            <h3 class="masthead-brand mb-0">Анализатор страниц</h3>
            <nav class="nav nav-masthead">
                <a class="nav-link active" aria-current="page" href="/">Главная</a>
                <a class="nav-link" href="/urls">Сайты</a>
            </nav>
        </div>
    </header>

    <main class="px-3">
        <h1>Анализатор страниц</h1>
        <p class="lead">Бесплатно проверяйте сайты на SEO‑пригодность</p>

        @if (flash != null && flashType != null)
            <div class="alert alert-${flashType}">${flash}</div>
        @endif

        <form method="post" action="/urls" class="d-flex justify-content-center">
            <input name="url"
                   type="text"
                   class="form-control form-control-lg w-50 me-2"
                   placeholder="https://www.example.com"
                   required>
            <button type="submit" class="btn btn-lg btn-secondary">Проверить</button>
        </form>

        <p class="mt-2">Пример: https://www.example.com</p>
    </main>

    <footer class="mt-auto">
    </footer>
</div>

</body>
</html>
