package demo.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import demo.configuration.constants.ConfigConstants;
import demo.dto.response.GreetingResponse;

@RestController
@RequestMapping(ConfigConstants.REST_API)
public class AuthController extends BaseController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AuthController.class);

	@RequestMapping(value = "/greeting", method = RequestMethod.GET)
	@PreAuthorize("hasAuthority('" + ConfigConstants.AUTHORITY_ADMIN + "')")
	public GreetingResponse greeting(@RequestParam(value = "name", defaultValue = "World") String name) {
		LOGGER.info("Rest API /greeting invoked");
		final long start = System.currentTimeMillis();
		GreetingResponse response = new GreetingResponse();
		try {
			response.setMessage(name);
			final long end = System.currentTimeMillis();
			LOGGER.info("Returning successful response for /greeting. Time: {}ms", end - start);
		} catch (Exception e) {
			LOGGER.warn("Exception occurred while serving Rest API /greeting.");
			prepareFailureResponse(response, e);
		}
		return response;
	}
}