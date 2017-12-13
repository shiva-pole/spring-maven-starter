package demo.configuration.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.config.annotation.builders.InMemoryClientDetailsServiceBuilder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.InMemoryTokenStore;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.header.writers.frameoptions.RegExpAllowFromStrategy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;

import demo.configuration.constants.ConfigConstants;
import demo.configuration.security.auth.AuthConfig;
import demo.configuration.security.auth.provider.DemoAuthProvider;
import demo.configuration.security.auth.provider.OAuthUserDetailsService;
import demo.configuration.security.domain.OAuthClient;
import demo.configuration.security.filter.SecurityFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
public class MultiHttpSecurityConfig {

	@Configuration
	@Order(100)
	public static class SessionSecurityConfig extends WebSecurityConfigurerAdapter {

		@Autowired
		private DemoAuthProvider authenticationProvider;

		@Override
		protected void configure(HttpSecurity http) throws Exception {

			http.csrf().disable();
			http.headers().addHeaderWriter(new XFrameOptionsHeaderWriter(new RegExpAllowFromStrategy(".*")));
			http.antMatcher(ConfigConstants.REST_API + "/**").sessionManagement().sessionFixation().none()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().exceptionHandling()
					.authenticationEntryPoint(new Http403ForbiddenEntryPoint()).and()
					.addFilterBefore(new SecurityFilter(authenticationManagerBean()), BasicAuthenticationFilter.class);
			http.csrf().disable().anonymous().disable().authorizeRequests().antMatchers("/oauth/token").permitAll();
			return;
		}

		@Bean
		public AuthenticationManager authenticationManagerBean() throws Exception {

			return super.authenticationManagerBean();
		}

		@Override
		protected void configure(AuthenticationManagerBuilder authManagerBuilder) throws Exception {

			authManagerBuilder.authenticationProvider(authenticationProvider);
		}
	}

	@Configuration
	@Order(101)
	public static class BasicSecurityConfig extends WebSecurityConfigurerAdapter {

		@Autowired
		private AuthConfig authConfig;

		@Override
		protected void configure(HttpSecurity http) throws Exception {

			BasicAuthenticationEntryPoint authenticationEntryPoint = new BasicAuthenticationEntryPoint();
			authenticationEntryPoint.setRealmName(authConfig.getRealm());
			if (authConfig.getSecurityBypassUrls() != null) {
				http.csrf().disable().authorizeRequests().antMatchers(authConfig.getSecurityBypassUrls()).permitAll();
			}
		}

	}

	@Configuration
	@EnableAuthorizationServer
	public static class OAuth2AuthServerConfig extends AuthorizationServerConfigurerAdapter {

		@Autowired
		private AuthenticationManager authenticationManager;
		@Autowired
		private AuthConfig authConfig;
		@Autowired
		private OAuthUserDetailsService userDetailsService;

		@Override
		public void configure(ClientDetailsServiceConfigurer clients) throws Exception {

			List<OAuthClient> oauthClients = authConfig.getOauthClients();
			if (!oauthClients.isEmpty()) {
				InMemoryClientDetailsServiceBuilder builder = clients.inMemory();
				for (OAuthClient oauthClient : oauthClients) {
					builder.withClient(oauthClient.getClientId())
							.authorizedGrantTypes(ConfigConstants.OAUTH_GRANT_TYPES)
							.scopes(ConfigConstants.OAUTH_SCOPES).secret(oauthClient.getClientSecret())
							.accessTokenValiditySeconds(oauthClient.getAccessTokenValidity())
							.refreshTokenValiditySeconds(oauthClient.getRefreshTokenValidity());
				}
			}
		}

		@Bean
		public TokenStore tokenStore() {

			return new InMemoryTokenStore();
		}

		@Override
		public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {

			endpoints.tokenStore(tokenStore()).authenticationManager(authenticationManager)
					.userDetailsService(userDetailsService);
		}

		@Override
		public void configure(AuthorizationServerSecurityConfigurer oauthServer) throws Exception {

			// oauthServer.allowFormAuthenticationForClients().realm(authConfig.getRealm());
			oauthServer.allowFormAuthenticationForClients();
		}
	}

	@Configuration
	@EnableResourceServer
	public static class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

		@Autowired
		private AuthenticationManager authenticationManager;

		@Override
		public void configure(HttpSecurity http) throws Exception {

			http.antMatcher(ConfigConstants.REST_API + "/**").sessionManagement().sessionFixation().none()
					.sessionCreationPolicy(SessionCreationPolicy.STATELESS).and().authorizeRequests()
					.antMatchers(ConfigConstants.REST_API + "/**")
					.access("!#oauth2.isOAuth() or #oauth2.hasScope('trust')").and().exceptionHandling()
					.authenticationEntryPoint(new Http403ForbiddenEntryPoint()).and()
					.addFilterBefore(new SecurityFilter(authenticationManager), BasicAuthenticationFilter.class);
		}
	}
}
