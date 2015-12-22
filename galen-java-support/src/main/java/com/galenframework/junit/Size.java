package com.galenframework.junit;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Dimensions of browser window.
 */
@Retention(RUNTIME)
@Target(TYPE)
public @interface Size {
    /**
     * The height of the browser window.
     * @return the height of the browser window.
     */
    int height();

    /**
     * The width of the browser window.
     * @return the width of the browser window.
     */
    int width();
}
