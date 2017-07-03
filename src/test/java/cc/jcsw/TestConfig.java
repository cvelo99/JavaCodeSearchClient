package cc.jcsw;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;

import cc.jcsw.main.Main;

/**
 * Configuration for Spring Boot integration tests. Ignores the Main class so as not to run the application.
 * 
 * @author Chris Carcel
 *
 */
@Configuration
@ComponentScan(value = "cc.jcsw", excludeFilters = @ComponentScan.Filter(value = Main.class, type = FilterType.ASSIGNABLE_TYPE))
@EnableAutoConfiguration
public class TestConfig {

}
