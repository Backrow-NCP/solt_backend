package org.backrow.solt.filter;

import io.jsonwebtoken.ExpiredJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.backrow.solt.service.CustomUserDetails;
import org.backrow.solt.service.LoginService;
import org.backrow.solt.service.UserDetailedServiceImpl;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
@RequiredArgsConstructor
@Log4j2
public class AuthenticationFilter extends OncePerRequestFilter {

    private final LoginService loginService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);
        if(jwt != null) {
            try{
            String email = loginService.getAuthUser(request);
            long memberId = loginService.getMemberId(email);

            CustomUserDetails userDetails = new CustomUserDetails.CustomUserDetailsBuilder()
                        .memberId(memberId)
                        .build();

            Authentication auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(auth);

//            기존의 이메일만 저장하는 방식
//            Authentication auth =
//                    new UsernamePasswordAuthenticationToken(email,
//                            null, java.util.Collections.emptyList());
//            SecurityContextHolder.getContext().setAuthentication(auth);

        } catch (ExpiredJwtException e){
                response.setHeader(HttpHeaders.EXPIRES,"AccessToken");
                }
            }
        filterChain.doFilter(request, response);
    }
}
