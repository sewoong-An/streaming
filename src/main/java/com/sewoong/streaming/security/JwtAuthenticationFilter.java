package com.sewoong.streaming.security;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.sewoong.streaming.ErrorCode;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

  private final JwtTokenProvider jwtTokenProvider;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    // 1. Request Header 에서 JWT 토큰 추출
    String token = resolveToken((HttpServletRequest) request);
    try {
      // 2. validateToken 으로 토큰 유효성 검사
      if (token != null && jwtTokenProvider.validateToken(token)) {
        // 토큰이 유효할 경우 토큰에서 Authentication 객체를 가지고 와서 SecurityContext 에 저장
        Authentication authentication = jwtTokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (SecurityException | MalformedJwtException e) {
      // Invalid JWT Token
      request.setAttribute("exception", ErrorCode.WRONG_TYPE_TOKEN.getCode());
    } catch (ExpiredJwtException e) {
      // Expired JWT Token
      request.setAttribute("exception", ErrorCode.EXPIRED_TOKEN.getCode());
    } catch (UnsupportedJwtException e) {
      // Unsupported JWT Token
      request.setAttribute("exception", ErrorCode.UNSUPPORTED_TOKEN.getCode());
    } catch (IllegalArgumentException e) {
      // JWT claims string is empty.
      request.setAttribute("exception", ErrorCode.WRONG_TYPE_TOKEN.getCode());
    } catch (Exception e) {
      request.setAttribute("exception", ErrorCode.UNKNOWN_ERROR.getCode());
    }

    chain.doFilter(request, response);
  }

  private String resolveToken(HttpServletRequest request) {
    // Request Header 에서 토큰 정보 추출
    String bearerToken = request.getHeader("Authorization");
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
      return bearerToken.substring(7);
    }

    return null;
  }
}
