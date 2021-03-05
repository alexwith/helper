package me.hyfe.helper.command.argument;

import me.hyfe.helper.command.CommandInterruptException;

import java.util.Optional;
import java.util.function.Function;

public interface ArgumentParser<T> {

    static <T> ArgumentParser<T> of(Function<String, Optional<T>> parseFunction) {
        return parseFunction::apply;
    }

    static <T> ArgumentParser<T> of(Function<String, Optional<T>> parseFunction, Function<String, CommandInterruptException> generateExceptionFunction) {
        return new ArgumentParser<T>() {

            @Override
            public Optional<T> parse(String t) {
                return parseFunction.apply(t);
            }

            @Override
            public CommandInterruptException generateException(String string) {
                return generateExceptionFunction.apply(string);
            }
        };
    }

    Optional<T> parse(String s);

    default CommandInterruptException generateException(String s) {
        return new CommandInterruptException("&cUnable to parse argument: " + s);
    }

    default CommandInterruptException generateException(int missingArgumentIndex) {
        return new CommandInterruptException("&cArgument at index " + missingArgumentIndex + " is missing.");
    }

    default T parseOrFail(String s) throws CommandInterruptException {
        Optional<T> ret = parse(s);
        if (!ret.isPresent()) {
            throw generateException(s);
        }
        return ret.get();
    }

    default Optional<T> parse(Argument argument) {
        return argument.value().flatMap(this::parse);
    }

    default T parseOrFail(Argument argument) throws CommandInterruptException {
        Optional<String> value = argument.value();
        if (!value.isPresent()) {
            throw generateException(argument.index());
        }
        return parseOrFail(value.get());
    }

    default ArgumentParser<T> thenTry(ArgumentParser<T> other) {
        ArgumentParser<T> first = this;
        return t -> {
            Optional<T> ret = first.parse(t);
            return ret.isPresent() ? ret : other.parse(t);
        };
    }
}