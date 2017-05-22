package com.neomccreations.common.database.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the annotated field
 * should use the name it has for looking
 * into the {@link java.sql.ResultSet}'s data.
 *
 * @author Ben (OutdatedVersion)
 * @since May/20/2017 (2:12 AM)
 *
 * @see Column
 */
@Retention ( RetentionPolicy.RUNTIME )
@Target ( ElementType.FIELD )
public @interface InheritColumn
{ }
