package com.devbliss.gpullr.util;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Annotation to inject {@link org.slf4j.Logger} with the class name.
 */
@Retention(RUNTIME)
@Target(FIELD)
@Documented
public @interface Log {

}
