package com.reddit.security;

import java.io.IOException;
import java.security.KeyStoreException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.SignatureException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  @Autowired
  private JwtProvider jwtProvider;

  @Autowired
  private UserDetailsService userDetailsService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    // TODO Auto-generated method stub
    String bearerToken = getJwtFromResquest(request);


      try {
        if (StringUtils.hasText(bearerToken) && jwtProvider.validateToken(bearerToken)) {
          String username = jwtProvider.getUsernameFromToken(bearerToken);

          UserDetails userDetails = userDetailsService.loadUserByUsername(username);

          UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userDetails, null,
              userDetails.getAuthorities());

          auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

          SecurityContextHolder.getContext().setAuthentication(auth);
        }
      } catch (SignatureException | ExpiredJwtException | UnsupportedJwtException | MalformedJwtException
          | UsernameNotFoundException | IllegalArgumentException | KeyStoreException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }

      filterChain.doFilter(request, response);


  }

  private String getJwtFromResquest(HttpServletRequest request) {
    // TODO Auto-generated method stub
    String bearerToken = request.getHeader("Authorization");

    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7);
    }
    return bearerToken;
  }

}
