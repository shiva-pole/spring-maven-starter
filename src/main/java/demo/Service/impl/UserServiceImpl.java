package demo.Service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import demo.Service.UserService;
import demo.controller.exception.UserNotFoundException;
import demo.dto.UserDTO;
import demo.dto.UserSessionData;

@Component
public class UserServiceImpl implements UserService {
	@Autowired
	private Environment env;

	@Override
	public UserSessionData loginUser(String userName, String password) {
		UserSessionData userSession = new UserSessionData();
		UserDTO currentUser = getCurrentUser(userName, password);
		if (currentUser != null) {
			userSession.setUserId(currentUser.getUserId());
			userSession.setUserName(currentUser.getUserName());
		} else {
			throw new UserNotFoundException();
		}
		return userSession;
	}

	private UserDTO getCurrentUser(String userName, String password) {
		List<UserDTO> allUsers = getRegisteredUsers();
		UserDTO currentUser = null;
		Optional<UserDTO> optional = allUsers.stream()
				.filter(u -> u.getUserName().equals(userName) && u.getPassword().equals(password)).findFirst();
		if (optional.isPresent()) {
			currentUser = optional.get();
		}
		return currentUser;

	}

	private List<UserDTO> getRegisteredUsers() {
		List<UserDTO> users = new ArrayList<UserDTO>();
		String[] userNames = env.getProperty("demo.users", "").split(",");

		for (String userName : userNames) {
			String userId = env.getProperty("demo.user." + userName + ".id");
			String password = env.getProperty("demo.user." + userName + ".password");
			UserDTO user = new UserDTO();
			user.setUserId(userId);
			user.setUserName(userName);
			user.setPassword(password);
			users.add(user);
		}
		return users;
	}

}
