package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests()
                .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()  // ← 追加①: H2コンソールへの許可
                .requestMatchers("/reports", "/reports/list", "/reports/my").hasAnyRole("STAFF", "VIEWER")
                .requestMatchers("/reports/edit/**", "/reports/delete/**").hasRole("STAFF")
                .requestMatchers("/api/events/**").hasAnyRole("STAFF", "VIEWER")  // GETはVIEWERも可
                .requestMatchers(HttpMethod.POST, "/api/events/**").hasRole("STAFF")  // 登録はSTAFFのみ
                .requestMatchers(HttpMethod.PUT, "/api/events/**").hasRole("STAFF")   // 編集も
                .requestMatchers(HttpMethod.DELETE, "/api/events/**").hasRole("STAFF") // 削除も

                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login")
                .defaultSuccessUrl("/reports", true)
                .permitAll()
                .and()
            .logout()
                .logoutSuccessUrl("/login?logout")
                .permitAll()
                .and()
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/h2-console/**")  // ← 追加②: H2用にCSRF無効化
            )
            .headers(headers -> headers
                .frameOptions(frame -> frame.disable())  // ← 追加③: frameを許可（H2が必要）
            );

        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
