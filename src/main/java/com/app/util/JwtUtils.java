package com.app.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class JwtUtils {

    @Value("${security.jwt.key.private}")
    private String privatekey;

    @Value("${security.jwt.user.generator}")
    private String userGenerator;


    public String createToken(Authentication authentication){

        //definimos el algoritmo para generar el token
        Algorithm algorithm = Algorithm.HMAC256(this.privatekey);

        // extraemos el usuario
        String userName = algorithm.getName().toString();

        // extraemos los permisos separados por comas
        String authorities =authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        // generamos el token
        String jwtToken = JWT.create()
                //ususer generador de token
                . withIssuer(this.userGenerator)
                // a quien se le va a generar el token
                . withSubject(userName)
                //claim en el payload//
                . withClaim("authorities",authorities) // permisos
                . withIssuedAt(new Date()) // fecha creacion
                .withExpiresAt(new Date(System.currentTimeMillis()+900000)) // fecha expiracion en milisegundos
                .withJWTId(UUID.randomUUID().toString()) // id para el token
                .withNotBefore(new Date(System.currentTimeMillis())) //token activo desde este momento
                .sign(algorithm); // firma

            return jwtToken;
    }

    public DecodedJWT valideteToken (String token){

        try {
            //definimos el algoritmo para generar el token
            Algorithm algorithm = Algorithm.HMAC256(this.privatekey);

            JWTVerifier verifier  = JWT.require(algorithm)
                    .withIssuer(this.userGenerator)
                    .build();

           DecodedJWT decodedJWT =  verifier.verify(token);
            return decodedJWT;
        }catch (JWTVerificationException e){
            throw new JWTVerificationException("token invalid, not authorized");
        }

    }


    public String extractUsername(DecodedJWT decodedJWT){
        return decodedJWT.getSubject().toString();
    }

    public Claim getSpecificClaim(DecodedJWT decodedJWT, String claimName){
        return decodedJWT.getClaim(claimName);
    }

    public Map<String, Claim> returnAllClaims(DecodedJWT decodedJWT){
        return decodedJWT.getClaims();
    }
}
