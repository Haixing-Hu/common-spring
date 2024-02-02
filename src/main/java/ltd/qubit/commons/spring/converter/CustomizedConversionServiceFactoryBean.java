////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.spring.converter;

import java.util.Set;

import jakarta.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.support.FormattingConversionServiceFactoryBean;

/**
 * A customized conversion service factory bean used to register all customized
 * converters.
 *
 * <p>Use it in your XML configuration of spring MVC as follows:
 * <pre><code>
 * &lt;context:component-scan
 *     base-package="ltd.qubit.commons.spring.converter, ltd.qubit.commons.controller" /&gt;
 * &lt;mvc:annotation-driven conversion-service="conversionService" /&gt;
 * &lt;bean id="conversionService"
 *    class="ltd.qubit.commons.spring.converter.CustomizedConversionServiceFactoryBean"/&gt;
 * </code></pre>
 *
 * @author Haixing Hu
 */
public class CustomizedConversionServiceFactoryBean extends
        FormattingConversionServiceFactoryBean {

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  @Resource
  @CustomizedConverter
  private Set<Converter<?, ?>> customizedConverters;

  public void afterPropertiesSet() {
    if (logger.isInfoEnabled()) {
      logger.info("Register customized converters: {}", getCustomizedConverterNames());
    }
    this.setConverters(customizedConverters);
    this.setRegisterDefaultFormatters(true);
    super.afterPropertiesSet();
  }

  private String getCustomizedConverterNames() {
    final StringBuilder builder = new StringBuilder();
    for (final Converter<?, ?> converter : customizedConverters) {
      final String name = converter.getClass().getSimpleName();
      if (builder.length() > 0) {
        builder.append(", ");
      }
      builder.append(name);
    }
    return builder.toString();
  }

}
