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
 * The annotation for the customized converters.
 *
 * @author Haixing Hu
 */
@Target({ ElementType.TYPE, ElementType.FIELD })
public @interface CustomizedConverter {
}
