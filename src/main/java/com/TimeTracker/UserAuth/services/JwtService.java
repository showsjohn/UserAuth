package com.TimeTracker.UserAuth.services;

import com.TimeTracker.UserAuth.models.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.JwtParserBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Date;
import java.util.function.Function;

@Service
public class JwtService {

    private final String SECRET_KEY = "f8ecf14e05262f840676e6356e66a410d5cbc662b14fa9f2ba3f691566172867";


    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    public boolean isValid(String token, UserDetails userDetails) {
        String username = extractUsername(token);
        return username != null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractAllClaims(token).getExpiration();

    }


    private Claims extractAllClaims(String token)
    {
        JwtParserBuilder parserBuilder = Jwts.parser(); // create parser builder object
        parserBuilder.verifyWith(getSignKey()); // configures the parser to use the signing key
        JwtParser parser = parserBuilder.build(); // finalize and return the parser object
        return parser.parseSignedClaims(token).getPayload(); // parse the token and return the claims

    }
    public String generateToken(UserEntity user) {
        String token = Jwts
                .builder()
                .subject(user.getUsername())
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + 24 * 60 * 60 * 1000 )) // set expiration time for 24 hours
                .signWith(getSignKey())
                .compact();

        return token;
    }

    private SecretKey getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
