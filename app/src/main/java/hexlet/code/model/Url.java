package hexlet.code.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Url {
    private String name;
    private Long id;
    private LocalDateTime createdAt;
    private UrlCheck lastCheck;

    public Url(String name) {
        this.name = name;
    }
}
