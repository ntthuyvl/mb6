package birt.spring.example;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.BeanNameViewResolver;
import birt.spring.core.BirtEngineFactory;
import birt.spring.core.BirtView;

@EnableWebMvc
@ComponentScan({ "birt.spring.core", "birt.spring.example" })
@ImportResource("/WEB-INF/dogstore-servlet.xml")
@Configuration
public class BirtWebConfiguration extends WebMvcConfigurerAdapter {
	/*
	 * @Override public void addViewControllers(ViewControllerRegistry registry)
	 * { registry.addViewController("/reports").setViewName("birtView");
	 * 
	 * }
	 */
	@Bean
	public BirtView birtView() {
		BirtView bv = new BirtView();
		bv.setBirtEngine(this.engine().getObject());
		return bv;
	}

	@Bean
	public BeanNameViewResolver beanNameResolver() {
		BeanNameViewResolver br = new BeanNameViewResolver();
		return br;
	}

	@Bean
	protected BirtEngineFactory engine() {
		BirtEngineFactory factory = new BirtEngineFactory();
		// Enable BIRT Engine Logging
		// factory.setLogLevel( Level.FINEST);
		// factory.setLogDirectory( new FileSystemResource("c:/temp"));

		return factory;
	}

}