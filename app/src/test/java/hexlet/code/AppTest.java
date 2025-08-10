package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlCheckRepository;
import hexlet.code.repository.UrlRepository;
import hexlet.code.utils.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {

    private Javalin app;
    private static MockWebServer mockServer;

    @BeforeAll
    public static void mockStart() throws IOException {
        mockServer = new MockWebServer();
        mockServer.enqueue(new MockResponse()
                .setBody("<html><head><title>Test page</title>"
                        + "<meta name=\"description\" content=\"Test desc\">"
                        + "</head><body><h1>Test H1</h1></body></html>")
                .setResponseCode(200));
        mockServer.start();
    }

    @AfterAll
    public static void mockStop() throws IOException {
        mockServer.shutdown();
    }

    /**
     * Initializes Javalin application before each test.
     *
     * @throws SQLException if database initialization fails
     * @throws IOException if resource files cannot be read
     */
    @BeforeEach
    public void setUp() throws SQLException, IOException {
        app = App.getApp();
    }

    @AfterEach
    void tearDown() {
        var ds = hexlet.code.repository.BaseRepository.getDataSource();
        if (ds != null) {
            ds.close();
            hexlet.code.repository.BaseRepository.setDataSource(null);
        }
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.home());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testUrlsEmptyPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urls());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testAddValidUrl() {
        JavalinTest.test(app, (server, client) -> {
            var validUrl = "https://ru.hexlet.io";
            var response = client.post(NamedRoutes.urls(), "url=" + validUrl);
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testAddInvalidUrl() {
        JavalinTest.test(app, (server, client) -> {
            var invalidUrl = "invalid-url";
            var response = client.post(NamedRoutes.urls(), "url=" + invalidUrl);
            assertThat(response.code()).isEqualTo(400);
        });
    }

    @Test
    void testShowUrlPage() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=https://example.com");
            assertThat(response.code()).isEqualTo(200);

            List<Url> urls = UrlRepository.getEntities();
            assertThat(urls).isNotEmpty();
            long id = urls.get(0).getId();

            var showResponse = client.get("/urls/" + id);
            assertThat(showResponse.code()).isEqualTo(200);
        });
    }

    @Test
    void testCheckUrl() throws SQLException {
        JavalinTest.test(app, (server, client) -> {
            var testUrl = "https://example.com";
            var addResponse = client.post("/urls", "url=" + testUrl);
            assertThat(addResponse.code()).isEqualTo(200);

            List<Url> urls = UrlRepository.getEntities();
            assertThat(urls).isNotEmpty();

            Optional<Url> createdUrl = urls.stream()
                    .filter(u -> u.getName().equals(testUrl))
                    .findFirst();

            assertThat(createdUrl).isPresent();
            long id = createdUrl.get().getId();

            var checkResponse = client.post("/urls/" + id + "/checks");
            assertThat(checkResponse.code()).isEqualTo(200);
        });
    }

    @Test
    public void testCssIsAvailable() throws IOException {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/style.css");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.header("Content-Type")).isEqualTo("text/css");
            // Дополнительно проверим, что тело не пустое — это добавит покрытие ветки чтения ресурса
            var body = response.body().string();
            assertThat(body).isNotBlank();
        });
    }


    @Test
    void showUrlNotFoundReturns404() {
        JavalinTest.test(app, (server, client) -> {
            var resp = client.get("/urls/999999");
            assertThat(resp.code()).isEqualTo(404);
        });
    }

    @Test
    void addEmptyUrlReturns400AndNoInsert() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            var before = UrlRepository.getEntities().size();
            var resp = client.post(NamedRoutes.urls(), "url=");
            assertThat(resp.code()).isEqualTo(400);
            var after = UrlRepository.getEntities().size();
            assertThat(after).isEqualTo(before);
        });
    }

    @Test
    void addDuplicateUrlNotInsertedTwice() throws Exception {
        JavalinTest.test(app, (server, client) -> {
            var u = "https://ru.hexlet.io";
            var r1 = client.post(NamedRoutes.urls(), "url=" + u);
            var r2 = client.post(NamedRoutes.urls(), "url=" + u);

            assertThat(r1.code()).isEqualTo(200);
            // Вторая попытка может вернуть 200 с сообщением «уже существует» или 409 — учитываем оба варианта
            assertThat(r2.code()).isIn(200, 409);

            long count = UrlRepository.getEntities()
                    .stream()
                    .filter(x -> x.getName().equals(u))
                    .count();
            assertThat(count).isEqualTo(1);
        });
    }

    @Test
    void datasourceIsConfigured() {
        assertThat(BaseRepository.getDataSource()).isNotNull();
    }

    @Test
    void repoFindByIdNotFoundAndIsExistFalse() throws Exception {

        Url u = new Url("https://repo.test/one");
        UrlRepository.save(u);

        assertThat(UrlRepository.isExist("https://repo.test/one")).isTrue();
        assertThat(UrlRepository.isExist("https://repo.test/missing")).isFalse();

        long existingId = UrlRepository.getEntities().stream()
                .filter(x -> x.getName().equals("https://repo.test/one"))
                .findFirst().orElseThrow().getId();
        assertThat(UrlRepository.findById(existingId)).isPresent();

        assertThat(UrlRepository.findById(9_999_999L)).isEmpty();
    }

    @Test
    void repoGetEntitiesByUrlReadsAllFields() throws Exception {
        var url = new hexlet.code.model.Url("https://repo.fields");
        UrlRepository.save(url);
        var saved = UrlRepository.getEntities().stream()
                .filter(u -> u.getName().equals("https://repo.fields"))
                .findFirst().orElseThrow();
        saved.setId(saved.getId());

        var check = new hexlet.code.model.UrlCheck(saved, "Title X", "H1 X", "Desc X");
        check.setStatusCode(201);
        UrlCheckRepository.save(check, saved);

        var checks = UrlCheckRepository.getEntitiesByUrl(saved);
        assertThat(checks).hasSize(1);
        var c = checks.get(0);

        assertThat(c.getId()).isNotNull();
        assertThat(c.getStatusCode()).isEqualTo(201);
        assertThat(c.getTitle()).isEqualTo("Title X");
        assertThat(c.getH1()).isEqualTo("H1 X");
        assertThat(c.getDescription()).isEqualTo("Desc X");
        assertThat(c.getCreatedAt()).isNotNull();
    }

    @Test
    void repoGetUrlsAndLastCheckReachesReturn() throws Exception {
        var url = new hexlet.code.model.Url("https://return.branch");
        UrlRepository.save(url);

        var result = UrlRepository.getUrlsAndLastCheck();

        assertThat(result).isNotNull();
        assertThat(result).anyMatch(u -> u.getName().equals("https://return.branch"));
    }
}
