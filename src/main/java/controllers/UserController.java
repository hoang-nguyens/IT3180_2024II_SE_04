package controllers;

import models.User;
import services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    //
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestParam String username,
                                      @RequestParam String email,
                                      @RequestParam String password) {
        try {
            User user = userService.registerUser(username, email, password);
            return ResponseEntity.ok("Đăng ký thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    //
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String username, @RequestParam String password) {
        Optional<User> user = userService.loginUser(username, password);

        if (user.isPresent()) {
            return ResponseEntity.ok("Đăng nhập thành công!");
        } else {
            return ResponseEntity.badRequest().body("Sai tài khoản hoặc mật khẩu!");
        }
    }

}
