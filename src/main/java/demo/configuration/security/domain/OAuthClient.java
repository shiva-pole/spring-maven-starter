package demo.configuration.security.domain;

import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.CollectionUtils;

public final class OAuthClient {

	private String clientId;
	private String clientSecret;
	private int accessTokenValidity;
	private int refreshTokenValidity;
	private boolean userEnabled;
	private List<GrantedAuthority> userAuthorities;
	private List<OAuthClientUser> users;

	public OAuthClient(String clientId, String clientSecret, int accessTokenValidity, int refreshTokenValidity,
			List<GrantedAuthority> userAuthorities) {

		this.clientId = clientId;
		this.clientSecret = clientSecret;
		this.accessTokenValidity = accessTokenValidity;
		this.refreshTokenValidity = refreshTokenValidity;
		this.userEnabled = !CollectionUtils.isEmpty(userAuthorities);
		this.userAuthorities = userAuthorities;
		users = new ArrayList<>();
	}

	public String getClientId() {

		return clientId;
	}

	public String getClientSecret() {

		return clientSecret;
	}

	public int getAccessTokenValidity() {

		return accessTokenValidity;
	}

	public int getRefreshTokenValidity() {

		return refreshTokenValidity;
	}

	public boolean isUserEnabled() {
		return userEnabled;
	}

	public List<GrantedAuthority> getUserAuthorities() {
		return userAuthorities;
	}

	public List<OAuthClientUser> getUsers() {

		return users;
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("OAuthClient [clientId=");
		builder.append(clientId);
		builder.append(", clientSecret=");
		builder.append(clientSecret);
		builder.append(", accessTokenValidity=");
		builder.append(accessTokenValidity);
		builder.append(", refreshTokenValidity=");
		builder.append(refreshTokenValidity);
		builder.append(", userEnabled=");
		builder.append(userEnabled);
		builder.append(", userAuthorities=");
		builder.append(userAuthorities);
		builder.append(", users=");
		builder.append(users);
		builder.append("]");
		return builder.toString();
	}
}
