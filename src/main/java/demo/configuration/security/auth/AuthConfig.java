package demo.configuration.security.auth;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import demo.configuration.security.domain.OAuthClient;
import demo.configuration.security.domain.OAuthClientUser;

@Component
public class AuthConfig implements InitializingBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthConfig.class);
	@Autowired
	private Environment env;
	private String realm;
	private String[] securityBypassUrls;
	private List<OAuthClient> oauthClients;

	@Override
	public void afterPropertiesSet() throws Exception {

		// Security Realm
		realm = env.getProperty("security.realm");
		// Web Resources - No Authentication
		if (env.getProperty("security.bypass.urls") != null) {
			securityBypassUrls = env.getProperty("security.bypass.urls").split(",");
		}
		// OAuth Security Config
		oauthClients = new ArrayList<>();
		// clients
		String[] clientIds = env.getProperty("security.oauth.clients", "").split(",");
		for (String clientId : clientIds) {
			if (clientId.isEmpty()
					|| !Boolean.parseBoolean(env.getProperty("security.oauth.client." + clientId + ".enabled"))) {
				continue;
			}
			String clientSecret = env.getProperty("security.oauth.client." + clientId + ".secret");
			int accessTokenValidity = Integer
					.parseInt(env.getProperty("security.oauth.client." + clientId + ".access-token-validity"));
			int refreshTokenValidity = Integer
					.parseInt(env.getProperty("security.oauth.client." + clientId + ".refresh-token-validity"));
			String investorAuthorities = env.getProperty("security.oauth.client." + clientId + ".user-authorities");
			List<GrantedAuthority> investorGrantedAuths = new ArrayList<>();
			if (!StringUtils.isEmpty(investorAuthorities)) {
				for (String grantedAuth : investorAuthorities.split(",")) {
					investorGrantedAuths.add(new SimpleGrantedAuthority(grantedAuth));
				}
			}
			OAuthClient oauthClient = new OAuthClient(clientId, clientSecret, accessTokenValidity, refreshTokenValidity,
					investorGrantedAuths);
			// users
			String[] users = env.getProperty("security.oauth.client." + clientId + ".users").split(",");
			for (String user : users) {
				String password = env.getProperty("security.oauth.client." + clientId + ".user." + user + ".password");
				String[] authorities = env
						.getProperty("security.oauth.client." + clientId + ".user." + user + ".authorities").split(",");
				List<GrantedAuthority> grantedAuths = new ArrayList<>();
				for (String grantedAuth : authorities) {
					grantedAuths.add(new SimpleGrantedAuthority(grantedAuth));
				}
				oauthClient.getUsers().add(new OAuthClientUser(clientId, user, password, grantedAuths));
			}
			oauthClients.add(oauthClient);
		}
		LOGGER.info("Security Config: oauth: {}", oauthClients);
	}

	public String getRealm() {

		return realm;
	}

	public String[] getSecurityBypassUrls() {

		return securityBypassUrls;
	}

	public List<OAuthClient> getOauthClients() {

		return oauthClients;
	}

	public OAuthClient getOauthClient(String clientId) {

		for (OAuthClient client : oauthClients) {
			if (client.getClientId().equals(clientId)) {
				return client;
			}
		}
		return null;
	}
}
