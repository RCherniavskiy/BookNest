package book_store.service;

import book_store.dto.user.UserRegistrationRequestDto;
import book_store.dto.user.UserResponseDto;
import book_store.exception.EntityNotFoundException;
import book_store.exception.RegistrationException;
import book_store.mapper.UserMapper;
import book_store.model.Role;
import book_store.model.User;
import book_store.repository.role.RoleRepository;
import book_store.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository ;

    @Override
    public UserResponseDto register(UserRegistrationRequestDto requestDto) throws RegistrationException {
        if (userRepository.findByEmail(requestDto.getEmail()).isPresent()) {
            throw new RegistrationException("User with this email already exists");
        }
        User user = userMapper.toModel(requestDto);
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        Role role = roleRepository.findByName(Role.RoleName.ROLE_USER)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
        user.setRoles(Set.of(role));
        User savedUser  = userRepository.save(user);
        return userMapper.toUserResponseDto(savedUser);
    }
}
