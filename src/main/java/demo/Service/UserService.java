package demo.Service;

import demo.dto.UserSessionData;

public interface UserService {
	UserSessionData loginUser(String userName, String password);
}
