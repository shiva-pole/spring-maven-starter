package demo.controller.exception;

import demo.configuration.constants.ErrorCodes;
import demo.configuration.exception.DemoException;

public class InputValidationException extends RuntimeException implements DemoException {

	private static final long serialVersionUID = 3173726815256007810L;

	public InputValidationException() {

		super("Input Validation failed for REST API input");
	}

	@Override
	public String getErrorCode() {

		return ErrorCodes.INPUT_VALIDATION_EXCEPTION;
	}
}
