package com.j0aoarthur.pokerbank.security;


import com.j0aoarthur.pokerbank.entities.RefreshToken;
import com.j0aoarthur.pokerbank.entities.Account;
import com.j0aoarthur.pokerbank.entities.enums.Role;
import com.j0aoarthur.pokerbank.repositories.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
public class TokenService {

    @Value("${jwt.secret.password}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationInMinutes;

    @Value("${jwt.refresh.expiration}")
    private long refreshExpirationInDays;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public String generateUserToken(Account account) {
        return createToken(new HashMap<>(), account.getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expirationInMinutes * 60 * 1000))
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    public String generateClubToken(Account account, Long clubId, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("clubId", clubId);
        claims.put("role", role.name());

        return createToken(claims, account.getUsername());
    }

    public String generateRefreshToken(Account account) {
        return createRefreshToken(account, new HashMap<>());
    }

    public String generateClubRefreshToken(Account account, Long clubId, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("clubId", clubId);
        claims.put("role", role.name());
        return createRefreshToken(account, claims);
    }

    private String createRefreshToken(Account account, Map<String, Object> claims) {
        Instant expiryDate = Instant.now().plusSeconds(refreshExpirationInDays * 24 * 60 * 60);

        String refreshTokenString = Jwts.builder()
                .setClaims(claims)
                .setSubject(account.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(Date.from(expiryDate))
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();

        RefreshToken refreshToken = refreshTokenRepository.findByAccountId(account.getId())
                .orElse(new RefreshToken());

        refreshToken.setAccount(account);
        refreshToken.setToken(refreshTokenString);
        refreshToken.setExpiryDate(expiryDate);

        refreshTokenRepository.save(refreshToken);

        return refreshTokenString;
    }

    public boolean validateRefreshToken(String token) {
        Optional<RefreshToken> refreshTokenByToken = refreshTokenRepository.findByToken(token);
        return refreshTokenByToken.isPresent() && !isTokenExpired(token);
    }

    public CustomUserDetails buildUserDetailsFromToken(String token, Account account) {
        Claims claims = this.extractAllClaims(token);
        Long clubId = claims.get("clubId", Long.class);
        String roleString = claims.get("role", String.class);

        if (clubId != null && roleString != null) {
            Role role = Role.valueOf(roleString);
            return new CustomUserDetails(account, role);
        }
        return new CustomUserDetails(account);
    }

    @Transactional
    public void deleteRefreshTokenByToken(String token) {
        refreshTokenRepository.deleteByToken(token);
    }
}