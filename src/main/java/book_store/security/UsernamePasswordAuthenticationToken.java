package book_store.security;

public class UsernamePasswordAuthenticationToken implements Authentication{
    public final String username;
    public final String password;

    public UsernamePasswordAuthenticationToken(String username, String password) {
        this.username = username;
        this.password = password;
    }

    @Override
    public Object getPrincipal() {
        return null;
    }

    @Override
    public Object getCredentials() {
        return null;
    }
}
