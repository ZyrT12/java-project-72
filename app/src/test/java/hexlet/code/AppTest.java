package hexlet.code;

import hexlet.code.model.Url;
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

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for {@link App} application.
 * Contains integration tests for URL checking functionality.
 */
public class AppTest {
    private Javalin app;
    private static MockWebServer mockServer;

    /**
     * Starts mock web server before all tests.
     *
     * @throws IOException if mock server fails to start
     */
    @BeforeAll
    public static void mockStart() throws IOException {
        mockServer = new MockWebServer();
        var mockResponse = new MockResponse()
                .setBody("<html><head><title>Test page</title></head><body>Test content</body></html>");
        mockServer.enqueue(mockResponse);
        mockServer.start();
    }

    /**
     * Shuts down mock web server after all tests.
     *
     * @throws IOException if mock server fails to shutdown
     */
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

    /**
     * Tests that main page is accessible.
     */
    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.home());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    /**
     * Tests that URLs page is accessible when no URLs are present.
     */
    @Test
    public void testUrlsEmptyPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urls());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    /**
     * Tests that valid URL can be added successfully.
     */
    @Test
    public void testAddValidUrl() {
        JavalinTest.test(app, (server, client) -> {
            var validUrl = "https://ru.hexlet.io";
            var response = client.post(NamedRoutes.urls(), "url=" + validUrl);
            assertThat(response.code()).isEqualTo(200);
        });
    }

    /**
     * Tests that invalid URL returns error response.
     */
    @Test
    public void testAddInvalidUrl() {
        JavalinTest.test(app, (server, client) -> {
            var invalidUrl = "invalid-url";
            var response = client.post(NamedRoutes.urls(), "url=" + invalidUrl);
            assertThat(response.code()).isEqualTo(400);
        });
    }

    /**
     * Tests that URL page shows details of specific URL.
     *
     * @throws SQLException if database operation fails
     */
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

    /**
     * Tests that URL checking functionality works correctly.
     *
     * @throws SQLException if database operation fails
     */
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

    /**
     * Tests that CSS file is available and has correct content type.
     */
    @Test
    public void testCssIsAvailable() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/style.css");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.header("Content-Type")).isEqualTo("text/css");
        });
    }
}
