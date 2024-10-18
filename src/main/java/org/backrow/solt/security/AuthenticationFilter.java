package org.backrow.solt.security;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.service.security.LoginService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
@Log4j2
public class AuthenticationFilter extends OncePerRequestFilter {

    private final LoginService loginService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (jwt != null) {
            try{
                String email = loginService.getAuthUser(request);
                Long memberId = loginService.getMemberId(request);

                CustomUserDetails userDetails = new CustomUserDetails.CustomUserDetailsBuilder()
                        .username(email)
                        .password("")
                        .memberId(memberId)
                        .authorities(Collections.emptyList())
                        .build();

                Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(auth);
            } catch (ExpiredJwtException e){
                log.error(e.getMessage());
                response.setHeader(HttpHeaders.EXPIRES,"AccessToken");
            } catch (Exception e) {
                log.error("Token parsing error", e);
            }
        }
        filterChain.doFilter(request, response);
    }
}
