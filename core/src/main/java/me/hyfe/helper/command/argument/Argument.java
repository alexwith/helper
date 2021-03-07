package me.hyfe.helper.command.argument;

import com.google.common.reflect.TypeToken;
import me.hyfe.helper.Commands;
import me.hyfe.helper.command.CommandInterruptException;

public interface Argument {

    int index();

    String value();

    default <T> T parse(ArgumentParser<T> parser) {
        return parser.parse(this);
    }

    default <T> T parseOrFail(ArgumentParser<T> parser) throws CommandInterruptException {
        return parser.parseOrFail(this);
    }

    default <T> T parse(TypeToken<T> type) {
        return this.parse(Commands.parserRegistry().find(type));
    }

    default <T> T parseOrFail(TypeToken<T> type) throws CommandInterruptException {
        ArgumentParser<T> parser = Commands.parserRegistry().find(type);
        if (parser == null) {
            throw new RuntimeException("Unable to find ArgumentParser for " + type);
        }
        return parseOrFail(parser);
    }

    default <T> T parse(Class<T> clazz) {
        return this.parse(Commands.parserRegistry().find(clazz));
    }

    default <T> T parseOrFail(Class<T> clazz) throws CommandInterruptException {
        ArgumentParser<T> parser = Commands.parserRegistry().find(clazz);
        if (parser == null) {
            throw new RuntimeException("Unable to find ArgumentParser for " + clazz);
        }
        return parseOrFail(parser);
    }

    boolean isPresent();

    default void assertPresent() throws CommandInterruptException {
        CommandInterruptException.makeAssertion(isPresent(), "&cArgument at index " + index() + " is not present.");
    }
}