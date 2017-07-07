package net.neogamesmc.bungee.distribution;

import net.md_5.bungee.api.config.ServerInfo;
import net.neogamesmc.bungee.connection.DataHandler;

import java.util.function.BiFunction;

/**
 * @author Ben (OutdatedVersion)
 * @since Jul/06/2017 (10:25 PM)
 */
public interface PlayerDistribution extends BiFunction<String, DataHandler, ServerInfo> { }
