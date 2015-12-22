package com.galenframework.junit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A list of tags for spec sections to be excluded from the filtered group.
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Exclude {
    /**
     * The list of tags.
     * @return a list of tags.
     */
    String[] value();
}
