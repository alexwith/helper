package me.hyfe.helper;

import me.hyfe.helper.command.argument.ArgumentParserRegistry;
import me.hyfe.helper.command.argument.SimpleParserRegistry;
import me.hyfe.helper.command.functional.FunctionalCommandBuilder;
import me.hyfe.helper.uuid.FastUUID;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public final class Commands {
    private static final ArgumentParserRegistry PARSER_REGISTRY;

    public static ArgumentParserRegistry parserRegistry() {
        return PARSER_REGISTRY;
    }

    static {
        PARSER_REGISTRY = new SimpleParserRegistry();

        PARSER_REGISTRY.register(String.class, Optional::of);
        PARSER_REGISTRY.register(Integer.class, (arg) -> Optional.of(Integer.valueOf(arg)));
        PARSER_REGISTRY.register(Long.class, (arg) -> Optional.of(Long.valueOf(arg)));
        PARSER_REGISTRY.register(Double.class, (arg) -> Optional.of(Double.valueOf(arg)));
        PARSER_REGISTRY.register(Boolean.class, arg -> arg.equalsIgnoreCase("true") ? Optional.of(true) : arg.equalsIgnoreCase("false") ? Optional.of(false) : Optional.empty());
        PARSER_REGISTRY.register(UUID.class, arg -> {
            try {
                return Optional.of(FastUUID.parse(arg));
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        });
        PARSER_REGISTRY.register(Player.class, (arg) -> {
            Supplier<Player> parser = () -> {
                try {
                    return Bukkit.getPlayer(FastUUID.parse(arg));
                } catch (IllegalArgumentException e) {
                    return Bukkit.getPlayerExact(arg);
                }
            };
            return Optional.ofNullable(parser.get());
        });
        PARSER_REGISTRY.register(OfflinePlayer.class, arg -> {
            Supplier<OfflinePlayer> parser = () -> {
                try {
                    return Bukkit.getOfflinePlayer(FastUUID.parse(arg));
                } catch (IllegalArgumentException e) {
                    return Bukkit.getOfflinePlayer(arg);
                }
            };
            return Optional.ofNullable(parser.get());
        });
        PARSER_REGISTRY.register(World.class, Helper::world);
    }

    public static FunctionalCommandBuilder<CommandSender> create() {
        return FunctionalCommandBuilder.newBuilder();
    }
}