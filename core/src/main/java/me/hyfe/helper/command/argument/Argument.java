package me.hyfe.helper.command.argument;

import com.google.common.reflect.TypeToken;
import me.hyfe.helper.Commands;
import me.hyfe.helper.command.CommandInterruptException;

import java.util.Optional;

public interface Argument {

    int index();

    Optional<String> value();

    default <T> Optional<T> parse(ArgumentParser<T> parser) {
        return parser.parse(this);
    }

    default <T> T parseOrFail(ArgumentParser<T> parser) throws CommandInterruptException {
        return parser.parseOrFail(this);
    }

    default <T> Optional<T> parse(TypeToken<T> type) {
        return Commands.parserRegistry().find(type).flatMap(this::parse);
    }

    default <T> T parseOrFail(TypeToken<T> type) throws CommandInterruptException {
        ArgumentParser<T> parser = Commands.parserRegistry().find(type).orElse(null);
        if (parser == null) {
            throw new RuntimeException("Unable to find ArgumentParser for " + type);
        }
        return parseOrFail(parser);
    }

    default <T> Optional<T> parse(Class<T> clazz) {
        return Commands.parserRegistry().find(clazz).flatMap(this::parse);
    }

    default <T> T parseOrFail(Class<T> clazz) throws CommandInterruptException {
        ArgumentParser<T> parser = Commands.parserRegistry().find(clazz).orElse(null);
        if (parser == null) {
            throw new RuntimeException("Unable to find ArgumentParser for " + clazz);
        }
        return parseOrFail(parser);
    }

    /**
     * Gets if the argument is present
     *
     * @return true if present
     */
    boolean isPresent();

    /**
     * Asserts that the permission is present
     */
    default void assertPresent() throws CommandInterruptException {
        CommandInterruptException.makeAssertion(isPresent(), "&cArgument at index " + index() + " is not present.");
    }
}