package demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import demo.Service.UserService;
import demo.configuration.constants.ConfigConstants;
import demo.configuration.security.session.SessionContext;
import demo.dto.UserSessionData;
import demo.dto.request.LoginRequest;
import demo.dto.request.LogoutRequest;
import demo.dto.response.BaseResponse;
import demo.dto.response.LoginResponse;

@RestController
@RequestMapping(ConfigConstants.REST_API)
public class LoginController extends BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(LoginController.class);
	@Autowired
	private UserService userService;
	@Autowired
	private SessionContext sessionContext;

	@RequestMapping(value = "/user/login", method = RequestMethod.POST)
	public LoginResponse loginUser(@RequestBody LoginRequest request) {

		LOGGER.info("Rest API /user/login POST invoked with data '{}'", request);
		final long start = System.currentTimeMillis();
		LoginResponse response = new LoginResponse();
		try {
			UserSessionData userData = userService.loginUser(request.getUserName(), request.getPassword());
			LOGGER.info("Session created for member '{}'", userData.getUserName());
			sessionContext.setAttribute(ConfigConstants.USER_DATA_SESSION_KEY, userData);
			response.setIsAuthenticated(true);
			response.setUserData(userData);
			final long end = System.currentTimeMillis();
			LOGGER.info("Returning successful response for /user/login POST. Time: {}ms", end - start);
		} catch (UsernameNotFoundException e) {
			LOGGER.warn("UsernameNotFoundException occurred while serving Rest API /user/login POST.", e);
			sessionContext.invalidate();
			prepareFailureResponse(response, e);
		} catch (Exception e) {
			LOGGER.warn("Exception occurred while serving Rest API /user/login POST.", e);
			sessionContext.invalidate();
			prepareFailureResponse(response, e);
		}
		return response;
	}

	@RequestMapping(value = "/user/login", method = RequestMethod.GET)
	public LoginResponse loginStatus() {

		LOGGER.info("Rest API /user/login GET invoked");
		final long start = System.currentTimeMillis();
		LoginResponse response = new LoginResponse();
		try {
			// get user data from session
			UserSessionData userData = (UserSessionData) sessionContext
					.getAttribute(ConfigConstants.USER_DATA_SESSION_KEY);
			if (userData != null) {
				LOGGER.info("Member '{}' found in session", userData.getUserName());
				response.setIsAuthenticated(true);
				response.setUserData(userData);
			} else {
				LOGGER.info("Member not found in session");
				response.setIsAuthenticated(false);
			}
			final long end = System.currentTimeMillis();
			LOGGER.info("Returning successful response for /user/login GET. Time: {}ms", end - start);
		} catch (Exception e) {
			LOGGER.warn("Exception occurred while serving Rest API /user/login GET.");
			prepareFailureResponse(response, e);
		}
		return response;
	}

	@RequestMapping(value = "/user/logout", method = RequestMethod.POST)
	public BaseResponse logoutUser(@RequestBody LogoutRequest request) {

		LOGGER.info("Rest API /user/logout for user '{}' invoked", request.getUserName());
		final long start = System.currentTimeMillis();
		BaseResponse response = new BaseResponse();
		try {
			// just invalidate the session
			sessionContext.invalidate();
			final long end = System.currentTimeMillis();
			LOGGER.info("Returning successful response for /user/logout for user '{}'. Time: {}ms",
					request.getUserName(), end - start);
		} catch (Exception e) {
			LOGGER.warn("Exception occurred while serving Rest API /user/logout for user '{}'.", request.getUserName());
			prepareFailureResponse(response, e);
		}
		return response;
	}
}