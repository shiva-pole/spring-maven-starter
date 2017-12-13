package demo.dto.response;

import demo.dto.UserSessionData;

public class LoginResponse extends BaseResponse {
	private Boolean isAuthenticated;
	private UserSessionData userData;

	public Boolean getIsAuthenticated() {
		return isAuthenticated;
	}

	public void setIsAuthenticated(Boolean isAuthenticated) {
		this.isAuthenticated = isAuthenticated;
	}

	public UserSessionData getUserData() {
		return userData;
	}

	public void setUserData(UserSessionData userData) {
		this.userData = userData;
	}

}