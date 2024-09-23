package org.backrow.solt.filter;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import org.backrow.solt.service.LoginService;
import org.backrow.solt.service.TokenService;
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

@Component
@RequiredArgsConstructor
public class AuthenticationFilter extends OncePerRequestFilter {

    private final LoginService loginService;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(jwt != null) {
            try{
            String email = loginService.getAuthUser(request);
            Authentication auth =
                    new UsernamePasswordAuthenticationToken(email,
                            null, java.util.Collections.emptyList());
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (ExpiredJwtException e){
            String refreshToken = request.getParameter("refreshToken");
            if(refreshToken != null) {
                String email = loginService.getAuthUser(request);
            if(tokenService.validateRefreshToken(email, refreshToken)) {
                String newAccessToken = loginService.getToken(email);
                response.setHeader("Authorization", "Bearer " + newAccessToken);
                Authentication auth =  new UsernamePasswordAuthenticationToken(email,
                        null, java.util.Collections.emptyList());
                SecurityContextHolder.getContext().setAuthentication(auth);
            }
            }
            }
        }
        filterChain.doFilter(request, response);
    }
}
