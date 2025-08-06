package hexlet.code.utils;

public class NamedRoutes {
    public static String home() {
        return "/";
    }

    public static String urls() {
        return "/urls";
    }

    public static String urlPath(Long id) {
        return urlPath(String.valueOf(id));
    }

    public static String urlPath(String id) {
        return "/urls/" + id;
    }

    public static String urlCheck(String id) {
        return String.format("/urls/%s/checks", id);
    }

    public static String urlCheck(Long id) {
        return urlCheck(String.valueOf(id));
    }
}
