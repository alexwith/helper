package me.hyfe.helper;

import me.hyfe.helper.command.functional.FunctionalCommandBuilder;
import me.hyfe.helper.command.functional.FunctionalSubCommandBuilder;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public final class Commands {

    public static FunctionalCommandBuilder<CommandSender> create(String... aliases) {
        return new FunctionalCommandBuilder<>(ConsoleCommandSender.class, null, "The default command.", aliases);
    }

    public static FunctionalSubCommandBuilder<CommandSender> createSub() {
        return new FunctionalSubCommandBuilder<>(ConsoleCommandSender.class, null, "Default description.", false);
    }
}