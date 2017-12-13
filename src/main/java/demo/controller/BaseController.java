package demo.controller;

import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import demo.configuration.constants.ErrorCodes;
import demo.configuration.exception.DemoException;
import demo.controller.exception.InputValidationException;
import demo.controller.util.ViewMapper;
import demo.dto.response.BaseResponse;

public class BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(BaseController.class);
	@Autowired
	protected ViewMapper viewMapper;
	private ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
	private Validator validator = factory.getValidator();

	protected void prepareFailureResponse(BaseResponse baseResponse, Exception e) {

		baseResponse.setSuccessFlag(false);
		if (e instanceof DemoException) {
			LOGGER.warn("Expected Exception occurred while serving Rest API ", e);
			baseResponse.setErrorCode(((DemoException) e).getErrorCode());
			baseResponse.setErrorMsg(e.getMessage());
		} else {
			LOGGER.error("Exception occurred while serving Rest API ", e);
			// don't send error message in response for non investor portal
			// exceptions
			baseResponse.setErrorCode(ErrorCodes.GENERIC_EXCEPTION);
			baseResponse.setErrorMsg("Error processing request.");
		}
	}

	protected ResponseEntity<byte[]> prepareFileFailureResponse(Exception e) {

		if (e instanceof DemoException) {
			LOGGER.warn("Expected Exception occurred while serving Rest API ", e);
		} else {
			LOGGER.error("Exception occurred while serving Rest API ", e);
		}
		return new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT);
	}

	protected void validate(Object obj) {

		Set<ConstraintViolation<Object>> violations = validator.validate(obj);
		LOGGER.debug("Validation of {} result: {}", obj, violations);
		if (violations != null && !violations.isEmpty()) {
			LOGGER.info("Validation errors for {} are: {}", obj, violations);
			throw new InputValidationException();
		}
	}
}
