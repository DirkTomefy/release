package mg.bovit.release.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                // URLs publiques
                .requestMatchers("/", "/login", "/register", "/public/**").permitAll()
                
                // URLs par rôle
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/vet/**").hasRole("VETERINAIRE")
                .requestMatchers("/api/bovins/**").hasAnyRole("ADMIN", "VETERINAIRE", "EMPLOYE")
                
                // méthode HTTP spécifique
                .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")
                
                // tout le reste = authentifié
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .permitAll()
            )
            .logout(logout -> logout.permitAll());

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
