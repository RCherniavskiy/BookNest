package book_store.security;

public class SecurityContextHolder {
    private static ThreadLocal<SecurityContext> securityContext = ThreadLocal.withInitial(SecurityContext::new);
    public static SecurityContext getSecurityContext() {
        return securityContext.get();
    }
}
