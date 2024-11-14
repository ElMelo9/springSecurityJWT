package com.app.config.filter;

import com.app.util.JwtUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

public class JwtTokenValidator extends OncePerRequestFilter {

    private JwtUtils jwtUtils;

    public JwtTokenValidator(JwtUtils jwtUtils) {
        this.jwtUtils = jwtUtils;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request
            ,@NonNull HttpServletResponse response
            ,@NonNull FilterChain filterChain) throws ServletException, IOException {

        String jwtToken = request.getHeader(HttpHeaders.AUTHORIZATION);

        //validamos que hay token en pa request
        if(jwtToken!=null){
            //extraemos solo el token: bearer xxxxxxx
            jwtToken = jwtToken.substring(7);
            //validamos el token
            DecodedJWT decodedJWT =  jwtUtils.valideteToken(jwtToken);
            //extraemos el usuario
            String username = jwtUtils.extractUsername(decodedJWT);
            //extraemos los permisos convertidos a String
            String stringAuthorities = jwtUtils.getSpecificClaim(decodedJWT,"authorities" ).asString();
            //convertimos los permisos a una lista GrantedAuthority
            Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(stringAuthorities);
            //agregamos el usuario al contexto de Security
            SecurityContext context = SecurityContextHolder.getContext();
            //agregamos los permisos al contexto de Security
            Authentication authentication   = new UsernamePasswordAuthenticationToken(username, null, authorities);
            context.setAuthentication(authentication);
            SecurityContextHolder.setContext(context);
        }
        filterChain.doFilter(request,response);

    }
}
