package demo.configuration.security.auth.provider;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import demo.configuration.security.auth.AuthConfig;
import demo.configuration.security.domain.OAuth2AuthenticationToken;
import demo.configuration.security.domain.OAuthClient;
import demo.configuration.security.domain.OAuthClientUser;
import demo.configuration.security.domain.SessionAuthenticationToken;

@Component
public class DemoAuthProvider implements AuthenticationProvider {

	private static final Logger LOGGER = LoggerFactory.getLogger(DemoAuthProvider.class);
	@Autowired
	private AuthConfig authConfig;
	@Autowired
	private OAuthUserDetailsService oauthUserDetailsService;
	private static final List<Class<? extends UsernamePasswordAuthenticationToken>> SUPPORTED_CLASSES = Arrays
			.asList(SessionAuthenticationToken.class, UsernamePasswordAuthenticationToken.class);

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		LOGGER.debug("Authentication request received for {}", authentication.getName());
		if (authentication instanceof SessionAuthenticationToken) {
			// Session Authentication
			SessionAuthenticationToken authToken = (SessionAuthenticationToken) authentication;
			return authenticateSessionToken(authToken);
		} else if (authentication instanceof UsernamePasswordAuthenticationToken) {
			// The OAuth2 Authentication
			UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) authentication;
			return authenticateOAuthToken(authToken);
		}
		return null;
	}

	private Authentication authenticateSessionToken(SessionAuthenticationToken authToken) {

		// nothing to validate here.. just log at debug level
		if (authToken.getPrincipal() != null) {
			LOGGER.debug("Session Authentication successful for user {}", authToken.getPrincipal());
			return authToken.copy();
		}
		LOGGER.debug("Session Authentication failed for null user {}");
		throw new BadCredentialsException("Invalid session token.");
	}

	private Authentication authenticateOAuthToken(UsernamePasswordAuthenticationToken authToken) {

		// get client id from auth token details
		OAuthClient client = null;
		if (authToken.getDetails() != null && authToken.getDetails() instanceof Map<?, ?>) {
			Map<?, ?> details = (Map<?, ?>) authToken.getDetails();
			if (details.get("client_id") != null) {
				client = authConfig.getOauthClient(details.get("client_id").toString());
			}
		}
		// get username and password from auth token
		String userName = authToken.getPrincipal() != null ? authToken.getPrincipal().toString() : null;
		String password = authToken.getCredentials() != null ? authToken.getCredentials().toString() : null;
		if (client == null || userName == null || password == null) {
			LOGGER.warn("OAuth Authentication failed: Either clientId or userName is not provided: {}, {}",
					client.getClientId(), userName);
			throw new BadCredentialsException("Invalid clientId or userName or password.");
		}
		LOGGER.info("OAuth Authentication received: clientId: {}, userName: {}", client.getClientId(), userName);
		OAuthClientUser user = oauthUserDetailsService.loadUserByUsername(userName);
		// remove the OR condition in password validation for encrypted password
		// check
		if (user != null && client.getClientId().equals(user.getClientId())
				&& password.trim().equals(user.getPassword())) {
			LOGGER.info("OAuth Authentication successful for clientId: {}, userName: {}. Authorities: {}",
					client.getClientId(), userName, user.getAuthorities());
			return new OAuth2AuthenticationToken(user.getUsername(), user.getPassword(), user.getAuthorities());
		}
		LOGGER.warn("OAuth Authentication failed for clientId: {}, userName: {}", client.getClientId(), userName);
		throw new BadCredentialsException("Invalid clientId or userName or password.");
	}

	@Override
	public boolean supports(Class<?> authentication) {

		return SUPPORTED_CLASSES.contains(authentication);
	}
}
