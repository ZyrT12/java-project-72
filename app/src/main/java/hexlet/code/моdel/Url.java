package hexlet.code.моdel;

import java.sql.Timestamp;

public class Url {
    private Long id;
    private String name;
    private Timestamp createdAt;

    public Url(String name) {
        this.name = name;
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    public Url(Long id, String name, Timestamp createdAt) {
        this.id = id;
        this.name = name;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Url{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
