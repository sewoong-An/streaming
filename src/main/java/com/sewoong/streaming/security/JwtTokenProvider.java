package com.sewoong.streaming.security;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.sewoong.streaming.dto.UserCustom;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtTokenProvider {
  private final Key key;

  public JwtTokenProvider(@Value("${jwt.secret}") String secretKey) {
    byte[] keyBytes = Decoders.BASE64.decode(secretKey);
    this.key = Keys.hmacShaKeyFor(keyBytes);
  }

  // 유저 정보를 가지고 AccessToken, RefreshToken 을 생성하는 메서드
  public TokenInfo generateToken(Authentication authentication) {
    // 권한 가져오기
    String authorities = authentication.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .collect(Collectors.joining(","));

    long now = (new Date()).getTime();
    // Access Token 생성
    // Date 생성자에 삽입하는 숫자 86480000
    // -> 30분: 30(m) * 60(s) * 1000(ms) = 1800000
    // -> 1일: 24(h) * 60(m) * 60(s) * 1000(ms) = 86400000
    Date accessTokenExpiresIn = new Date(now + 1800000);
    String accessToken = Jwts.builder()
        .setSubject(authentication.getName())
        .claim("auth", authorities)
        .claim("member_code", ((UserCustom) authentication.getPrincipal()).getMemberCode())
        .setExpiration(accessTokenExpiresIn)
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();

    // Refresh Token 생성
    String refreshToken = Jwts.builder()
        .claim("member_code", ((UserCustom) authentication.getPrincipal()).getMemberCode())
        .setExpiration(new Date(now + 86400000))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();

    return TokenInfo.builder()
        .grantType("Bearer")
        .accessToken(accessToken)
        .refreshToken(refreshToken)
        .build();
  }

  // JWT 토큰을 복호화하여 토큰에 들어있는 정보를 꺼내는 메서드
  public Authentication getAuthentication(String accessToken) {
    // 토큰 복호화
    Claims claims = parseClaims(accessToken);

    if (claims.get("auth") == null) {
      throw new RuntimeException("권한 정보가 없는 토큰입니다.");
    }

    // 클레임에서 권한 정보 가져오기
    Collection<? extends GrantedAuthority> authorities = Arrays.stream(claims.get("auth").toString().split(","))
        .map(SimpleGrantedAuthority::new)
        .collect(Collectors.toList());

    // UserDetails 객체를 만들어서 Authentication 리턴
    UserCustom principal = new UserCustom(claims.getSubject(), "", authorities, (Integer) claims.get("member_code"));
    return new UsernamePasswordAuthenticationToken(principal, "", authorities);
  }

  public Integer getMemberCodeByRefreshToken(String refreshToken) {
    Claims claims = parseClaims(refreshToken);
    return (Integer) claims.get("member_code");
  }

  // 토큰 정보를 검증하는 메서드
  public boolean validateToken(String token) {
    // try {
    Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
    return true;
    // } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException
    // e) {
    // log.info("Invalid JWT Token", e);
    // } catch (ExpiredJwtException e) {
    // log.info("Expired JWT Token", e);
    // } catch (UnsupportedJwtException e) {
    // log.info("Unsupported JWT Token", e);
    // } catch (IllegalArgumentException e) {
    // log.info("JWT claims string is empty.", e);
    // }
    // return false;
  }

  private Claims parseClaims(String accessToken) {
    try {
      return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(accessToken).getBody();
    } catch (ExpiredJwtException e) {
      return e.getClaims();
    }
  }
}
