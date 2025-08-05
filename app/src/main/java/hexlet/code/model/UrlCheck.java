package hexlet.code.model;

import java.time.LocalDateTime;

public final class UrlCheck {

    private final Long id;
    private final Long urlId;
    private final int statusCode;
    private final String title;
    private final String h1;
    private final String description;
    private final LocalDateTime createdAt;

    public UrlCheck(Long id, Long urlId, int statusCode, String title, String h1, String description, LocalDateTime createdAt) {
        this.id = id;
        this.urlId = urlId;
        this.statusCode = statusCode;
        this.title = title;
        this.h1 = h1;
        this.description = description;
        this.createdAt = createdAt;
    }

    public UrlCheck(Long urlId, int statusCode, String title, String h1, String description, LocalDateTime createdAt) {
        this(null, urlId, statusCode, title, h1, description, createdAt);
    }

    public Long getId() {
        return id;
    }

    public Long getUrlId() {
        return urlId;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public String getTitle() {
        return title;
    }

    public String getH1() {
        return h1;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "UrlCheck{" +
                "id=" + id +
                ", urlId=" + urlId +
                ", statusCode=" + statusCode +
                ", title='" + title + '\'' +
                ", h1='" + h1 + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
