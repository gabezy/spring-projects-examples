package br.com.gabezy.cashcard.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/cashcards/**") // mapping all HTTP request to /cashcards
                        .hasRole("CARD-OWNER")) //enable RBAC
                .httpBasic(Customizer.withDefaults()) // using HTTP Basic authentication (username and password)
                .csrf(AbstractHttpConfigurer::disable) // disabling CSRF
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService testOnlyUsers(PasswordEncoder passwordEncoder) {
        User.UserBuilder user = User.builder();
        UserDetails sarah = user
                .username("sarah1")
                .password(passwordEncoder().encode("abc123"))
                .roles("CARD-OWNER") // new role
                .build();

        UserDetails hankOwnsNoCards = user
                .username("hank-owns-no-cards")
                .password(passwordEncoder.encode("qrs456"))
                .roles("NON-OWNER") // new role
                .build();

        UserDetails kumar = user
                .username("kumar2")
                .password(passwordEncoder.encode("xyz789"))
                .roles("CARD-OWNER")
                .build();

        UserDetails[] users = {sarah, hankOwnsNoCards, kumar};

        return new InMemoryUserDetailsManager(users);
    }

}
