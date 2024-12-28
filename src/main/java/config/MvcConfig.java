package config;

import controller.RegisterRequestValidator;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.validation.Validator;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.ViewResolverRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * class         : MvcConfig
 * date          : 2024-12-26
 * description   : Spring MVC 설정을 구성하는 클래스.
 */
@Configuration
@EnableWebMvc
public class MvcConfig implements WebMvcConfigurer {


	/**
	 * method        : getValidator
	 * date          : 2024-12-26
	 * return        : Validator
	 * description   : RegisterRequestValidator를 Validator로 등록. 스프링 MVC에서 폼 데이터 검증에 사용.
	 */
	@Override
	public Validator getValidator() {
		return new RegisterRequestValidator();
	}

	@Override
	public void configureDefaultServletHandling(
			DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
		registry.jsp("/WEB-INF/view/", ".jsp");
	}

	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
		registry.addViewController("/main").setViewName("main");
	}

	@Bean
	public MessageSource messageSource() {
		ResourceBundleMessageSource ms = new ResourceBundleMessageSource();
		ms.setBasenames("message.label");
		ms.setDefaultEncoding("UTF-8");
		return ms;
	}

}
