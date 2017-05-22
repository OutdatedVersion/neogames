package com.neomccreations.common.database.mutate;

/**
 * Combines both {@link To} and {@link From} tasks.
 *
 * @author Ben (OutdatedVersion)
 * @since May/21/2017 (9:03 PM)
 */
public interface Mutator<V> extends To<V>, From<V> { }
