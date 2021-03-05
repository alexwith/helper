package me.hyfe.helper.command.functional;

import me.hyfe.helper.command.Command;
import me.hyfe.helper.command.context.CommandContext;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.function.Predicate;

public interface FunctionalCommandBuilder<T extends CommandSender> {
    String DEFAULT_NOT_OP_MESSAGE = "&cOnly server operators are able to use this command.";
    String DEFAULT_NOT_PLAYER_MESSAGE = "&cOnly players are able to use this command.";
    String DEFAULT_NOT_CONSOLE_MESSAGE = "&cThis command is only available through the server console.";
    String DEFAULT_INVALID_USAGE_MESSAGE = "&cInvalid usage. Try: {usage}.";
    String DEFAULT_INVALID_ARGUMENT_MESSAGE = "&cInvalid argument '{arg}' at index {index}.";
    String DEFAULT_INVALID_SENDER_MESSAGE = "&cYou are not able to use this command.";

    static FunctionalCommandBuilder<CommandSender> newBuilder() {
        return new FunctionalCommandBuilderImpl<>();
    }

    FunctionalCommandBuilder<T> description(String description);

    FunctionalCommandBuilder<T> assertFunction(Predicate<? super CommandContext<? extends T>> test);

    default FunctionalCommandBuilder<T> assertPermission(String permission) {
        return assertPermission(permission, null);
    }

    FunctionalCommandBuilder<T> assertPermission(String permission, String failureMessage);

    default FunctionalCommandBuilder<T> assertOp() {
        return assertOp(DEFAULT_NOT_OP_MESSAGE);
    }

    FunctionalCommandBuilder<T> assertOp(String failureMessage);

    default FunctionalCommandBuilder<Player> assertPlayer() {
        return assertPlayer(DEFAULT_NOT_PLAYER_MESSAGE);
    }

    FunctionalCommandBuilder<Player> assertPlayer(String failureMessage);

    default FunctionalCommandBuilder<ConsoleCommandSender> assertConsole() {
        return assertConsole(DEFAULT_NOT_CONSOLE_MESSAGE);
    }

    FunctionalCommandBuilder<ConsoleCommandSender> assertConsole(String failureMessage);

    default FunctionalCommandBuilder<T> assertUsage(String usage) {
        return assertUsage(usage, DEFAULT_INVALID_USAGE_MESSAGE);
    }

    FunctionalCommandBuilder<T> assertUsage(String usage, String failureMessage);

    default FunctionalCommandBuilder<T> assertArgument(int index, Predicate<String> test) {
        return assertArgument(index, test, DEFAULT_INVALID_ARGUMENT_MESSAGE);
    }

    FunctionalCommandBuilder<T> assertArgument(int index, Predicate<String> test, String failureMessage);

    default FunctionalCommandBuilder<T> assertSender(Predicate<T> test) {
        return assertSender(test, DEFAULT_INVALID_SENDER_MESSAGE);
    }

    FunctionalCommandBuilder<T> assertSender(Predicate<T> test, String failureMessage);

    Command handler(FunctionalCommandHandler<T> handler);
}