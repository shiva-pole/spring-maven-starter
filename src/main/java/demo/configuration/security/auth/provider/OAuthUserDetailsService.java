package demo.configuration.security.auth.provider;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import demo.configuration.security.auth.AuthConfig;
import demo.configuration.security.domain.OAuthClient;
import demo.configuration.security.domain.OAuthClientUser;
import demo.configuration.security.util.CryptoUtil;

@Component
public class OAuthUserDetailsService implements UserDetailsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(OAuthUserDetailsService.class);
	@Autowired
	private AuthConfig authConfig;

	@Override
	public OAuthClientUser loadUserByUsername(String userName) throws UsernameNotFoundException {

		LOGGER.debug("Getting OAuth User Details for {}", userName);
		List<OAuthClient> oauthClients = authConfig.getOauthClients();
		for (OAuthClient client : oauthClients) {
			for (OAuthClientUser user : client.getUsers()) {
				if (userName.equals(user.getUsername())) {
					LOGGER.info("Found OAuth User: ", user);
					return user.clone();
				}
			}
		}
		for (OAuthClient client : oauthClients) {
			if (client.isUserEnabled()) {
				String memberId = userName;
				String password = CryptoUtil.encrypt(memberId, client.getClientSecret());
				return new OAuthClientUser(client.getClientId(), memberId, password, client.getUserAuthorities());
			}
		}
		return null;
	}
}
