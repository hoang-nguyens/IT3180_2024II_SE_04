package services;

import models.User;
import models.enums.Status;
import models.enums.Role;
import org.springframework.jdbc.core.JdbcTemplate;
import repositories.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Dùng interface thay vì class cụ thể
    private final JdbcTemplate jdbcTemplate;
    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    public boolean checkUserByUsername(String username){
        Optional<User> userOpt = userRepository.findByUsername(username);
        if(userOpt.isPresent()){
            return true;
        }
        return false;
    }

    public boolean checkUserByEmail(String email){
        Optional<User> userOpt = userRepository.findByEmail(email);
        if(userOpt.isPresent()){
            return true;
        }
        return false;
    }
    public User setUserByUsername(String username){
        Optional<User> userOpt = userRepository.findByUsername(username);
        if(userOpt.isPresent()){
            return userOpt.get();
        }
        return new User();
    }

    public User registerUser(String username, String email, String password_hash) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPasswordHash(password_hash);
        user.setRole(Role.USER);
        user.setStatus(Status.ACTIVE);

        return userRepository.save(user);
    }

    public Optional<User> loginUser(String username, String password) {
        Optional<User> userOpt = userRepository.findByUsername(username);

        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(password, user.getPasswordHash())) {
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }

    public void updateUser(User user) {
        userRepository.save(user);

    }

    /**
     * Truy vấn mật khẩu đã băm từ cơ sở dữ liệu bằng tên người dùng.
     * @param username Tên đăng nhập của người dùng.
     * @return Optional chứa mật khẩu đã băm nếu tìm thấy, ngược lại trả về Optional.empty().
     */
    public Optional<String> getPasswordByUsername(String username) {
        try {
            String sql = "SELECT password_hash FROM users WHERE username = ?";
            String hashedPassword = jdbcTemplate.queryForObject(sql, new Object[]{username}, String.class);
            return Optional.ofNullable(hashedPassword);
        } catch (Exception e) {
            return Optional.empty(); // Trả về Optional rỗng nếu tài khoản không tồn tại
        }
    }

    public void addUser(String username, String email, String password) {
        String sql = "INSERT INTO users (username, email, password_hash, role, status, created_at) VALUES (?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,username, email, passwordEncoder.encode(password),"user", Status.ACTIVE.name().toLowerCase(), LocalDateTime.now());
    }

    public void updatePasswordByUsername(String username, String newPassword) {
            String sql = "UPDATE users SET password_hash = ? WHERE username = ?";
        jdbcTemplate.update(sql, passwordEncoder.encode(newPassword), username);
    }


    public Optional<String> getEmailByUsername(String username) {
        try {
            String sql = "SELECT email FROM users WHERE username = ?";
            String email = jdbcTemplate.queryForObject(sql, new Object[]{username}, String.class);
            return Optional.ofNullable(email);
        } catch (Exception e) {
            return Optional.empty();
        }
    }


}
