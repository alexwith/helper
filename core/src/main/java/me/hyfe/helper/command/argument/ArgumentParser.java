package me.hyfe.helper.command.argument;

import me.hyfe.helper.command.CommandInterruptException;

import java.util.Optional;
import java.util.function.Function;

public interface ArgumentParser<T> {

    static <T> ArgumentParser<T> of(Function<String, T> parseFunction) {
        return parseFunction::apply;
    }

    static <T> ArgumentParser<T> of(Function<String, T> parseFunction, Function<String, CommandInterruptException> generateExceptionFunction) {
        return new ArgumentParser<T>() {

            @Override
            public T parse(String t) {
                return parseFunction.apply(t);
            }

            @Override
            public CommandInterruptException generateException(String string) {
                return generateExceptionFunction.apply(string);
            }
        };
    }

    T parse(String s);

    default CommandInterruptException generateException(String s) {
        return new CommandInterruptException("&cUnable to parse argument: " + s);
    }

    default CommandInterruptException generateException(int missingArgumentIndex) {
        return new CommandInterruptException("&cArgument at index " + missingArgumentIndex + " is missing.");
    }

    default T parseOrFail(String s) throws CommandInterruptException {
        T ret = parse(s);
        if (ret == null) {
            throw generateException(s);
        }
        return ret;
    }

    default T parse(Argument argument) {
        return this.parse(argument.value());
    }

    default T parseOrFail(Argument argument) throws CommandInterruptException {
        String value = argument.value();
        if (value == null) {
            throw generateException(argument.index());
        }
        return parseOrFail(value);
    }

    default ArgumentParser<T> thenTry(ArgumentParser<T> other) {
        ArgumentParser<T> first = this;
        return t -> {
            T ret = first.parse(t);
            return ret == null ? other.parse(t) : ret;
        };
    }
}