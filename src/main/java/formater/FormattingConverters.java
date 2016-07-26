package formater;

import org.springframework.format.FormatterRegistry;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class FormattingConverters extends
		FormattingConversionServiceFactoryBean {
	@Override
	public void installFormatters(FormatterRegistry registry) {
		super.installFormatters(registry);
		// registry.addFormatterForFieldType(PeriodicTypeFormater.class,
		// new PeriodicTypeFormater());
	}
}
