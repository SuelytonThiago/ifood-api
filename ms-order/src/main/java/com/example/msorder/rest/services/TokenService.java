package com.example.msorder.rest.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    @Value("${jwt_secret_password}")
    private String jwtSecret;

    public DecodedJWT extractClaimsFromToken(String token, String jwtSecret) {
        try {
            Algorithm algorithm = Algorithm.HMAC512(jwtSecret);
            return JWT.require(algorithm).build().verify(token);
        } catch (Exception e) {
            return null;
        }
    }
    public Long getClaimId(HttpServletRequest request){
        var auth = request.getHeader("Authorization");
        var token = auth.replace("Bearer ","");
        var decodeJWT = extractClaimsFromToken(token,jwtSecret);
        return decodeJWT.getClaim("id").asLong();
    }

    public String getSubject(String token){
        return JWT.require(Algorithm.HMAC512(jwtSecret))
                .build()
                .verify(token)
                .getSubject();
    }
}
