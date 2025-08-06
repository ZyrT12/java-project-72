package hexlet.code.model;

import gg.jte.Content;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;

public final class Url {
    private Long id;
    private String name;
    private Timestamp createdAt;

    public Url(String url) throws MalformedURLException {
        URL parsedUrl = URI.create(url).toURL();
        this.name = parsedUrl.getProtocol() + "://" + parsedUrl.getAuthority();
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Url(final Long urlId, final String urlName, final Timestamp urlCreatedAt) {
        this.id = urlId;
        this.name = urlName;
        this.createdAt = urlCreatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long urlId) {
        this.id = urlId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String urlName) {
        this.name = urlName;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Timestamp urlCreatedAt) {
        this.createdAt = urlCreatedAt;
    }

    @Override
    public String toString() {
        return "Url{"
                + "id=" + id
                + ", name='" + name + '\''
                + ", createdAt=" + createdAt
                + '}';
    }
}
