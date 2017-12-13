package demo.configuration.constants;

/**
 * The Constants for Configuration.
 */
public interface ConfigConstants {

	String DEV_PROFILE = "dev";
	String PROD_PROFILE = "prod";
	/** The Constant DEFAULT_ENCODING. */
	String DEFAULT_ENCODING = "UTF-8";
	String MESSAGES_BASENAME = "messages";
	String USER_DATA_SESSION_KEY = "user-data";
	String[] OAUTH_GRANT_TYPES = { "password", "refresh_token" };
	String[] OAUTH_SCOPES = { "trust" };
	String AUTHORITY_ADMIN = "ROLE_ADMIN";
	String AUTHORITY_PUBLIC = "ROLE_PUBLIC";
	String REST_API = "/rest/restapi";
}
