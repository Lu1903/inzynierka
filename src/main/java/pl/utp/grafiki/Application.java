package pl.utp.grafiki;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.vaadin.flow.spring.annotation.EnableVaadin;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@EntityScan(basePackages = "pl.utp.grafiki.domain")
@EnableJpaRepositories("pl.utp.grafiki.repository")
@EnableAutoConfiguration
@ComponentScan
@ServletComponentScan
@EnableVaadin({"pl.utp.grafiki", "pl.utp.grafiki.views.admin", "pl.utp.grafiki.views", "pl.utp.grafiki.views.superuser", "pl.utp.grafiki.views.user"})
public class Application {

    public static void main(String[] args) {
        //SpringApplication.run(Application.class, args);
    	SpringApplicationBuilder builder = new SpringApplicationBuilder(Application.class);

        builder.headless(false);
        builder.run(args);
    }

}
