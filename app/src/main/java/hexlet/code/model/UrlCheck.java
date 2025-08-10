package hexlet.code.model;

import java.sql.Timestamp;
import lombok.Setter;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Setter
@Getter
@NoArgsConstructor(force = true)
@RequiredArgsConstructor
public class UrlCheck {
    @NonNull private Url url;
    @NonNull private String title;
    @NonNull private String h1;
    @NonNull private String description;
    private Long id;
    private Integer statusCode;
    private Timestamp createdAt;
}
