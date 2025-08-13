package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.model.UrlCheck;
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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {

    private Javalin app;
    private static MockWebServer mockServer;
    private static String fixtureHtml;

    private static String readFixture(String path) throws IOException {
        Path filePath = Path.of("src", "test", "resources", path);
        return Files.readString(filePath, StandardCharsets.UTF_8);
    }

    @BeforeAll
    public static void mockStart() throws IOException {
        fixtureHtml = readFixture("fixtures/page.html");
        assertThat(fixtureHtml).isNotBlank();
        mockServer = new MockWebServer();
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
    public final void tearDown() {
        var ds = BaseRepository.getDataSource();
        if (ds != null) {
            ds.close();
            BaseRepository.setDataSource(null);
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
            assertThat(response.code()).isIn(200, 302); // create -> redirect
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
    void testShowUrlPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=https://example.com");
            assertThat(response.code()).isIn(200, 302);

            List<Url> urls = UrlRepository.getEntities();
            assertThat(urls).isNotEmpty();
            long id = urls.get(0).getId();

            var showResponse = client.get("/urls/" + id);
            assertThat(showResponse.code()).isEqualTo(200);
        });
    }

    @Test
    void testCheckUrlAndFieldsPersisted() throws Exception {
        var pageUrl = mockServer.url("/").toString();

        mockServer.enqueue(new MockResponse()
                .setBody(fixtureHtml)
                .setResponseCode(200));

        JavalinTest.test(app, (server, client) -> {
            var addResponse = client.post("/urls", "url=" + pageUrl);
            assertThat(addResponse.code()).isIn(200, 302);

            var created = UrlRepository.getEntities().stream()
                    .filter(u -> u.getName()
                    .equals(pageUrl.replaceAll("/$", "")))
                    .findFirst().orElseThrow();

            var checkResponse = client.post("/urls/" + created.getId() + "/checks");
            assertThat(checkResponse.code()).isIn(200, 302);

            var latest = UrlCheckRepository.findLatestByUrlId(created.getId()).orElseThrow();
            assertThat(latest.getStatusCode()).isEqualTo(200);
            assertThat(latest.getTitle()).isEqualTo("Test page");
            assertThat(latest.getH1()).isEqualTo("Test H1");
            assertThat(latest.getDescription()).isEqualTo("Test desc");
            assertThat(latest.getCreatedAt()).isNotNull();
        });
    }

    @Test
    public void testCssIsAvailable() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/style.css");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.header("Content-Type")).isEqualTo("text/css");
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
    void addEmptyUrlReturns400AndNoInsert() {
        JavalinTest.test(app, (server, client) -> {
            var before = UrlRepository.getEntities().size();
            var resp = client.post(NamedRoutes.urls(), "url=");
            assertThat(resp.code()).isEqualTo(400);
            var after = UrlRepository.getEntities().size();
            assertThat(after).isEqualTo(before);
        });
    }

    @Test
    void addDuplicateUrlNotInsertedTwice() {
        JavalinTest.test(app, (server, client) -> {
            var u = "https://ru.hexlet.io";
            var r1 = client.post(NamedRoutes.urls(), "url=" + u);
            var r2 = client.post(NamedRoutes.urls(), "url=" + u);

            assertThat(r1.code()).isIn(200, 302);
            assertThat(r2.code()).isIn(200, 302, 409);

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
        u.setCreatedAt(LocalDateTime.now());
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
        var url = new Url("https://repo.fields");
        url.setCreatedAt(LocalDateTime.now());
        UrlRepository.save(url);
        var saved = UrlRepository.getEntities().stream()
                .filter(u -> u.getName().equals("https://repo.fields"))
                .findFirst().orElseThrow();

        var check = new UrlCheck(saved, "Title X", "H1 X", "Desc X");
        check.setStatusCode(201);
        check.setCreatedAt(LocalDateTime.now());
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
        var url = new Url("https://return.branch");
        url.setCreatedAt(LocalDateTime.now());
        UrlRepository.save(url);

        var result = UrlRepository.getUrlsAndLastCheck();

        assertThat(result).isNotNull();
        assertThat(result).anyMatch(u -> u.getName().equals("https://return.branch"));
    }

    @Test
    void addUrlNormalizedAndDuplicateDetected() {
        JavalinTest.test(app, (server, client) -> {
            int size0 = UrlRepository.getEntities().size();

            var r1 = client.post(NamedRoutes.urls(), "url=https://ru.hexlet.io/courses?utm=1#x");
            assertThat(r1.code()).isIn(200, 302);
            int size1 = UrlRepository.getEntities().size();
            assertThat(size1).isEqualTo(size0 + 1);

            var r2 = client.post(NamedRoutes.urls(), "url=https://ru.hexlet.io/");
            assertThat(r2.code()).isIn(200, 302, 409);

            int size2 = UrlRepository.getEntities().size();
            assertThat(size2).isEqualTo(size1);
        });
    }

    @Test
    void urlsIndexContainsAddedUrl() {
        JavalinTest.test(app, (server, client) -> {
            var u = "https://ru.hexlet.io";
            var add = client.post(NamedRoutes.urls(), "url=" + u);
            assertThat(add.code()).isIn(200, 302);

            var list = client.get(NamedRoutes.urls());
            assertThat(list.code()).isEqualTo(200);
            var html = list.body().string();

            assertThat(html).contains("ru.hexlet.io");
        });
    }
}
