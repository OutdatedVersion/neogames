package com.neomccreations.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Indicates that the annotation class
 * purely holds data. Primarily used
 * by our configurations.
 *
 * @author Ben (OutdatedVersion)
 * @since May/17/2017 (9:55 PM)
 */
@Target ( ElementType.TYPE )
public @interface Data { }
