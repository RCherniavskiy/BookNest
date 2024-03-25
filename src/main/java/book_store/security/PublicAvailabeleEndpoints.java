package book_store.security;

import java.util.List;

public class PublicAvailabeleEndpoints {
    private static List<String> publicEndpoints = List.of(
            "/auth/login",
            "/auth/registration"
    );
    public static List<String> getPublicEndpoints() {
        return publicEndpoints;
    }
}
