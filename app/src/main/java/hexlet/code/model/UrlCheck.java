package hexlet.code.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class UrlCheck {
    private Long id;
    private Url url;
    private Integer statusCode;
    private String title;
    private String h1;
    private String description;
    private LocalDateTime createdAt;

    public UrlCheck(Url url, String title, String h1, String description) {
        this.url = url;
        this.title = title;
        this.h1 = h1;
        this.description = description;
    }
}
