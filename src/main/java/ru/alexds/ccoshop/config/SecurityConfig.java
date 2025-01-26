package ru.alexds.ccoshop.config;

 import lombok.RequiredArgsConstructor;
 import org.springframework.context.annotation.Bean;
 import org.springframework.context.annotation.Configuration;
 import org.springframework.security.authentication.AuthenticationManager;
 import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
 import org.springframework.security.config.annotation.web.builders.HttpSecurity;
 import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
 import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
 import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
 import org.springframework.security.crypto.password.PasswordEncoder;
 import org.springframework.security.web.SecurityFilterChain;
 import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

 @Configuration
 @EnableWebSecurity
 @RequiredArgsConstructor
 public class SecurityConfig {

 @Bean
 public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
 http
 .csrf(AbstractHttpConfigurer::disable)
 .authorizeHttpRequests(auth -> auth
 // Публичные URL
 .requestMatchers("/login").permitAll() // Позволить доступ к странице входа
 .requestMatchers("/").permitAll()
 .requestMatchers("/**").permitAll()
 //                                "/",
 //                                "/register",
 //                                "/login",
 //                                "/css/**",
 //                                "/js/**",
 //                                "/images/**"
 //                        ).permitAll()

 //                        // URL для администраторов
 //                        .requestMatchers("/admin/**").hasRole("ADMIN")
 //
 //                        // URL для авторизованных пользователей
 //                        .requestMatchers("/profile/**", "/cart/**", "/orders/**").hasAnyRole("USER", "ADMIN")
 //
 //                        // Все остальные URL требуют аутентификации
 //                        .anyRequest().authenticated()
 )
 //                .formLogin(form -> form
 //                        .loginPage("/login")
 //                        .defaultSuccessUrl("/")
 //                        .failureUrl("/login?error=true")
 //                        .permitAll()
 //                )
 //                .logout(logout -> logout
 //                        .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
 //                        .logoutSuccessUrl("/login?logout=true")
 //                        .deleteCookies("JSESSIONID")
 //                        .invalidateHttpSession(true)
 //                        .clearAuthentication(true)
 //                        .permitAll()
 //                )
 .exceptionHandling(exc -> exc
 .accessDeniedPage("/error/403")
 );

 return http.build();
 }

 @Bean
 public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
 return authConfig.getAuthenticationManager();
 }

 @Bean
 public PasswordEncoder passwordEncoder() {
 return new BCryptPasswordEncoder();
 }
 }