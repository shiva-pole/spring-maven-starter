package demo.controller.exception;

import demo.configuration.constants.ErrorCodes;
import demo.configuration.exception.DemoException;

public class UserNotFoundException extends RuntimeException implements DemoException {

	private static final long serialVersionUID = -1799135176600608486L;

	public UserNotFoundException() {

		super("User not found");
	}

	@Override
	public String getErrorCode() {

		return ErrorCodes.USER_NOT_FOUND_EXCEPTION;
	}
}
