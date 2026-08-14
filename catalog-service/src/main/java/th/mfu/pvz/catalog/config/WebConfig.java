package th.mfu.pvz.catalog.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Lets the demo page served by order-service (port 8300) call this service from
 * the browser.
 *
 * Only browsers enforce CORS - curl and Postman never see this error, which is
 * why "it works in Postman" proves nothing about a web page.
 *
 * The demo page is also served on 8080/8081 in some setups, so those origins are
 * allowed too. We name the origins we trust rather than using "*".
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:8300", "http://127.0.0.1:8300")
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS");
    }
}
