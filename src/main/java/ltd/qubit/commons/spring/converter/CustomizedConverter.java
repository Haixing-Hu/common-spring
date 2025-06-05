////////////////////////////////////////////////////////////////////////////////
//
//    Copyright (c) 2022 - 2024.
//    Haixing Hu, Qubit Co. Ltd.
//
//    All rights reserved.
//
////////////////////////////////////////////////////////////////////////////////
package ltd.qubit.commons.spring.converter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * 用于标记自定义转换器的注解。
 *
 * @author 胡海星
 */
@Target({ ElementType.TYPE, ElementType.FIELD })
public @interface CustomizedConverter {
}