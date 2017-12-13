package demo.configuration.security.domain;

import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

public class OAuthClientUser extends User implements UserDetails {

	private static final long serialVersionUID = 3495366004497959102L;
	private String clientId;
	private String userName;
	private String password;
	private List<GrantedAuthority> authorities;

	public OAuthClientUser(String clientId, String userName, String password, List<GrantedAuthority> authorities) {

		super(userName, password, true, true, true, true, authorities);
		this.clientId = clientId;
		this.userName = userName;
		this.password = password;
		this.authorities = authorities;
	}

	public String getClientId() {

		return clientId;
	}

	public OAuthClientUser clone() {

		return new OAuthClientUser(clientId, userName, password, authorities);
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("OAuthClientUser [userName=");
		builder.append(userName);
		builder.append(", password=");
		builder.append(password);
		builder.append(", authorities=");
		builder.append(authorities);
		builder.append("]");
		return builder.toString();
	}
}
