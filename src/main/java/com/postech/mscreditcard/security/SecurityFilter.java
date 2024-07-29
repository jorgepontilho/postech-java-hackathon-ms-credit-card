package com.postech.mscreditcard.security;

import java.io.IOException;

import com.postech.mscreditcard.dto.CustomerDTO;
import com.postech.mscreditcard.entity.Customer;
import com.postech.mscreditcard.entity.User;
import com.postech.mscreditcard.gateway.CreditCardGateway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.postech.mscreditcard.repository.CustomerRepository;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class SecurityFilter extends OncePerRequestFilter {
    @Autowired
    TokenService tokenService;
    @Autowired
    CustomerRepository customerRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        validateRequest(request);
        filterChain.doFilter(request, response);
    }

    public User validToken(String token) {
        try {
            String login = tokenService.validateToken(token);
            if (login.equals("")) {
                return null;
            }
            User user = new User();// userRepository.findByLogin(login);
            Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);
            return user;
        } catch (Exception e) {
            return null;
        }
    }

    private void validateRequest(HttpServletRequest request) {
        var token = this.recoverToken(request);
        if (token == null) {
            request.setAttribute("error_code", HttpStatus.UNAUTHORIZED);
            request.setAttribute("error", "Acesso não autorizado");
            return;
        }

        User user = validToken(token);
        if (user == null) {
            request.setAttribute("error_code", HttpStatus.UNAUTHORIZED);
            request.setAttribute("error", "Bearer token inválido");
            return;
        }
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null) return null;
        return authHeader.replace("Bearer ", "");
    }
}