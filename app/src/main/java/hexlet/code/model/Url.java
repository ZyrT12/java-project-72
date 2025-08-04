package hexlet.code.model;

import java.sql.Timestamp;

public final class Url {
    private Long id;
    private String name;
    private Timestamp createdAt;

    public Url(final String urlName) {
        this.name = urlName;
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
