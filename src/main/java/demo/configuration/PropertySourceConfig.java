package demo.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@Configuration
@PropertySource({ "classpath:common.properties" })
public class PropertySourceConfig {

	static @Bean public PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {

		return new PropertySourcesPlaceholderConfigurer();
	}

	/**
	 * Properties to support the 'dev' mode of operation.
	 */
	@Configuration
	@Profile("dev")
	@PropertySource("classpath:env-dev.properties")
	static class Dev {
	}

	/**
	 * Properties to support the 'production' mode of operation.
	 */
	@Configuration
	@Profile("prod")
	@PropertySource("classpath:env-prod.properties")
	static class Production {
	}
}