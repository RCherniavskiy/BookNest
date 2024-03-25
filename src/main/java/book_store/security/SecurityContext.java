package book_store.security;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SecurityContext {
    private Authentication authentication;
}
