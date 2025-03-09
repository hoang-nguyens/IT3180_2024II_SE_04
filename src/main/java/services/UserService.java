package services;

import models.User;
import models.enums.Status;
import models.enums.Role;
import repositories.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    //
    public User registerUser(String username, String email, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("Username đã tồn tại!");
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email đã tồn tại!");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(password)); // Mã hóa mật khẩu
        user.setRole(Role.USER);
        user.setStatus(Status.ACTIVE);

        return userRepository.save(user);
    }

    //
    public Optional<User> loginUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPasswordHash())) {
                // Cập nhật thời gian đăng nhập cuối cùng
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }
}