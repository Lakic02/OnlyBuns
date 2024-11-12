package com.example.onlybuns.config;

import java.util.Collections;
import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.example.onlybuns.domain.JWTUser;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter{
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7); // Preuzmi token bez "Bearer "
        }

        if (token != null) {
            try {
                JWTUser jwtUser = JWTDecoder.verifyToken(token); // Dekodiraj token i preuzmi korisnika
                //System.out.println("JWTUSER ID: "+jwtUser.id);
                if (jwtUser != null) {
                    SimpleGrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + jwtUser.getRole().toUpperCase());

                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            jwtUser, null, Collections.singleton(authority));
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Postavi korisnika u SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                // Loguj ili obradi grešku dekodiranja tokena
                System.out.println("Invalid token: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
