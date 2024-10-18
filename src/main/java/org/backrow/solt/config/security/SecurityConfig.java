package org.backrow.solt.config.security;

import lombok.RequiredArgsConstructor;
import org.backrow.solt.security.AuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;
    private final AuthenticationFilter authenticationFilter;
    private final AuthEntryPoint authEntryPoint;

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .passwordEncoder(new BCryptPasswordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
                .authorizeRequests()
//                .antMatchers(HttpMethod.GET, "/", "/swagger-ui/**", "/v3/api-docs/**",
//                        "/plan/recom",
//                        "/like/*",
//                        "/login/password", "/login/check",
//                        "/reply/list/*",
//                        "/board/*", "/board/list", "/board/list/*",
//                        "/email/verifications",
//                        "/loadbalancer").permitAll()
//                .antMatchers(HttpMethod.POST,
//                        "/file",
//                        "/login", "/login/token", "/login/register",
//                        "/email/verification-requests").permitAll()
//                .antMatchers(HttpMethod.DELETE,
//                        "/file").permitAll()
//                .anyRequest().authenticated().and()
//                .addFilterBefore(authenticationFilter, UsernamePasswordAuthenticationFilter.class)
//                .exceptionHandling().authenticationEntryPoint(authEntryPoint);
                .anyRequest().permitAll(); // 모든 요청 허가 (테스트 전용)
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList("*"));
        config.setAllowedMethods(Arrays.asList("*"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(false);
        config.applyPermitDefaultValues();

        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
