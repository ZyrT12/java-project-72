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
<body>

<!-- Верхняя полоса с брендом и навигацией -->
<nav class="navbar navbar-dark bg-dark">
    <div class="container-xl">
        <span class="navbar-brand mb-0 h1">Анализатор страниц</span>
        <ul class="navbar-nav flex-row gap-4">
            <li class="nav-item"><a class="nav-link active" aria-current="page" href="/">Главная</a></li>
            <li class="nav-item"><a class="nav-link" href="/urls">Сайты</a></li>
        </ul>
    </div>
</nav>

<!-- Герой как в примере -->
<section class="hero-dark py-5">
    <div class="container-xl">
        <h1 class="display-2 fw-semibold text-white mb-3">Анализатор страниц</h1>
        <p class="lead text-white-50 mb-4">Бесплатно проверяйте сайты на SEO‑пригодность</p>

        @if (flash != null && flashType != null)
            <div class="alert alert-${flashType}">${flash}</div>
        @endif

        <form method="post" action="/urls" class="row g-3 align-items-center">
            <div class="col-12 col-xl-9">
                <input name="url"
                       type="text"
                       class="form-control form-control-lg"
                       placeholder="https://www.example.com"
                       required>
                <div class="form-text text-white-50 mt-2">Пример: https://www.example.com</div>
            </div>
            <div class="col-12 col-xl-3 d-grid d-xl-block">
                <button type="submit" class="btn btn-primary btn-lg px-4">Проверить</button>
            </div>
        </form>
    </div>
</section>

</body>
</html>
