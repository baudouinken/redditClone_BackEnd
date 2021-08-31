package com.reddit.security;

import static java.util.Date.from;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Instant;
import java.util.Date;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.InvalidKeyException;
import io.jsonwebtoken.security.SignatureException;
@Service
public class JwtProvider {

  private KeyStore keyStore;

  @Value("${jwt.expiration.time}")
  private Long jwtExpirationInMillis;

  @PostConstruct
  public void init() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException {
    keyStore = keyStore.getInstance("JKS");
    InputStream resourceAsStream = getClass().getResourceAsStream("/springblog.jks");
    keyStore.load(resourceAsStream, "secret".toCharArray());
  }

  public String generateToken(Authentication authentication)
      throws InvalidKeyException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
    User principal = (User) authentication.getPrincipal();
    return Jwts.builder()
        .setSubject(principal.getUsername())
        .signWith(getPrivateKey())
        .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
        .compact();
  }
  
  public String generateTokenWithUserName(String username) throws InvalidKeyException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
    return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(from(Instant.now()))
            .signWith(getPrivateKey())
            .setExpiration(Date.from(Instant.now().plusMillis(jwtExpirationInMillis)))
            .compact();
}

  private PrivateKey getPrivateKey() throws UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
    // TODO Auto-generated method stub
    return (PrivateKey) keyStore.getKey("springblog", "secret".toCharArray());
  }

  public boolean validateToken(String jwt) throws SignatureException, ExpiredJwtException, UnsupportedJwtException,
      MalformedJwtException, IllegalArgumentException, KeyStoreException {
    Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(jwt);
    return true;
  }

  private PublicKey getPublicKey() throws KeyStoreException {
    // TODO Auto-generated method stub
    return keyStore.getCertificate("springblog").getPublicKey();
  }

  public String getUsernameFromToken(String token) throws SignatureException, ExpiredJwtException,
      UnsupportedJwtException, MalformedJwtException, IllegalArgumentException, KeyStoreException {
    Claims claims = Jwts.parser().setSigningKey(getPublicKey()).parseClaimsJws(token).getBody();
    return claims.getSubject();
  }

  public Long getExpirationTime() {
    return jwtExpirationInMillis;
  }

}
