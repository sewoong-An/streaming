package com.sewoong.streaming.security;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtTokenProvider jwtTokenProvider;

  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // .httpBasic().disable().csrf().disable(): rest api이므로 basic auth 및 csrf 보안을
        // 사용하지 않는다는 설정
        .httpBasic((httpBasicConfig) -> httpBasicConfig.disable())
        .csrf((csrfConfig) -> csrfConfig.disable())

        // .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS):
        // JWT를 사용하기 때문에 세션을 사용하지 않는다는 설정
        .sessionManagement((sessionConfig) -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

        .headers((headerConfig) -> headerConfig.frameOptions(frameOptionsConfig -> frameOptionsConfig.sameOrigin()))

        // 요청에 대한 인증 설정
        .authorizeHttpRequests((authorizeRequests) -> authorizeRequests
            // 정적자원에 대해서 요청을 허가한다는 설정
            .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()

            // requestMatchers("호출 URL").permitAll() : 해당 요청에 대해서는 모든 요청을 허가한다는 설정
            // requestMatchers("호출 URL").authenticated() : 해당 요청에 대해서는 인증을 필요로 한다는 설정
            // requestMatchers("호출 URL").hasRole("권한명"): 특정 권한이 있어야 요청할 수 있다는 설정
            // requestMatchers("호출 URL").hasAnyRole("권한명1", "권한명2"): 권한1 또는 권한2 이 있어야 요청할 수
            // 있다는 설정
            .requestMatchers("/api/refreshToken", "/api/hello", "/api/member/login", "/api/member/join").permitAll()
            .requestMatchers("/api/**").authenticated()
            .anyRequest().permitAll())
            

        // exceptionHandling(): 예외처리
        // .authenticationEntryPoint(메소드): 인증 실패 시 처리할 메소드
        .exceptionHandling(
            (exceptionConfig) -> exceptionConfig.authenticationEntryPoint(new CustomAuthenticationEntryPoint()))
        .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return PasswordEncoderFactories.createDelegatingPasswordEncoder();
  }
}
