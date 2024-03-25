package book_store.security;

public interface Authentication {
    Object getPrincipal();

    Object getCredentials();
}
