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
 * 一个自定义的转换服务工厂 Bean，用于注册所有自定义转换器。
 *
 * <p>在 Spring MVC 的 XML 配置中使用它，如下所示：
 * <pre><code>
 * &lt;context:component-scan
 *     base-package="ltd.qubit.commons.spring.converter, ltd.qubit.commons.controller" /&gt;
 * &lt;mvc:annotation-driven conversion-service="conversionService" /&gt;
 * &lt;bean id="conversionService"
 *    class="converter.ltd.qubit.commons.spring.CustomizedConversionServiceFactoryBean"/&gt;
 * </code></pre>
 *
 * @author 胡海星
 */
public class CustomizedConversionServiceFactoryBean extends
        FormattingConversionServiceFactoryBean {

  /**
   * 日志记录器。
   */
  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  /**
   * 注入的自定义转换器集合。
   */
  @Resource
  @CustomizedConverter
  private Set<Converter<?, ?>> customizedConverters;

  /**
   * 在设置所有 Bean 属性后由 BeanFactory 调用。
   *
   * <p>此方法注册自定义转换器并调用父类的 {@code afterPropertiesSet} 方法。
   */
  @Override
  public void afterPropertiesSet() {
    if (logger.isInfoEnabled()) {
      logger.info("注册自定义转换器: {}", getCustomizedConverterNames());
    }
    this.setConverters(customizedConverters);
    this.setRegisterDefaultFormatters(true);
    super.afterPropertiesSet();
  }

  /**
   * 获取所有自定义转换器的名称字符串，以逗号分隔。
   *
   * @return 包含所有自定义转换器名称的字符串。
   */
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