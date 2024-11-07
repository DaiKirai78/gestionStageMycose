package com.projet.mycose.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import com.projet.mycose.exceptions.InvalidJwtTokenException;

import java.nio.file.AccessDeniedException;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
	private final int expirationInMs;
	private final String jwtSecret;

	public JwtTokenProvider(
			@Value("${application.security.jwt.expiration}") int expirationInMs,
			@Value("${application.security.jwt.secret-key}") String jwtSecret) {
		this.expirationInMs = expirationInMs;
		this.jwtSecret = jwtSecret;
	}

	public String generateToken(Authentication authentication) {
		try {
			long nowMillis = System.currentTimeMillis();
			JwtBuilder builder = Jwts.builder()
					.setSubject(authentication.getName())
					.setIssuedAt(new Date(nowMillis))
					.setExpiration(new Date(nowMillis + expirationInMs))
					.claim("authorities", authentication.getAuthorities())
					.signWith(key());
			return builder.compact();
		} catch (Exception e) {
			throw new JwtException("La génération de token a échouée : " + e.getMessage());
		}
	}

	private Key key() {
		return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	}

	public String getEmailFromJWT(String token) throws AccessDeniedException {
		try {
			return Jwts.parserBuilder()
					.setSigningKey(key())
					.build()
					.parseClaimsJws(token)
					.getBody()
					.getSubject();
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw new AccessDeniedException("Token Invalide");
		}
	}

	public void validateToken(String token) {
		if (token == null || token.trim().isEmpty()) {
			throw new InvalidJwtTokenException(HttpStatus.BAD_REQUEST, "JWT claims string is empty");
		}
		try {
			Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token);
		} catch (SecurityException ex) {
			throw new InvalidJwtTokenException(HttpStatus.BAD_REQUEST, "Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			throw new InvalidJwtTokenException(HttpStatus.BAD_REQUEST, "Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			throw new InvalidJwtTokenException(HttpStatus.BAD_REQUEST, "Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			throw new InvalidJwtTokenException(HttpStatus.BAD_REQUEST, "Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			throw new InvalidJwtTokenException(HttpStatus.BAD_REQUEST, "JWT claims string is empty");
		}
	}
}
