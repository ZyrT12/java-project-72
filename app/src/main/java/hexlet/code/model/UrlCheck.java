package hexlet.code.model;

import java.sql.Timestamp;
import lombok.Setter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.NoArgsConstructor;

@Setter
@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class UrlCheck {
    private final Url url;
    private final String title;
    private final String h1;
    private final String description;
    private Long id;
    private int status;
    private Timestamp createdAt;
}
