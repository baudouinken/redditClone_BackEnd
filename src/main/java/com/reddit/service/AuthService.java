package com.reddit.service;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.reddit.dto.AuthenticationResponse;
import com.reddit.dto.RefreshTokenRequest;
import com.reddit.dto.RegisterRequest;
import com.reddit.dto.SignInRequest;
import com.reddit.exception.SpringRedditException;
import com.reddit.model.NotificationEmail;
import com.reddit.model.User;
import com.reddit.model.VerificationToken;
import com.reddit.repository.UserRepository;
import com.reddit.repository.VerificationTokenRepository;
import com.reddit.security.JwtProvider;

import io.jsonwebtoken.security.InvalidKeyException;

@Service
public class AuthService {

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private VerificationTokenRepository verificationTokenRepository;

  @Autowired
  private AuthenticationManager authenticationManager;

  @Autowired
  private MailService mailService;

  @Autowired
  private JwtProvider jwtProvider;

  @Autowired
  private RefreshTokenService refreshTokenService;

  @Transactional
  public void signUp(RegisterRequest registerRequest) {
    User user = new User();
    user.setUsername(registerRequest.getUsername());
    user.setEmail(registerRequest.getEmail());
    user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
    user.setCreated(Instant.now());
    user.setEnabled(false);
    
    if(userRepository.countByUsername(registerRequest.getUsername()) > 0) {
      throw new SpringRedditException("This username already exist");
    }
    
    if(userRepository.countByEmail(registerRequest.getEmail()) > 0) {
      throw new SpringRedditException("This email already exist");
    }
    
    userRepository.save(user);

    String token = generateVerificationToken(user);

    mailService.sendMessage(new NotificationEmail("Please Activate Your Account ", user.getEmail(),
        "Thank you to signing up to Spring Reddit, " +
            "\n Please click on link below to activate your account : " +
            "http://localhost:8080/api/auth/accountVerification/" + token));
  }

  private String generateVerificationToken(User user) {
    String token = UUID.randomUUID().toString();
    VerificationToken verificationToken = new VerificationToken();
    verificationToken.setToken(token);
    verificationToken.setUser(user);

    verificationTokenRepository.save(verificationToken);

    return token;

  }

  public void verifyAccount(String token) {
    // TODO Auto-generated method stub
    Optional<VerificationToken> verificationToken = verificationTokenRepository.findByToken(token);
    verificationToken.orElseThrow(() -> new SpringRedditException("Invalid Token"));

    fetchUserAndEnable(verificationToken.get());
  }

  @Transactional
  private void fetchUserAndEnable(VerificationToken verificationToken) {
    // TODO Auto-generated method stub
    String username = verificationToken.getUser().getUsername();
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> new SpringRedditException("User not found with name " + username));

    user.setEnabled(true);

    userRepository.save(user);
  }

  public AuthenticationResponse signIn(SignInRequest signInRequest)
      throws InvalidKeyException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
    // TODO Auto-generated method stub
    Authentication authenticate = authenticationManager.authenticate(
        new UsernamePasswordAuthenticationToken(signInRequest.getUsername(), signInRequest.getPassword()));
    SecurityContextHolder.getContext().setAuthentication(authenticate);

    return AuthenticationResponse.builder()
        .token(jwtProvider.generateToken(authenticate))
        .username(signInRequest.getUsername())
        .expireAt(Instant.now().plusMillis(jwtProvider.getExpirationTime()))
        .refresToken(refreshTokenService.generateRefreshToken().getToken())
        .build();
  }

  public User getCurrentUser() {
    org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) SecurityContextHolder
        .getContext().getAuthentication().getPrincipal();
    return userRepository.findByUsername(principal.getUsername())
        .orElseThrow(() -> new UsernameNotFoundException("User name not found - " + principal.getUsername()));
  }

  public AuthenticationResponse refreshToken(@Valid RefreshTokenRequest refreshTokenRequest)
      throws InvalidKeyException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
    // TODO Auto-generated method stub
    refreshTokenService.validateRefreshToken(refreshTokenRequest.getRefreshToken());
    String token = jwtProvider.generateTokenWithUserName(refreshTokenRequest.getUsername());
    return AuthenticationResponse.builder()
        .token(token)
        .refresToken(refreshTokenRequest.getRefreshToken())
        .expireAt(Instant.now().plusMillis(jwtProvider.getExpirationTime()))
        .username(refreshTokenRequest.getUsername())
        .build();
  }

}
