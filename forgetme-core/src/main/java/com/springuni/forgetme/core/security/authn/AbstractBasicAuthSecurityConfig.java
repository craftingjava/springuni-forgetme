package com.springuni.forgetme.core.security.authn;

import static org.springframework.http.HttpStatus.UNAUTHORIZED;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.StringValueResolver;

@Slf4j
public abstract class AbstractBasicAuthSecurityConfig
    extends WebSecurityConfigurerAdapter implements EnvironmentAware, EmbeddedValueResolverAware {

  private Environment environment;
  private StringValueResolver valueResolver;

  @Override
  public void setEnvironment(Environment environment) {
    this.environment = environment;
  }

  @Override
  public void setEmbeddedValueResolver(StringValueResolver valueResolver) {
    this.valueResolver = valueResolver;
  }

  @Override
  protected final void configure(AuthenticationManagerBuilder auth) throws Exception {
    BasicAuthKeys basicAuthKeys = Binder.get(environment)
        .bind(getBasicAuthKeysPrefix(), BasicAuthKeys.class)
        .get();

    log.info(
        "Setting up access and secret keys for {}.",
        getClass().getSuperclass().getSimpleName()
    );

    basicAuthKeys.resolveKeys(valueResolver);

    auth.inMemoryAuthentication()
        .withUser(basicAuthKeys.getAccessKey())
        .password(basicAuthKeys.getSecretKey())
        .roles();
  }

  @Override
  protected final void configure(HttpSecurity httpSecurity) throws Exception {
    String urlPattern = getUrlPattern();

    httpSecurity
        .antMatcher(urlPattern)
        .csrf().disable()
        .httpBasic()
        .and()
        .exceptionHandling()
        .defaultAuthenticationEntryPointFor(
            new HttpStatusEntryPoint(UNAUTHORIZED),
            new AntPathRequestMatcher(urlPattern, null)
        )
        .and()
        .authorizeRequests()
        .antMatchers(urlPattern).authenticated();
  }

  @NonNull
  protected abstract String getBasicAuthKeysPrefix();

  @NonNull
  protected abstract String getUrlPattern();

}
