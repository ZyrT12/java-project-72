@param String flash
@param String flashType

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Анализатор страниц</title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="/style.css" rel="stylesheet">
</head>
<body class="min-vh-100 d-flex flex-column">

<nav class="navbar navbar-dark bg-dark">
    <div class="container-lg d-flex align-items-center">
        <span class="navbar-brand mb-0 h1">Анализатор страниц</span>
        <ul class="navbar-nav flex-row gap-4 ms-4">
            <li class="nav-item"><a class="nav-link active" aria-current="page" href="/">Главная</a></li>
            <li class="nav-item"><a class="nav-link" href="/urls">Сайты</a></li>
        </ul>
    </div>
</nav>

<section class="bg-hero py-5">
    <div class="container-lg text-white">
        <h1 class="display-3 fw-semibold mb-2">Анализатор страниц</h1>
        <p class="lead text-white-50 mb-4">Бесплатно проверяйте сайты на SEO‑пригодность</p>

        @if (flash != null && flashType != null)
            <div class="alert alert-${flashType}">${flash}</div>
        @endif

        <!-- компактная форма по центру -->
        <form method="post" action="/urls" class="hero-form mx-auto">
            <div class="input-group input-group-lg">
                <input
                        name="url"
                        type="text"
                        class="form-control"
                        placeholder="Ссылка"
                        required>
                <button type="submit" class="btn btn-primary">Проверить</button>
            </div>
            <div class="form-text text-white-50 mt-2">
                Пример: https://www.example.com
            </div>
        </form>
    </div>
</section>
<footer class="d-none"></footer>

</body>
</html>
