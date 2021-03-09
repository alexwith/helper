package me.hyfe.helper;

import me.hyfe.helper.oldcommand.argument.ArgumentParserRegistry;
import me.hyfe.helper.oldcommand.argument.SimpleParserRegistry;
import me.hyfe.helper.oldcommand.functional.FunctionalCommandBuilder;
import me.hyfe.helper.uuid.FastUUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public final class Commands {
    private static final ArgumentParserRegistry PARSER_REGISTRY;

    public static ArgumentParserRegistry parserRegistry() {
        return PARSER_REGISTRY;
    }

    static {
        PARSER_REGISTRY = new SimpleParserRegistry();

        PARSER_REGISTRY.register(String.class, (arg) -> arg);
        PARSER_REGISTRY.register(Integer.class, Integer::parseInt);
        PARSER_REGISTRY.register(Long.class, Long::parseLong);
        PARSER_REGISTRY.register(Double.class, Double::parseDouble);
        PARSER_REGISTRY.register(Boolean.class, arg -> arg.equalsIgnoreCase("true"));
        PARSER_REGISTRY.register(UUID.class, FastUUID::parse);
        PARSER_REGISTRY.register(Player.class, Bukkit::getPlayerExact);
        PARSER_REGISTRY.register(OfflinePlayer.class, Bukkit::getOfflinePlayer);
        PARSER_REGISTRY.register(World.class, Helper::world);
    }

    public static FunctionalCommandBuilder<CommandSender> create() {
        return FunctionalCommandBuilder.newBuilder();
    }
}