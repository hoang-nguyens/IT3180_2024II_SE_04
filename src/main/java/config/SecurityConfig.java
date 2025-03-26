package config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF theo cú pháp mới
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()) // Cho phép tất cả request không cần login
                .formLogin(form -> form.disable()) // Không sử dụng form login
                .httpBasic(basic -> basic.disable()) // Không sử dụng Basic Auth
                .build();
    }
}
