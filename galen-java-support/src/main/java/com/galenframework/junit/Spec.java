package com.galenframework.junit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * The Galen specification file.
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Spec {
    String value();
}
