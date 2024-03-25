package book_store.security;

import book_store.dto.user.UserLoginRequestDto;
import book_store.model.User;
import book_store.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    public boolean authenticate(UserLoginRequestDto requestDto) {
        Optional<User> user = userRepository.findByEmail(requestDto.email());
        return user.isPresent() && user.get().getPassword().equals(requestDto.password());
    }
}
