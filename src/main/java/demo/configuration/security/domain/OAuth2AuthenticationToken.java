package demo.configuration.security.domain;

import java.util.Collection;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

public class OAuth2AuthenticationToken extends UsernamePasswordAuthenticationToken {

	private static final long serialVersionUID = 2401736884683020884L;

	public OAuth2AuthenticationToken(String userName, String password, Collection<GrantedAuthority> authorities) {

		super(userName, password, authorities);
	}
}
