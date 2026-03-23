package pl.kinoman.kinoman.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Włącza bezpieczne szyfrowanie haseł!
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        // Te strony może oglądać KAŻDY (nawet niezalogowany)
                        .requestMatchers("/", "/register", "/movies/details/**", "/css/**").permitAll()
                        // Wszystkie INNE akcje (np. dodawanie filmów, ocenianie) wymagają zalogowania
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // TO JEST KLUCZOWE - wskazujemy nasz adres
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/") // Gdzie iść po wylogowaniu
                        .permitAll()
                );

        return http.build();
    }
}