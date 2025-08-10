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
<body class="cover-page">

<header class="py-3 bg-dark">
    <div class="container-lg d-flex align-items-center justify-content-between">
        <h3 class="masthead-brand mb-0 text-white">Анализатор страниц</h3>
        <nav class="nav nav-masthead">
            <a class="nav-link active" aria-current="page" href="/">Главная</a>
            <a class="nav-link" href="/urls">Сайты</a>
        </nav>
    </div>
</header>

<main class="hero py-4">
    <div class="container-lg text-white">
        <h1 class="display-5 fw-semibold mb-2">Анализатор страниц</h1>
        <p class="lead mb-4">Бесплатно проверяйте сайты на SEO‑пригодность</p>

        @if (flash != null && flashType != null)
            <div class="alert alert-${flashType}">${flash}</div>
        @endif

        <form method="post" action="/urls" class="row g-2 align-items-center">
            <div class="col-12 col-lg-9">
                <input name="url"
                       type="text"
                       class="form-control form-control-lg w-100"
                       placeholder="https://www.example.com"
                       required>
                <div class="form-text text-light opacity-75 mt-1">
                    Пример: https://www.example.com
                </div>
            </div>
            <div class="col-12 col-lg-3 d-grid d-lg-block">
                <button type="submit" class="btn btn-lg btn-secondary px-4">Проверить</button>
            </div>
        </form>
    </div>
</main>

</body>
</html>
