package mg.bovit.release.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
                .requestMatchers("/", "/login", "/auth/login", "/register", "/auth/register", "/public/**").permitAll()

                // Vente
                .requestMatchers("/vente/**", "/clients/**", "/api/factures/**").hasAnyRole("ADMIN", "VENTE")

                // Pesée
                .requestMatchers("/peseBovin/**", "/bovin/api/**").hasAnyRole("ADMIN", "PESEE")

                // Gestion du troupeau / lots
                .requestMatchers("/bovins/**", "/races/**", "/mortalite/**", "/contrat/**").hasAnyRole("ADMIN", "LOT")

                // Stock / matériel
                .requestMatchers("/materiel/**", "/inventaire/**", "/mouvement/**", "/api/materiels/**").hasAnyRole("ADMIN", "STOCK")

                // Caisse / paiements employés
                .requestMatchers("/caisse/**", "/employees/**", "/employee/**").hasAnyRole("ADMIN", "CAISSE", "EMPLOYE")

                // Administration globale
                .requestMatchers("/**").hasRole("ADMIN")

                // tout le reste = authentifié
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/auth/login")
                .loginProcessingUrl("/login")
                .successHandler(new RoleBasedAuthenticationSuccessHandler())
                .failureUrl("/auth/login?error=true")
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
