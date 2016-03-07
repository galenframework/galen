package com.galenframework.junit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A URL of page for Galen to test on
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Url {
    String value();
}
