package com.neomccreations.common.inject;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotation element must
 * be started with the server/app itself. Class
 * path scanning may be used to find these
 * elements and instantiate them.
 *
 * @author Ben (OutdatedVersion)
 * @since May/20/2017 (2:18 PM)
 */
@Retention ( RetentionPolicy.RUNTIME )
@Target ( ElementType.TYPE )
public @interface ParallelStartup { }