package com.projet.mycose.security;

import com.projet.mycose.repository.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {

    private final JwtTokenProvider jwtTokenProvider;
    private final UtilisateurRepository utilisateurRepository;
    private final JwtAuthenticationEntryPoint authenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(POST, "/utilisateur/login").permitAll()
                        .requestMatchers(POST, "/etudiant/register").permitAll()
                        .requestMatchers(POST, "/etudiant/register/check-for-conflict").permitAll()
                        .requestMatchers(POST, "/entreprise/register").permitAll()
                        .requestMatchers(POST, "/entreprise/register/check-for-conflict").permitAll()
                        .requestMatchers(POST, "/enseignant/register").permitAll()
                        .requestMatchers(POST, "/enseignant/register/check-for-conflict").permitAll()
                        .requestMatchers(GET, "/utilisateur/*").hasAnyAuthority("GESTIONNAIRE_STAGE", "ETUDIANT", "EMPLOYEUR", "ENSEIGNANT")
                        .requestMatchers("/etudiant/**").hasAuthority("ETUDIANT")
                        .requestMatchers("/enseignant/**").hasAuthority("ENSEIGNANT")
                        .requestMatchers("/entreprise/**").hasAuthority("EMPLOYEUR")
                        .requestMatchers("/enseignant/**").hasAuthority("ENSEIGNANT")
                        .requestMatchers("/gestionnaire/**").hasAuthority("GESTIONNAIRE_STAGE")
                        .requestMatchers("/api/cv/waitingcv").hasAuthority("GESTIONNAIRE_STAGE")
                        .requestMatchers("/api/cv/pages").hasAuthority("GESTIONNAIRE_STAGE")
                        .requestMatchers("/api/cv/accept").hasAuthority("GESTIONNAIRE_STAGE")
                        .requestMatchers("/api/cv/refuse").hasAuthority("GESTIONNAIRE_STAGE")
                        .requestMatchers("/api/application-stage/apply").hasAuthority("ETUDIANT")
                        .requestMatchers("/api/application-stage/my-applications").hasAuthority("ETUDIANT")
                        .requestMatchers("/api/application-stage/get/*").hasAuthority("EMPLOYEUR")
                        .requestMatchers("/api/application-stage/my-applications/status/*").hasAnyAuthority("GESTIONNAIRE_STAGE", "EMPLOYEUR")
                        .requestMatchers("/api/application-stage/application/*").hasAuthority("EMPLOYEUR")
                        .requestMatchers("/api/application-stage/status/*").hasAuthority("GESTIONNAIRE_STAGE")

                        .requestMatchers("/**").permitAll()
                        //Laisser en commentaire car c'est utile pour faire des postman lorsqu'on veut tester des features :)
                        .anyRequest().denyAll()
                )
                .sessionManagement((secuManagement) -> {
                    secuManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(configurer -> configurer.authenticationEntryPoint(authenticationEntryPoint))
        ;
        return http.build();
    }

    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() throws Exception {
        return new JwtAuthenticationFilter(jwtTokenProvider, utilisateurRepository);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration
    ) throws Exception{
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
}
