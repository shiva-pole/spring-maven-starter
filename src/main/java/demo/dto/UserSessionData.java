package demo.dto;

import java.io.Serializable;

public class UserSessionData implements Serializable {

	private static final long serialVersionUID = -689181504792581659L;
	private Boolean isPubliclyAuthenticated = Boolean.FALSE;
	private String userId;
	private String userName;

	public Boolean getIsPubliclyAuthenticated() {

		return isPubliclyAuthenticated;
	}

	public void setIsPubliclyAuthenticated(Boolean isPubliclyAuthenticated) {

		this.isPubliclyAuthenticated = isPubliclyAuthenticated;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

}
