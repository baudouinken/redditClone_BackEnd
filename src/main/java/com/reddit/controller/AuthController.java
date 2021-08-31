package com.reddit.controller;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reddit.dto.AuthenticationResponse;
import com.reddit.dto.RefreshTokenRequest;
import com.reddit.dto.RegisterRequest;
import com.reddit.dto.SignInRequest;
import com.reddit.service.AuthService;
import com.reddit.service.RefreshTokenService;

import io.jsonwebtoken.security.InvalidKeyException;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins="http://localhost:4200")
public class AuthController {

  @Autowired
  private AuthService authService;
  
  @Autowired
  private RefreshTokenService refreshTokenService;

  @PostMapping("/signup")
  public ResponseEntity<String> signup(@RequestBody RegisterRequest registerRequest) {
    authService.signUp(registerRequest);
    return new ResponseEntity<>("User Registration Successfully", HttpStatus.OK);
  }

  @PostMapping("/signin")
  public AuthenticationResponse signIn(@RequestBody SignInRequest signInRequest)
      throws InvalidKeyException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
    return authService.signIn(signInRequest);
  }

  @GetMapping("/accountVerification/{token}")
  public ResponseEntity<String> verifyAccount(@PathVariable String token) {
    authService.verifyAccount(token);
    return new ResponseEntity<>("Account Activated Successfully", HttpStatus.OK);
  }

  @PostMapping("/refresh/token")
  public AuthenticationResponse refreshTokens(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) throws InvalidKeyException, UnrecoverableKeyException, KeyStoreException, NoSuchAlgorithmException {
    return authService.refreshToken(refreshTokenRequest);
  }
  
  @PostMapping("/logout")
  public ResponseEntity<String> logout(@Valid @RequestBody RefreshTokenRequest refreshTokenRequest) {
    refreshTokenService.deleteRefreshToken(refreshTokenRequest.getRefreshToken());
    return ResponseEntity.ok("Refreshtoken deleted");
  }

}
