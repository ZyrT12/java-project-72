package hexlet.code;

import hexlet.code.model.Url;
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

import java.io.IOException;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {
    private Javalin app;
    private static MockWebServer mockServer;

    @BeforeAll
    public static void mockStart() throws IOException {
        mockServer = new MockWebServer();
        var mockResponse = new MockResponse()
                .setBody("<html><head><title>Test page</title></head><body>Test content</body></html>");
        mockServer.enqueue(mockResponse);
        mockServer.start();
    }

    @AfterAll
    public static void mockStop() throws IOException {
        mockServer.shutdown();
    }

    @BeforeEach
    public void setUp() throws SQLException, IOException {
        app = App.getApp();
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
    void testShowUrlPage() {
        JavalinTest.test(app, (server, client) -> {
            // Сначала создаем URL
            var response = client.post("/urls", "url=https://example.com");
            assertThat(response.code()).isEqualTo(200);

            // Затем проверяем его страницу с валидным ID
            var showResponse = client.get("/urls/1");
            assertThat(showResponse.code()).isEqualTo(200);
        });
    }

    @Test
    void testCheckUrl() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.post("/urls", "url=https://example.com");
            assertThat(response.code()).isEqualTo(200);

            Thread.sleep(10000);

            var checkResponse = client.post("/urls/1/checks");
            assertThat(checkResponse.code()).isEqualTo(200);
        });
    }

    @Test
    public void testCssIsAvailable() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get("/style.css");
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.header("Content-Type")).isEqualTo("text/css");
        });
    }
}
