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
        <a href="/" class="navbar-brand mb-0 h1 me-4">Анализатор страниц</a>
        <ul class="navbar-nav flex-row">
            <li class="nav-item">
                <a class="nav-link active" href="/urls">Сайты</a>
            </li>
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

</body>
</html>
