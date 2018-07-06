package com.springuni.forgetme.ui.config;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.endpoint.NimbusAuthorizationCodeTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AccessTokenResponseClient;
import org.springframework.security.oauth2.client.endpoint.OAuth2AuthorizationCodeGrantRequest;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;

@Configuration
@EnableWebSecurity
@Order(2)
public class UISecurityConfig extends WebSecurityConfigurerAdapter {

  private static final String PAGES_URL_PATTERN = "/pages/**";
  private static final String STATIC_URL_PATTERN = "/assets/**";

  private static final String LOGIN_URL = "/login/oauth2";
  private static final String DEFAULT_SUCCESS_URL = "/pages/subscriber";

  @Bean
  public OAuth2AccessTokenResponseClient<OAuth2AuthorizationCodeGrantRequest> accessTokenResponseClient() {
    return new NimbusAuthorizationCodeTokenResponseClient();
  }

  @Bean
  public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
    return new HttpSessionOAuth2AuthorizationRequestRepository();
  }

  @Bean
  public OAuth2UserService<OAuth2UserRequest, OAuth2User> defaultOAuth2UserService() {
    return new DefaultOAuth2UserService();
  }

  @Bean
  public OAuth2UserService<OAuth2UserRequest, OAuth2User> oauth2UserService() {
    return defaultOAuth2UserService();
  }

  @Override
  protected void configure(HttpSecurity httpSecurity) throws Exception {
    httpSecurity
        .csrf()
        .and()
        .oauth2Login()
        .authorizationEndpoint()
        .authorizationRequestRepository(authorizationRequestRepository())
        .and()
        .tokenEndpoint().accessTokenResponseClient(accessTokenResponseClient())
        .and()
        .userInfoEndpoint().userService(oauth2UserService()).and()
        .loginPage("/login/oauth2")
        .defaultSuccessUrl(DEFAULT_SUCCESS_URL, true)
        .and()
        .exceptionHandling()
        .defaultAuthenticationEntryPointFor(
            new LoginUrlAuthenticationEntryPoint(LOGIN_URL),
            AnyRequestMatcher.INSTANCE
        )
        .and()
        .authorizeRequests()
        .antMatchers(GET, "/").permitAll()
        .antMatchers(GET, LOGIN_URL).permitAll()
        .antMatchers(GET, STATIC_URL_PATTERN).permitAll()
        .antMatchers(GET, PAGES_URL_PATTERN).hasAuthority("ROLE_USER")
        .antMatchers(POST, PAGES_URL_PATTERN).hasAuthority("ROLE_USER")
        .anyRequest().denyAll()
        .and();
  }

}
